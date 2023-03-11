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
import com.mzaxd.noodles.util.CpuUtil;
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

    /**
     * 检查实例状态并且发送对应的提醒
     */
    @Scheduled(cron = "* 0/1 * * * ?")
    public void checkInstancesStatus() {
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
    }
}
