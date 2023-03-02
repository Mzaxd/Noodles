package com.mzaxd.noodles.websocket;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.ChannelType;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mzaxd.noodles.domain.entity.Container;
import com.mzaxd.noodles.domain.entity.SshLink;
import com.mzaxd.noodles.domain.ssh.SshModel;
import com.mzaxd.noodles.domain.ssh.SshMessage;
import com.mzaxd.noodles.service.ContainerService;
import com.mzaxd.noodles.service.SshLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qingfeng
 * @version 1.0.0
 * @ProjectName SshHandler
 * @Description ssh 处理
 * @createTime 2022/5/2 0002 15:26
 */
@ServerEndpoint(value = "/ws/ssh/{sshId}")
@Component
@Slf4j
public class SshHandler {

    private static SshLinkService sshLinkService;

    private static ContainerService containerService;

    @Autowired
    public void setSshLinkService(SshLinkService sshLinkService) {
        SshHandler.sshLinkService = sshLinkService;
    }

    @Autowired
    public void ContainerService(ContainerService containerService) {
        SshHandler.containerService = containerService;
    }


    private static final ConcurrentHashMap<String, HandlerItem> HANDLER_ITEM_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("websocket 加载");
    }


    private static final AtomicInteger OnlineCount = new AtomicInteger(0);

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的Session对象。
     */
    private static CopyOnWriteArraySet<javax.websocket.Session> sessionSet = new CopyOnWriteArraySet<javax.websocket.Session>();


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(javax.websocket.Session session, @PathParam("sshId") Long sshId) {
        sessionSet.add(session);
        SshLink sshLink = sshLinkService.getById(sshId);
        SshModel sshItem = new SshModel();
        sshItem.setHost(sshLink.getHost());
        sshItem.setPort(sshLink.getPort());
        sshItem.setUser(sshLink.getName());
        sshItem.setPassword(sshLink.getPassword());
        // 在线数加1
        int cnt = OnlineCount.incrementAndGet();
        log.info("有连接加入，当前连接数为：{},sessionId={}", cnt, session.getId());
        HandlerItem handlerItem = null;
        try {
            handlerItem = new HandlerItem(session, sshItem);
            handlerItem.startRead();
            HANDLER_ITEM_CONCURRENT_HASH_MAP.put(session.getId(), handlerItem);
            if (Objects.nonNull(sshLink.getConsoleType())) {
                //找出docker id
                LambdaQueryWrapper<Container> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Container::getSshId, sshId);
                Container container = containerService.getOne(wrapper);
                sendBinary(session,"docker exec -it " + container.getContainerId() + " /bin/" + sshLink.getConsoleType() + "\n");
            }
        } catch (Exception exception) {
            sendMessage(session, "连接失败，请检查连接信息是否配置正确\n");
            sessionSet.remove(session);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(javax.websocket.Session session) {
        sessionSet.remove(session);
        int cnt = OnlineCount.decrementAndGet();
        log.info("有连接关闭，当前连接数为：{}", cnt);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, javax.websocket.Session session) throws Exception {
        try {
            if (JSONUtil.isJson(message)) {
                SshMessage sshData = JSON.parseObject(message, SshMessage.class);
                HandlerItem handlerItem = HANDLER_ITEM_CONCURRENT_HASH_MAP.get(session.getId());
                handlerItem.channel.setPty(true);
                handlerItem.channel.setPtySize(sshData.getCols(), sshData.getRows(), 640, 480);
            } else {
                HandlerItem handlerItem = HANDLER_ITEM_CONCURRENT_HASH_MAP.get(session.getId());
                this.sendCommand(handlerItem, message);
            }
        } catch (Exception e) {
            //吃掉Hutool JSONUtil产生的异常
            HandlerItem handlerItem = HANDLER_ITEM_CONCURRENT_HASH_MAP.get(session.getId());
            this.sendCommand(handlerItem, message);
        }

    }

    /**
     * 出现错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(javax.websocket.Session session, Throwable error) {
        log.error("发生错误：{}，Session ID： {}", error.getMessage(), session.getId());
        error.printStackTrace();
    }

    private void sendCommand(HandlerItem handlerItem, String data) throws Exception {
        if (handlerItem.checkInput(data)) {
            handlerItem.outputStream.write(data.getBytes());
        } else {
            handlerItem.outputStream.write("没有执行相关命令权限".getBytes());
            handlerItem.outputStream.flush();
            handlerItem.outputStream.write(new byte[]{3});
        }
        handlerItem.outputStream.flush();
    }

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session
     * @param message
     */
    public static void sendMessage(javax.websocket.Session session, String message) {
        try {
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            log.error("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    private class HandlerItem implements Runnable {
        private final javax.websocket.Session session;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final Session openSession;
        private final ChannelShell channel;
        private final SshModel sshItem;
        private final StringBuilder nowLineInput = new StringBuilder();

        HandlerItem(javax.websocket.Session session, SshModel sshItem) throws IOException {
            this.session = session;
            this.sshItem = sshItem;
            this.openSession = JschUtil.openSession(sshItem.getHost(), sshItem.getPort(), sshItem.getUser(), sshItem.getPassword());
            this.channel = (ChannelShell) JschUtil.createChannel(openSession, ChannelType.SHELL);
            this.inputStream = channel.getInputStream();
            this.outputStream = channel.getOutputStream();
        }

        void startRead() throws JSchException {
            this.channel.connect();
            ThreadUtil.execute(this);
        }


        /**
         * 添加到命令队列
         *
         * @param msg 输入
         * @return 当前待确认待所有命令
         */
        private String append(String msg) {
            char[] x = msg.toCharArray();
            if (x.length == 1 && x[0] == 127) {
                // 退格键
                int length = nowLineInput.length();
                if (length > 0) {
                    nowLineInput.delete(length - 1, length);
                }
            } else {
                nowLineInput.append(msg);
            }
            return nowLineInput.toString();
        }

        public boolean checkInput(String msg) {
            String allCommand = this.append(msg);
            boolean refuse;
            if (StrUtil.equalsAny(msg, StrUtil.CR, StrUtil.TAB)) {
                String join = nowLineInput.toString();
                if (StrUtil.equals(msg, StrUtil.CR)) {
                    nowLineInput.setLength(0);
                }
                refuse = SshModel.checkInputItem(sshItem, join);
            } else {
                // 复制输出
                refuse = SshModel.checkInputItem(sshItem, msg);
            }
            return refuse;
        }


        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                int i;
                //如果没有数据来，线程会一直阻塞在这个地方等待数据。
                while ((i = inputStream.read(buffer)) != -1) {
                    sendBinary(session, new String(Arrays.copyOfRange(buffer, 0, i), sshItem.getCharsetT()));
                }
            } catch (Exception e) {
                if (!this.openSession.isConnected()) {
                    return;
                }
                SshHandler.this.destroy(this.session);
            }
        }
    }

    public void destroy(javax.websocket.Session session) {
        HandlerItem handlerItem = HANDLER_ITEM_CONCURRENT_HASH_MAP.get(session.getId());
        if (handlerItem != null) {
            IoUtil.close(handlerItem.inputStream);
            IoUtil.close(handlerItem.outputStream);
            JschUtil.close(handlerItem.channel);
            JschUtil.close(handlerItem.openSession);
        }
        IoUtil.close(session);
        HANDLER_ITEM_CONCURRENT_HASH_MAP.remove(session.getId());
    }

    private static void sendBinary(javax.websocket.Session session, String msg) {
        try {
            System.out.println("#####:" + msg);
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {

        }
    }
}
