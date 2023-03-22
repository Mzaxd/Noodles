package com.mzaxd.noodles;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mzaxd.noodles.constant.RedisConstant;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.constant.UrlConstant;
import com.mzaxd.noodles.domain.entity.*;
import com.mzaxd.noodles.mapper.OsMapper;
import com.mzaxd.noodles.service.*;
import com.mzaxd.noodles.util.CpuUtil;
import com.mzaxd.noodles.util.RedisCache;
import com.mzaxd.noodles.util.SystemInfoUtils;
import com.mzaxd.noodles.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.Resource;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoodlesApplicationTests {

    @Resource
    private OsMapper osMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SystemSettingService systemSettingService;

    @Resource
    private NotificationService notificationService;

    @Resource
    private HostDetectorService hostDetectorService;

    @Test
    void contextLoads() {
        try {
            HttpRequest.get("https://elizaveta.top:71/").setConnectionTimeout(1000).execute(true);
        } catch (HttpException exception) {
            log.info("建立连接失败");
        }
    }

    @Test
    void test() {
        Set<Integer> containerNotifyIds = redisCache.getCacheSet("ContainerNotifyIds");
        redisTemplate.boundSetOps("setKey");
        redisTemplate.boundSetOps("Notify:ContainerIds").remove(1);
        containerNotifyIds.remove(1);
        redisCache.setCacheSet("ContainerNotifyIds", containerNotifyIds);
    }

    @Test
    void emailTest() {
        SystemSetting systemSetting = systemSettingService.getById(1);
        MailAccount account = new MailAccount();
        account.setFrom(systemSetting.getNotificationEmail());
        account.setUser(systemSetting.getNotificationEmail());
        account.setPass(systemSetting.getEmailPass());
        MailUtil.send(account, CollUtil.newArrayList("mzaxd0712@gmail.com"), "测试5", "邮件来自测试", false);
    }

    @Test
    public void testJVMInfo() {
        JSONObject jvmInfo = SystemInfoUtils.getJvmInfo();
        System.out.println(jvmInfo);
    }

    @Resource
    private HostMachineService hostMachineService;

    @Resource
    private ContainerService containerService;

    @Resource
    private ServirService servirService;

    @Resource
    private AuditLogService auditLogService;

    @Resource
    private EveryDayDataService everyDayDataService;

    /**
     * 每天晚上收集今日数据
     */
    @Test
    public void everyDayDataCollect() {
        EveryDayData everyDayData = new EveryDayData();
        LambdaQueryWrapper<AuditLog> auditLogWrapper = new LambdaQueryWrapper<>();
        Map<String, Integer> hostStateInfo = hostMachineService.getHostStateInfo();
        Map<String, Integer> vmStateInfo = hostMachineService.getVmStateInfo();
        Map<String, Integer> containerStateInfo = containerService.getContainerStateInfo();
        //设置主机总数
        everyDayData.setHostCount(hostStateInfo.get("hostCount"));
        //设置主机在线总数
        everyDayData.setHostOnlineCount(hostStateInfo.get("hostOnlineCount"));
        //设置主机离线总数
        everyDayData.setHostOfflineCount(hostStateInfo.get("hostOfflineCount"));
        //设置主机未知总数
        everyDayData.setHostUnknownCount(hostStateInfo.get("hostUnknownCount"));
        //设置虚拟机总数
        everyDayData.setVmCount(vmStateInfo.get("vmCount"));
        //设置虚拟机在线总数
        everyDayData.setVmOnlineCount(vmStateInfo.get("vmOnlineCount"));
        //设置虚拟机离线总数
        everyDayData.setVmOfflineCount(vmStateInfo.get("vmOfflineCount"));
        //设置虚拟机未知总数
        everyDayData.setVmUnknownCount(vmStateInfo.get("vmUnknownCount"));
        //设置容器总数
        everyDayData.setContainerCount(containerStateInfo.get("containerCount"));
        //设置容器在线总数
        everyDayData.setContainerOnlineCount(containerStateInfo.get("containerOnlineCount"));
        //设置容器离线总数
        everyDayData.setContainerOfflineCount(containerStateInfo.get("containerOfflineCount"));
        //设置容器未知总数
        everyDayData.setContainerUnknownCount(containerStateInfo.get("containerUnknownCount"));
        //设置服务总数
        everyDayData.setServirCount(servirService.count());
        //设置操作总数
        auditLogWrapper.ge(AuditLog::getCreateTime, DateUtil.beginOfDay(DateUtil.date()));
        auditLogWrapper.lt(AuditLog::getCreateTime, DateUtil.endOfDay(DateUtil.date()));
        everyDayData.setAuditCount(auditLogService.count(auditLogWrapper));

        System.out.println(everyDayData);
    }

    @Test
    public void checkInstanceStatus() {
        long startTime = System.nanoTime();    // 记录开始时间

        //检测容器在线状态
        List<Container> containers = containerService.list();
        containers.forEach(container -> {
            try {
                if (!StringUtils.hasText(container.getWebUi())) {
                    container.setContainerState(SystemConstant.CONTAINER_STATE_UNKNOWN);
                    return;
                }
                HttpRequest.get(container.getWebUi()).setConnectionTimeout(1000).execute(true);
                log.info("[实例状态检测]：与{}建立连接成功", container.getName());
                container.setContainerState(SystemConstant.CONTAINER_STATE_RUNNING);
            } catch (Exception exception) {
                log.info("[实例状态检测]：与{}建立连接失败，状态转为离线", container.getName());
                container.setContainerState(SystemConstant.CONTAINER_STATE_EXITED);
                //判断Redis里面有没有 如果有就不需要提醒 如果没有就提醒
                Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_CONTAINER_IDS);
                if (!CollectionUtils.isEmpty(set)) {
                    //如果redis里面有 说明已经发送过了未check的通知 所以不需要发送 直接返回
                    if (set.contains(container.getId().toString())) {
                        return;
                    } else {
                        //根据实例对应的提醒方式进行提醒
                        if (container.getNotify().equals(SystemConstant.NOTIFY_NO)) {
                            return;
                        } else {
                            notificationService.sendContainerOfflineNotification(container.getId());
                        }
                    }
                }
                //存入redis
                set.add(container.getId().toString());
                redisCache.setCacheSet(RedisConstant.NOTIFY_CONTAINER_IDS, set);
            }
        });
        containerService.saveOrUpdateBatch(containers);

        //检测虚拟机在线状态
        LambdaQueryWrapper<HostMachine> vmWrapper = new LambdaQueryWrapper<>();
        vmWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        List<HostMachine> vms = hostMachineService.list(vmWrapper);
        vms.forEach(vm -> {
            try {
                if (!StringUtils.hasText(vm.getManageIp())) {
                    vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_UNKNOWN);
                    return;
                }
                HttpRequest.get(vm.getManageIp()).setConnectionTimeout(1000).execute(true);
                log.info("[实例状态检测]：与{}建立连接成功", vm.getName());
                vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_ONLINE);
            } catch (Exception exception) {
                log.info("[实例状态检测]：与{}建立连接失败，状态转为离线", vm.getName());
                vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_OFFLINE);
                //判断Redis里面有没有 如果有就不需要提醒 如果没有就提醒
                Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_VM_IDS);
                if (!CollectionUtils.isEmpty(set)) {
                    //如果redis里面有 说明已经发送过了未check的通知 所以不需要发送 直接返回
                    if (set.contains(vm.getId().toString())) {
                        return;
                    } else {
                        //根据实例对应的提醒方式进行提醒
                        if (vm.getNotify().equals(SystemConstant.NOTIFY_NO)) {
                            return;
                        } else {
                            notificationService.sendVmOfflineNotification(vm.getId());
                        }
                    }
                }
                //存入redis
                set.add(vm.getId().toString());
                redisCache.setCacheSet(RedisConstant.NOTIFY_VM_IDS, set);
            }
        });
        hostMachineService.saveOrUpdateBatch(vms);

        //检测物理机在线状态(检测物理机要用探测器的isTureUrl接口)
        LambdaQueryWrapper<HostDetector> detectorWrapper = new LambdaQueryWrapper<>();
        List<HostDetector> hostDetectors = hostDetectorService.list(detectorWrapper);
        List<HostMachine> hostMachines = new ArrayList<>();
        hostDetectors.forEach(detector -> {
            try {
                HttpRequest.get(detector.getDetectorIpAddress() + UrlConstant.DETECTOR_IS_TRUE_URL).setConnectionTimeout(1000).execute(true);
                HostMachine hostMachine = hostMachineService.getById(detector.getHostMachineId());
                if (Objects.nonNull(hostMachine)) {
                    hostMachine.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_ONLINE);
                    hostMachines.add(hostMachine);
                }
            } catch (Exception exception) {
                HostMachine hostMachine = hostMachineService.getById(detector.getHostMachineId());
                if (Objects.nonNull(hostMachine)) {
                    hostMachine.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_OFFLINE);
                    hostMachines.add(hostMachine);
                }
                //判断Redis里面有没有 如果有就不需要提醒 如果没有就提醒
                Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_HOST_IDS);
                if (!CollectionUtils.isEmpty(set)) {
                    //如果redis里面有 说明已经发送过了未check的通知 所以不需要发送 直接返回
                    if (set.contains(hostMachine.getId().toString())) {
                        return;
                    } else {
                        //根据实例对应的提醒方式进行提醒
                        if (hostMachine.getNotify().equals(SystemConstant.NOTIFY_NO)) {
                            return;
                        } else {
                            notificationService.sendHostOfflineNotification(hostMachine.getId());
                        }
                    }
                }
                //存入redis
                set.add(hostMachine.getId().toString());
                redisCache.setCacheSet(RedisConstant.NOTIFY_HOST_IDS, set);
            }
        });
        hostMachineService.saveOrUpdateBatch(hostMachines);

        long endTime = System.nanoTime();      // 记录结束时间
        long elapsedTime = endTime - startTime;
        double seconds = (double) elapsedTime / 1_000_000_000.0; // 将纳秒转换为秒
        System.out.println("代码执行时间：" + seconds + " 秒");
    }

    @Test
    public void checkContainerStatusMultiThread() {
        long startTime = System.nanoTime();    // 记录开始时间

        List<Container> containers = containerService.list();
        int thread = CpuUtil.getLogicProcessorCount();
        int containerCount = containerService.count();

        int corePoolSize = Math.min(thread + 1, containerCount);
        int maxPoolSize = Math.max(thread + 1, containerCount);

        // 创建一个包含10个线程的线程池
        ExecutorService executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                1,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.AbortPolicy());

        // 创建一个 Future 列表，用于存储每个容器检查的结果
        List<Future<Container>> futures = new ArrayList<>();

        for (Container container : containers) {
            futures.add(executor.submit(() -> {
                try {
                    if (!StringUtils.hasText(container.getWebUi())) {
                        container.setContainerState(SystemConstant.CONTAINER_STATE_UNKNOWN);
                        return container;
                    }
                    HttpRequest.get(container.getWebUi()).setConnectionTimeout(5000).execute(true);
                    log.info("[实例状态检测]：与{}建立连接成功", container.getName());
                    container.setContainerState(SystemConstant.CONTAINER_STATE_RUNNING);
                } catch (Exception exception) {
                    log.info("[实例状态检测]：与{}建立连接失败，状态转为离线", container.getName());
                    container.setContainerState(SystemConstant.CONTAINER_STATE_EXITED);
                    // 判断 Redis 里面有没有，如果有就不需要提醒，如果没有就提醒
                    Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_CONTAINER_IDS);
                    if (!CollectionUtils.isEmpty(set)) {
                        // 如果 Redis 里面有，说明已经发送过了未 check 的通知，所以不需要发送，直接返回
                        if (set.contains(container.getId().toString())) {
                            return container;
                        } else {
                            // 根据实例对应的提醒方式进行提醒
                            if (container.getNotify().equals(SystemConstant.NOTIFY_NO)) {
                                return container;
                            } else {
                                notificationService.sendContainerOfflineNotification(container.getId());
                            }
                        }
                    }
                    // 存入 Redis
                    set.add(container.getId().toString());
                    redisCache.setCacheSet(RedisConstant.NOTIFY_CONTAINER_IDS, set);
                }
                return container;
            }));
        }

        // 等待所有线程执行完成，并收集更新后的容器列表
        List<Container> updatedContainers = new ArrayList<>();
        for (Future<Container> future : futures) {
            try {
                Container container = future.get();
                updatedContainers.add(container);
            } catch (InterruptedException | ExecutionException e) {
                log.error("检查容器状态时出错：{}", e.getMessage());
            }
        }

        // 将更新后的容器列表保存到数据库中
        containerService.saveOrUpdateBatch(updatedContainers);

        // 关闭线程池
        executor.shutdown();

        long endTime = System.nanoTime();      // 记录结束时间
        long elapsedTime = endTime - startTime;
        double seconds = (double) elapsedTime / 1_000_000_000.0; // 将纳秒转换为秒
        System.out.println("代码执行时间：" + seconds + " 秒");
    }


    @Test
    public void checkVmStatus() {
        long startTime = System.nanoTime();    // 记录开始时间

        //检测虚拟机在线状态
        LambdaQueryWrapper<HostMachine> vmWrapper = new LambdaQueryWrapper<>();
        vmWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        List<HostMachine> vms = hostMachineService.list(vmWrapper);
        vms.forEach(vm -> {
            try {
                if (!StringUtils.hasText(vm.getManageIp())) {
                    vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_UNKNOWN);
                    return;
                }
                HttpRequest.get(vm.getManageIp()).setConnectionTimeout(5000).execute(true);
                log.info("[实例状态检测]：与{}建立连接成功", vm.getName());
                vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_ONLINE);
            } catch (Exception exception) {
                log.info("[实例状态检测]：与{}建立连接失败，状态转为离线", vm.getName());
                vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_OFFLINE);
                //判断Redis里面有没有 如果有就不需要提醒 如果没有就提醒
                Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_VM_IDS);
                if (!CollectionUtils.isEmpty(set)) {
                    //如果redis里面有 说明已经发送过了未check的通知 所以不需要发送 直接返回
                    if (set.contains(vm.getId().toString())) {
                        return;
                    } else {
                        //根据实例对应的提醒方式进行提醒
                        if (vm.getNotify().equals(SystemConstant.NOTIFY_NO)) {
                            return;
                        } else {
                            notificationService.sendVmOfflineNotification(vm.getId());
                        }
                    }
                }
                //存入redis
                set.add(vm.getId().toString());
                redisCache.setCacheSet(RedisConstant.NOTIFY_VM_IDS, set);
            }
        });
        hostMachineService.saveOrUpdateBatch(vms);

        long endTime = System.nanoTime();      // 记录结束时间
        long elapsedTime = endTime - startTime;
        double seconds = (double) elapsedTime / 1_000_000_000.0; // 将纳秒转换为秒
        System.out.println("代码执行时间：" + seconds + " 秒");
    }


    @Test
    public void checkVmStatusMultiThread() throws InterruptedException {
        long startTime = System.nanoTime();    // 记录开始时间

        ExecutorService executorService = Executors.newFixedThreadPool(10); // 创建一个线程池
        List<HostMachine> vms = hostMachineService.list(); // 获取所有主机列表
        CountDownLatch countDownLatch = new CountDownLatch(vms.size()); // 用于等待所有线程完成
        for (HostMachine vm : vms) {
            executorService.submit(() -> {
                try {
                    if (!StringUtils.hasText(vm.getManageIp())) {
                        vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_UNKNOWN);
                        return;
                    }
                    HttpRequest.get(vm.getManageIp()).setConnectionTimeout(5000).execute(true);
                    log.info("[实例状态检测]：与{}建立连接成功", vm.getName());
                    vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_ONLINE);
                } catch (Exception exception) {
                    log.info("[实例状态检测]：与{}建立连接失败，状态转为离线", vm.getName());
                    vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_OFFLINE);
                    //判断Redis里面有没有 如果有就不需要提醒 如果没有就提醒
                    Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_VM_IDS);
                    if (!CollectionUtils.isEmpty(set)) {
                        //如果redis里面有 说明已经发送过了未check的通知 所以不需要发送 直接返回
                        if (set.contains(vm.getId().toString())) {
                            return;
                        } else {
                            //根据实例对应的提醒方式进行提醒
                            if (vm.getNotify().equals(SystemConstant.NOTIFY_NO)) {
                                return;
                            } else {
                                notificationService.sendVmOfflineNotification(vm.getId());
                            }
                        }
                    }
                    //存入redis
                    set.add(vm.getId().toString());
                    redisCache.setCacheSet(RedisConstant.NOTIFY_VM_IDS, set);
                } finally {
                    countDownLatch.countDown(); // 完成一个线程
                }
            });
        }
        countDownLatch.await(); // 等待所有线程完成
        hostMachineService.saveOrUpdateBatch(vms); // 保存更新后的主机状态
        executorService.shutdown(); // 关闭线程池

        long endTime = System.nanoTime();      // 记录结束时间
        long elapsedTime = endTime - startTime;
        double seconds = (double) elapsedTime / 1_000_000_000.0; // 将纳秒转换为秒
        System.out.println("代码执行时间：" + seconds + " 秒");
    }

    @Test
    public void pingTest() {
        String ipAddress = "192.168.1.90"; // 要检查的IP地址
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            boolean isReachable = inetAddress.isReachable(5000); // 超时时间为5秒
            if (isReachable) {
                System.out.println("虚拟机在线");
            } else {
                System.out.println("虚拟机不在线");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void serviceTest() {
        try {
            String serverName = "wiz.elizaveta.top";
            int port = 700;
            Socket socket = new Socket(serverName, port);
            System.out.println("MySQL服务已经开启");
            socket.close();
        } catch (Exception e) {
            System.out.println("MySQL服务没有开启或者远程服务器无法访问");
        }
    }

    @Test
    public void hostnameTest() {
        String hostname = UrlUtil.getHostname("192.168.1.90:8080");
        System.out.println(hostname);
    }

    @Test
    public void portTest() {
        String address = "192.168.1.103:3306";
        String regex = ":(\\d+)$"; // 匹配冒号后面的数字
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        } else {
            System.out.println("解析失败");
        }
    }
}
