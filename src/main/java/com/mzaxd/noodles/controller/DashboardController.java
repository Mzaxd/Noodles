package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.service.DashboardService;
import com.mzaxd.noodles.service.EveryDayDataService;
import com.mzaxd.noodles.service.HostDetectorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 13439
 */

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private HostDetectorService detectorService;

    @Resource
    private EveryDayDataService everyDayDataService;

    @Resource
    private DashboardService dashboardService;

    @GetMapping("/getMemInfo")
    public ResponseResult getMemInfo() {
        return detectorService.getMemInfo();
    }

    @GetMapping("/getAuditLogCountYesterday")
    public ResponseResult getAuditLogCountYesterday() {
        return everyDayDataService.getAuditLogCountYesterday();
    }

    @GetMapping("/getInstancesHistory")
    public ResponseResult getInstancesHistory() {
        return everyDayDataService.getInstancesHistory();
    }

    @GetMapping("/getInstancesRealTimeData")
    public ResponseResult getInstancesRealTimeData() {
        return dashboardService.getInstancesRealTimeData();
    }

    @GetMapping("/getRecentConsoleList")
    public ResponseResult getRecentConsoleList() {
        return dashboardService.getRecentConsoleList();
    }

    @GetMapping("/getAffectedServirList")
    public ResponseResult getAffectedServirList() {
        return dashboardService.getAffectedServirList();
    }

}
