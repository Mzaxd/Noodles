package com.mzaxd.noodles.job;

import cn.hutool.http.HttpRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mzaxd.noodles.constant.RedisConstant;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.constant.UrlConstant;
import com.mzaxd.noodles.domain.entity.Container;
import com.mzaxd.noodles.domain.entity.HostDetector;
import com.mzaxd.noodles.domain.entity.HostMachine;
import com.mzaxd.noodles.service.*;
import com.mzaxd.noodles.util.CpuUtil;
import com.mzaxd.noodles.util.RedisCache;
import com.mzaxd.noodles.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/**
 * 检测所有实例的在线状态
 *
 * @author 13439
 */

@Slf4j
@Component
public class CheckInstancesStatus {

    @Resource
    private ContainerService containerService;

    @Resource
    private HostMachineService hostMachineService;

    @Resource
    private HostDetectorService hostDetectorService;

    @Resource
    private NotificationService notificationService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 检查实例状态并且发送对应的提醒
     */
    @Scheduled(cron = "* 0/1 * * * ?")
    public void checkInstancesStatus() {
        List<Container> containers = containerService.list();

        containers.forEach(container -> {
            try {
                if (!StringUtils.hasText(container.getWebUi()) && !StringUtils.hasText(container.getServerAddress())) {
                    container.setContainerState(SystemConstant.CONTAINER_STATE_UNKNOWN);
                    containerService.saveOrUpdate(container);
                    return;
                }
                //通过Socket（IP+端口）判断是否在线
                if (StringUtils.hasText(container.getServerAddress())) {
                    if (UrlUtil.isServiceOnline(UrlUtil.getHostname(container.getServerAddress()), UrlUtil.getPort(container.getServerAddress()))) {
                        container.setContainerState(SystemConstant.CONTAINER_STATE_RUNNING);
                        containerService.saveOrUpdate(container);
                        return;
                    }
                }
                //通过HTTP请求判断是否在线
                HttpRequest.get(container.getWebUi()).setConnectionTimeout(5000).execute(true);
                log.info("[实例状态检测]：与{}建立连接成功", container.getName());
                container.setContainerState(SystemConstant.CONTAINER_STATE_RUNNING);
                containerService.saveOrUpdate(container);
            } catch (Exception exception) {
                log.info("[实例状态检测]：与{}建立连接失败，状态转为离线", container.getName());
                container.setContainerState(SystemConstant.CONTAINER_STATE_EXITED);
                // 判断 Redis 里面有没有，如果有就不需要提醒，如果没有就提醒
                Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_CONTAINER_IDS);
                if (CollectionUtils.isEmpty(set)) {
                    set = new HashSet<>();
                }
                // 如果 Redis 里面有，说明已经发送过了未 check 的通知，所以不需要发送，直接返回
                if (set.contains(container.getId().toString())) {
                    return;
                } else {
                    // 根据实例对应的提醒方式进行提醒
                    if (container.getNotify().equals(SystemConstant.NOTIFY_NO)) {
                        return;
                    }
                    if (container.getNotify().equals(SystemConstant.NOTIFY_BROWSER)) {
                        notificationService.sendContainerOfflineNotification(container.getId());
                        set.add(container.getId().toString());
                    }
                    if (container.getNotify().equals(SystemConstant.NOTIFY_EMAIL)) {
                        notificationService.sendContainerOfflineEmail(container.getId());
                    }
                    if (container.getNotify().equals(SystemConstant.NOTIFY_BROWSER_EMAIL)) {
                        notificationService.sendContainerOfflineNotificationEmail(container.getId());
                        set.add(container.getId().toString());
                    }
                }
                redisCache.setCacheSet(RedisConstant.NOTIFY_CONTAINER_IDS, set);
            }
        });

