package com.mzaxd.noodles.job;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mzaxd.noodles.constant.RedisConstant;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.constant.UrlConstant;
import com.mzaxd.noodles.domain.entity.Container;
import com.mzaxd.noodles.domain.entity.HostDetector;
import com.mzaxd.noodles.domain.entity.HostMachine;
import com.mzaxd.noodles.service.*;
import com.mzaxd.noodles.util.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 检测所有实例的在线状态
 * @author 13439
 */

@Slf4j
@Component
public class CheckInstancesStatus{

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

    /**
     * 检查实例状态并且发送对应的提醒
     */
    @Scheduled(cron = "* 0/10 * * * ?")
    public void checkInstancesStatus() {
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
                if (!CollectionUtils.isEmpty(set)){
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
                if (!CollectionUtils.isEmpty(set)){
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
            } catch (HttpException exception) {
                HostMachine hostMachine = hostMachineService.getById(detector.getHostMachineId());
                if (Objects.nonNull(hostMachine)) {
                    hostMachine.setHostMachineState(SystemConstant.HOST_MACHINE_STATE_OFFLINE);
                    hostMachines.add(hostMachine);
                }
                //判断Redis里面有没有 如果有就不需要提醒 如果没有就提醒
                Set<String> set = redisCache.getCacheSet(RedisConstant.NOTIFY_HOST_IDS);
                if (!CollectionUtils.isEmpty(set)){
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
    }
}
