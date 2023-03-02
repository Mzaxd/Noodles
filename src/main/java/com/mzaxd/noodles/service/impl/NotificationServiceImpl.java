package com.mzaxd.noodles.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mzaxd.noodles.constant.RedisConstant;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.Container;
import com.mzaxd.noodles.domain.entity.HostMachine;
import com.mzaxd.noodles.domain.entity.Notification;
import com.mzaxd.noodles.domain.vo.NotificationListVo;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.service.ContainerService;
import com.mzaxd.noodles.service.HostMachineService;
import com.mzaxd.noodles.service.NotificationService;
import com.mzaxd.noodles.mapper.NotificationMapper;
import com.mzaxd.noodles.util.BeanCopyUtils;
import com.mzaxd.noodles.util.RedisCache;
import com.mzaxd.noodles.util.SystemSettingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author 13439
 * @description 针对表【notification】的数据库操作Service实现
 * @createDate 2023-02-16 16:04:53
 */
@Service
@Slf4j
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {

    @Resource
    private ContainerService containerService;

    @Resource
    private HostMachineService hostMachineService;

    @Resource
    private SystemSettingUtils systemSettingUtils;

    @Resource
    private RedisTemplate redisTemplate;

    public void sendContainerOfflineNotification(Long id) {
        Notification notification = new Notification();
        Container container = containerService.getById(id);
        Integer notify = container.getNotify();
        //判断发送方式
        if (notify.equals(SystemConstant.NOTIFY_BROWSER_EMAIL) || notify.equals(SystemConstant.NOTIFY_EMAIL)) {
            //发送邮件
            sendContainerOfflineEmail(id);
        }
        notification.setTitle("容器掉线")
                .setType(SystemConstant.OFFLINE_NOTIFICATION)
                .setSendType(container.getNotify())
                .setInstanceType(SystemConstant.INSTANCETYPE_CONTAINER)
                .setInstanceId(id);
        String content = String.format("容器[ %s ]已无法正常访问，请排查", container.getName());
        notification.setContent(content);
        save(notification);
        log.info("[掉线提醒]：发送浏览器提醒：{}", content);
    }

    private void sendContainerOfflineEmail(Long id) {
        Container container = containerService.getById(id);
        String time = LocalDateTime.now().toString();
        String content = String.format("发现容器[ %s ]掉线---- %s", container.getName(), time);
        MailUtil.send(systemSettingUtils.getMailAccount(), CollUtil.newArrayList(systemSettingUtils.getMailTarget()), "Noodles掉线提醒", content, false);
        log.info("[掉线提醒]：发送邮件提醒：{}", content);
    }

    @Override
    public void sendVmOfflineNotification(Long id) {
        Notification notification = new Notification();
        HostMachine vm = hostMachineService.getById(id);
        Integer notify = vm.getNotify();
        //判断发送方式
        if (notify.equals(SystemConstant.NOTIFY_BROWSER_EMAIL) || notify.equals(SystemConstant.NOTIFY_EMAIL)) {
            //发送邮件
            sendVmOfflineEmail(id);
        }
        notification.setTitle("虚拟机掉线")
                .setType(SystemConstant.OFFLINE_NOTIFICATION)
                .setSendType(vm.getNotify())
                .setInstanceType(SystemConstant.INSTANCETYPE_VM)
                .setInstanceId(id);
        String content = String.format("虚拟机[ %s ]已无法正常访问，请排查", vm.getName());
        notification.setContent(content);
        save(notification);
        log.info("[掉线提醒]：发送浏览器提醒：{}", content);
    }

    private void sendVmOfflineEmail(Long id) {
        HostMachine vm = hostMachineService.getById(id);
        String time = LocalDateTime.now().toString();
        String content = String.format("发现虚拟机[ %s ]掉线---- %s", vm.getName(), time);
        MailUtil.send(systemSettingUtils.getMailAccount(), CollUtil.newArrayList(systemSettingUtils.getMailTarget()), "Noodles掉线提醒", content, false);
        log.info("[掉线提醒]：发送邮件提醒：{}", content);
    }

    @Override
    public void sendHostOfflineNotification(Long id) {
        Notification notification = new Notification();
        HostMachine host = hostMachineService.getById(id);
        Integer notify = host.getNotify();
        //判断发送方式
        if (notify.equals(SystemConstant.NOTIFY_BROWSER_EMAIL) || notify.equals(SystemConstant.NOTIFY_EMAIL)) {
            //发送邮件
            sendHostOfflineEmail(id);
        }
        notification.setTitle("物理机掉线")
                .setType(SystemConstant.OFFLINE_NOTIFICATION)
                .setSendType(host.getNotify())
                .setInstanceType(SystemConstant.INSTANCETYPE_HOST)
                .setInstanceId(id);
        String content = String.format("无法连接到物理机[ %s ]的探测器，请排查", host.getName());
        notification.setContent(content);
        save(notification);
        log.info("[掉线提醒]：发送浏览器提醒：{}", content);
    }

    @Override
    public ResponseResult getNotificationList(Integer tab, Integer perPage, Integer currentPage) {
        LambdaQueryWrapper<Notification> notificationWrapper = new LambdaQueryWrapper<>();
        notificationWrapper.orderByDesc(Notification::getCreateTime);
        List<Notification> notificationList = new ArrayList<>();
        if (tab.equals(SystemConstant.FRONTEND_NOTIFICATION_NOT_AFFIRM)) {
            notificationWrapper.eq(Notification::getAffirm, SystemConstant.NOTIFICATION_NOT_AFFIRM);
        }
        if (tab.equals(SystemConstant.FRONTEND_NOTIFICATION_AFFIRM)) {
            notificationWrapper.eq(Notification::getAffirm, SystemConstant.NOTIFICATION_AFFIRM);
        }
        if (tab.equals(SystemConstant.FRONTEND_NOTIFICATION_ALL)) {
            //分页
            PageHelper.startPage(currentPage, perPage);
            notificationList = list(notificationWrapper);
            PageInfo pageInfo = new PageInfo<>(notificationList, perPage);
            List<NotificationListVo> notificationListVos = BeanCopyUtils.copyBeanList(notificationList, NotificationListVo.class);
            notificationListVos.forEach(notificationListVo -> {
                Integer instanceType = notificationListVo.getInstanceType();
                //设置实例的图像、名称等信息
                if (SystemConstant.INSTANCETYPE_HOST.equals(instanceType) || SystemConstant.INSTANCETYPE_VM.equals(instanceType)) {
                    HostMachine hostMachine = hostMachineService.getById(notificationListVo.getInstanceId());
                    notificationListVo.setInstanceName(hostMachine.getName())
                            .setInstanceAvatar(hostMachine.getAvatar());
                }
                if (SystemConstant.INSTANCETYPE_CONTAINER.equals(instanceType)) {
                    Container container = containerService.getById(notificationListVo.getInstanceId());
                    notificationListVo.setInstanceName(container.getName())
                            .setInstanceAvatar(container.getAvatar());
                }
            });
            pageInfo.setList(notificationListVos);
            return ResponseResult.okResult(pageInfo);
        }
        if (tab.equals(SystemConstant.FRONTEND_NOTIFICATION_TYPE_OFFLINE)) {
            notificationWrapper.eq(Notification::getType, SystemConstant.OFFLINE_NOTIFICATION);
        }
        if (tab.equals(SystemConstant.FRONTEND_NOTIFICATION_TYPE_STATISTICS)) {
            notificationWrapper.eq(Notification::getType, SystemConstant.STATISTICS_NOTIFICATION);
        }
        //分页
        PageHelper.startPage(currentPage, perPage);
        notificationList = list(notificationWrapper);
        PageInfo pageInfo = new PageInfo<>(notificationList, perPage);
        List<NotificationListVo> notificationListVos = BeanCopyUtils.copyBeanList(notificationList, NotificationListVo.class);
        notificationListVos.forEach(notificationListVo -> {
            Integer instanceType = notificationListVo.getInstanceType();
            //设置实例的图像、名称等信息
            if (SystemConstant.INSTANCETYPE_HOST.equals(instanceType) || SystemConstant.INSTANCETYPE_VM.equals(instanceType)) {
                HostMachine hostMachine = hostMachineService.getById(notificationListVo.getInstanceId());
                notificationListVo.setInstanceName(hostMachine.getName())
                                  .setInstanceAvatar(hostMachine.getAvatar());
            }
            if (SystemConstant.INSTANCETYPE_CONTAINER.equals(instanceType)) {
                Container container = containerService.getById(notificationListVo.getInstanceId());
                notificationListVo.setInstanceName(container.getName())
                        .setInstanceAvatar(container.getAvatar());
            }
        });
        pageInfo.setList(notificationListVos);
        return ResponseResult.okResult(pageInfo);
    }

    @Override
    public ResponseResult affirmNotification(Long id) {
        Notification notification = getById(id);
        if (SystemConstant.INSTANCETYPE_HOST.equals(notification.getInstanceType())) {
            redisTemplate.opsForSet().remove(RedisConstant.NOTIFY_HOST_IDS, notification.getInstanceId().toString());
        }
        if (SystemConstant.INSTANCETYPE_VM.equals(notification.getInstanceType())) {
            redisTemplate.opsForSet().remove(RedisConstant.NOTIFY_VM_IDS, notification.getInstanceId().toString());
        }
        if (SystemConstant.INSTANCETYPE_CONTAINER.equals(notification.getInstanceType())) {
            redisTemplate.opsForSet().remove(RedisConstant.NOTIFY_CONTAINER_IDS, notification.getInstanceId().toString());
        }
        saveOrUpdate(notification.setAffirm(SystemConstant.NOTIFICATION_AFFIRM));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS.getCode(), "处理完成");
    }

    @Override
    public ResponseResult getNotificationCount() {
        LambdaQueryWrapper<Notification> notificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        notificationLambdaQueryWrapper.eq(Notification::getAffirm, SystemConstant.NOTIFICATION_NOT_AFFIRM);
        return ResponseResult.okResult(count(notificationLambdaQueryWrapper));
    }

    private void sendHostOfflineEmail(Long id) {
        HostMachine host = hostMachineService.getById(id);
        String time = LocalDateTime.now().toString();
        String content = String.format("发现物理机[ %s ]掉线---- %s", host.getName(), time);
        MailUtil.send(systemSettingUtils.getMailAccount(), CollUtil.newArrayList(systemSettingUtils.getMailTarget()), "Noodles掉线提醒", content, false);
        log.info("[掉线提醒]：发送邮件提醒：{}", content);
    }
}




