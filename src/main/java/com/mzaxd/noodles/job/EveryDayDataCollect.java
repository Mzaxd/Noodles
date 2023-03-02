package com.mzaxd.noodles.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.domain.entity.AuditLog;
import com.mzaxd.noodles.domain.entity.Container;
import com.mzaxd.noodles.domain.entity.EveryDayData;
import com.mzaxd.noodles.domain.entity.HostMachine;
import com.mzaxd.noodles.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author 13439
 */

@Slf4j
@Component
public class EveryDayDataCollect {

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
    @Scheduled(cron = "0 59 23 * * ?")
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
        everyDayDataService.save(everyDayData);
    }
}
