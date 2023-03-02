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
}
