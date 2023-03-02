package com.mzaxd.noodles.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.AuditLog;
import com.mzaxd.noodles.domain.entity.EveryDayData;
import com.mzaxd.noodles.domain.vo.RecentConsoleListVo;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.*;
import com.mzaxd.noodles.util.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
}
