package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;

/**
 * @author 13439
 */
public interface DashboardService {
    /**
     * 获取仪表台实例服务实时数据
     *
     * @return
     */
    ResponseResult getInstancesRealTimeData();

    /**
     * 获取最近连接过的控制台列表
     *
     * @return
     */
    ResponseResult getRecentConsoleList();

    /**
     * 获取所有受影响的服务
     *
     * @return ResponseResult
     * @author mzaxd
     * @date 2023/3/8 9:43
     */
    ResponseResult getAffectedServirList();
}
