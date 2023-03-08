package com.mzaxd.noodles.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mzaxd.noodles.constant.RedisConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.*;
import com.mzaxd.noodles.domain.vo.*;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.*;
import com.mzaxd.noodles.util.BeanCopyUtils;
import com.mzaxd.noodles.util.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 13439
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private HostMachineService hostMachineService;

    @Resource
    private ContainerServiceImpl containerService;

    @Resource
    private EveryDayDataService everyDayDataService;

    @Resource
    private ServirService servirService;

    @Resource
    private AuditLogService auditLogService;

    @Resource
    private SshLinkService sshLinkService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ServirHostService servirHostService;

    @Resource
    private ServirContainerService servirContainerService;

    @Override
    public ResponseResult getInstancesRealTimeData() {
        HashMap<String, Object> result = new HashMap<>();
        Map<String, Integer> hostStateInfo = hostMachineService.getHostStateInfo();
        Map<String, Integer> vmStateInfo = hostMachineService.getVmStateInfo();
        Map<String, Integer> containerStateInfo = containerService.getContainerStateInfo();
        List<EveryDayData> lastSixDayData = everyDayDataService.getLastSixDayData();
        List<Integer> servirCountList = lastSixDayData.stream().map(EveryDayData::getServirCount).collect(Collectors.toList());
        servirCountList.add(servirService.count());
        result.put("servirCountList", servirCountList);
        result.put("hostCount", hostStateInfo.get("hostCount"));
        result.put("hostOnlineCount", hostStateInfo.get("hostOnlineCount"));
        result.put("vmCount", vmStateInfo.get("vmCount"));
        result.put("vmOnlineCount", vmStateInfo.get("vmOnlineCount"));
        result.put("containerCount", containerStateInfo.get("containerCount"));
        result.put("containerOnlineCount", containerStateInfo.get("containerOnlineCount"));
        return ResponseResult.okResult(result);
    }

    @Override
    public ResponseResult getRecentConsoleList() {
        LambdaQueryWrapper<AuditLog> auditLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        auditLogLambdaQueryWrapper.eq(AuditLog::getOperation, OperationEnum.CONSOLE_CONNECT.getOperation()).orderByDesc(AuditLog::getCreateTime);
        List<AuditLog> list = auditLogService.list(auditLogLambdaQueryWrapper);
        if (Objects.nonNull(list)) {
            List<RecentConsoleListVo> recentConsoleListVos = list.stream()
                    .distinct()
                    .limit(7L)
                    .map(auditLog -> {
                        Long sshId = JSON.parseArray(auditLog.getParam(), Long.class).get(0);
                        Object instance = sshLinkService.getInstanceInfoBySshId((sshId));
                        if (Objects.nonNull(instance)) {
                            RecentConsoleListVo recentConsoleListVo = BeanCopyUtils.copyBean(instance, RecentConsoleListVo.class);
                            recentConsoleListVo.setSshId(sshId);
                            return recentConsoleListVo;
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
            return ResponseResult.okResult(recentConsoleListVos);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getAffectedServirList() {
        HashSet<Long> affectedServirIds = new HashSet<>();
        //Key是受影响的服务的id,也就是affectedServirId
        HashMap<Long, List<Long>> offlineContainer = new HashMap<>();
        HashMap<Long, List<Long>> offlineHost = new HashMap<>();
        List<AffectedServirListVo> result = new ArrayList<>();
        //判断Redis里面有没有掉线容器Id 如果没有就说明没有受影响的服务
        Set<String> containerSet = redisCache.getCacheSet(RedisConstant.NOTIFY_CONTAINER_IDS);
        if (!CollectionUtils.isEmpty(containerSet)){
            containerSet.forEach(s -> {
                LambdaQueryWrapper<ServirContainer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ServirContainer::getContainerId, Long.valueOf(s));
                //根据Redis中掉线容器Id来查找所有关联的服务
                List<ServirContainer> servirContainers = servirContainerService.list(lambdaQueryWrapper);
                //填充受影响的服务Id和 服务-容器关系 的Map
                servirContainers.forEach(servirContainer -> {
                    Long servirId = servirContainer.getServirId();
                    Long containerId = servirContainer.getContainerId();
                    affectedServirIds.add(servirId);
                    if (Objects.nonNull(offlineContainer.get(servirId))) {
                        offlineContainer.get(servirId).add(containerId);
                    } else {
                        List<Long> containerIds = new ArrayList<>();
                        containerIds.add(containerId);
                        offlineContainer.put(servirId, containerIds);
                    }
                });
            });
        }

        //判断Redis里面有没有掉线的物理机Id或虚拟机Id 如果没有就说明没有受影响的服务
        Set<String> vmSet = redisCache.getCacheSet(RedisConstant.NOTIFY_VM_IDS);
        Set<String> serverSet = redisCache.getCacheSet(RedisConstant.NOTIFY_HOST_IDS);
        Set<String> hostSet = new HashSet<>();
        hostSet.addAll(vmSet);
        hostSet.addAll(serverSet);
        if (!CollectionUtils.isEmpty(hostSet)){
            hostSet.forEach(s -> {
                LambdaQueryWrapper<ServirHost> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ServirHost::getHostId, Long.valueOf(s));
                //根据Redis中掉线hostId来查找所有关联的服务
                List<ServirHost> servirHosts = servirHostService.list(lambdaQueryWrapper);
                //填充受影响的服务Id和 服务-容器关系 的Map
                servirHosts.forEach(servirHost -> {
                    Long servirId = servirHost.getServirId();
                    Long hostId = servirHost.getHostId();
                    affectedServirIds.add(servirId);
                    if (Objects.nonNull(offlineHost.get(servirId))) {
                        offlineHost.get(servirId).add(hostId);
                    } else {
                        List<Long> hostIds = new ArrayList<>();
                        hostIds.add(hostId);
                        offlineHost.put(servirId, hostIds);
                    }
                });
            });
        }
        affectedServirIds.forEach(servirId -> {
            //查询对应服务
            Servir servir = servirService.getById(servirId);
            AffectedServirListVo affectedServirListVo = BeanCopyUtils.copyBean(servir, AffectedServirListVo.class);

            //根据offlineContainer查找对应的容器
            List<Long> containerIds = offlineContainer.get(servirId);
            //根据服务Id查询所有掉线container
            if (!CollectionUtils.isEmpty(containerIds)) {
                List<Container> offlineContainers = containerIds.stream().map(containerId -> containerService.getById(containerId)).collect(Collectors.toList());
                List<ContainerVo> containerVos = BeanCopyUtils.copyBeanList(offlineContainers, ContainerVo.class);
                affectedServirListVo.setContainers(containerVos);
            }

            //根据offlineContainer查找对应的容器
            List<Long> hostIds = offlineHost.get(servirId);
            //根据offlineHost查找对应的物理机或虚拟机
            if (!CollectionUtils.isEmpty(hostIds)) {
                List<HostMachine> offlineHosts = hostIds.stream().map(hostId -> hostMachineService.getById(hostId)).collect(Collectors.toList());
                List<HostVo> hostVos = BeanCopyUtils.copyBeanList(offlineHosts, HostVo.class);
                affectedServirListVo.setHosts(hostVos);
            }
            result.add(affectedServirListVo);
        });
        return ResponseResult.okResult(result);
    }
}