        //检测虚拟机在线状态
        LambdaQueryWrapper<HostMachine> vmWrapper = new LambdaQueryWrapper<>();
        vmWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        List<HostMachine> vms = hostMachineService.list(vmWrapper);
        vms.forEach(vm -> {
            try {
                if (!StringUtils.hasText(vm.getServerAddress()) && !StringUtils.hasText(vm.getServerAddress())) {
                    vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_UNKNOWN);
                    hostMachineService.saveOrUpdate(vm);
                    return;
                }
                //通过Ping的方式判断是否在线
                if (StringUtils.hasText(vm.getServerAddress())) {
                    if (UrlUtil.isHostOnline(vm.getServerAddress())) {
                        log.info("[实例状态检测]：与{}建立连接成功", vm.getName());
                        vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_ONLINE);
                        hostMachineService.saveOrUpdate(vm);
                        return;
                    }
                }
                //通过HTTP请求的方式判断是否在线
                HttpRequest.get(vm.getManageIp()).setConnectionTimeout(5000).execute(true);
                log.info("[实例状态检测]：与{}建立连接成功", vm.getName());
                vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_ONLINE);
                hostMachineService.saveOrUpdate(vm);
            } catch (Exception exception) {
                log.info("[实例状态检测]：与{}建立连接失败，状态转为离线", vm.getName());
                vm.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_OFFLINE);
                //判断Redis里面有没有 如果有就不需要提醒 如果没有就提醒
                Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_VM_IDS);
                if (CollectionUtils.isEmpty(set)) {
                    set = new HashSet<>();
                }
                //如果redis里面有 说明已经发送过了未check的通知 所以不需要发送 直接返回
                if (set.contains(vm.getId().toString())) {
                    return;
                } else {
                    //根据实例对应的提醒方式进行提醒
                    if (vm.getNotify().equals(SystemConstant.NOTIFY_NO)) {
                        return;
                    }
                    if (vm.getNotify().equals(SystemConstant.NOTIFY_BROWSER)) {
                        notificationService.sendContainerOfflineNotification(vm.getId());
                        set.add(vm.getId().toString());
                    }
                    if (vm.getNotify().equals(SystemConstant.NOTIFY_EMAIL)) {
                        notificationService.sendContainerOfflineEmail(vm.getId());
                    }
                    if (vm.getNotify().equals(SystemConstant.NOTIFY_BROWSER_EMAIL)) {
                        notificationService.sendContainerOfflineNotificationEmail(vm.getId());
                        set.add(vm.getId().toString());
                    }
                }
                redisCache.setCacheSet(RedisConstant.NOTIFY_VM_IDS, set);
            }
        });

        //检测物理机在线状态(检测物理机要用探测器的isTureUrl接口)
        LambdaQueryWrapper<HostDetector> detectorWrapper = new LambdaQueryWrapper<>();
        List<HostDetector> hostDetectors = hostDetectorService.list(detectorWrapper);
        List<HostMachine> hostMachines = new ArrayList<>();
        hostDetectors.forEach(detector -> {
            try {
                HttpRequest.get(detector.getDetectorIpAddress() + UrlConstant.DETECTOR_IS_TRUE_URL).setConnectionTimeout(5000).execute(true);
                HostMachine hostMachine = hostMachineService.getById(detector.getHostMachineId());
                if (Objects.nonNull(hostMachine)) {
                    hostMachine.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_ONLINE);
                    hostMachineService.saveOrUpdate(hostMachine);
                }
            } catch (Exception exception) {
                HostMachine hostMachine = hostMachineService.getById(detector.getHostMachineId());
                if (Objects.nonNull(hostMachine)) {
                    hostMachine.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_OFFLINE);
                    hostMachineService.saveOrUpdate(hostMachine);
                }
                //判断Redis里面有没有 如果有就不需要提醒 如果没有就提醒
                Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_HOST_IDS);
                if (CollectionUtils.isEmpty(set)) {
                    set = new HashSet<>();
                }
                //如果redis里面有 说明已经发送过了未check的通知 所以不需要发送 直接返回
                if (set.contains(hostMachine.getId().toString())) {
                    return;
                } else {
                    //根据实例对应的提醒方式进行提醒
                    if (hostMachine.getNotify().equals(SystemConstant.NOTIFY_NO)) {
                        return;
                    }
                    if (hostMachine.getNotify().equals(SystemConstant.NOTIFY_BROWSER)) {
                        notificationService.sendContainerOfflineNotification(hostMachine.getId());
                        set.add(hostMachine.getId().toString());
                    }
                    if (hostMachine.getNotify().equals(SystemConstant.NOTIFY_EMAIL)) {
                        notificationService.sendContainerOfflineEmail(hostMachine.getId());
                    }
                    if (hostMachine.getNotify().equals(SystemConstant.NOTIFY_BROWSER_EMAIL)) {
                        notificationService.sendContainerOfflineNotificationEmail(hostMachine.getId());
                        set.add(hostMachine.getId().toString());
                    }
                }
                redisCache.setCacheSet(RedisConstant.NOTIFY_HOST_IDS, set);
            }
        });
    }
}
