package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.EveryDayData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 13439
* @description 针对表【every_data】的数据库操作Service
* @createDate 2023-02-21 12:52:12
*/
public interface EveryDayDataService extends IService<EveryDayData> {

    /**
     * 返回昨日操作次数
     *
     * @return
     */
    ResponseResult getAuditLogCountYesterday();

    /**
     * 返回昨日实例情况
     *
     * @return
     */
    ResponseResult getInstancesHistory();

    /**
     * 获取昨天的每日数据
     *
     * @return
     */
    EveryDayData getYesterdayData();

    /**
     * 获取上周的每日数据
     *
     * @return
     */
    List<EveryDayData> getLastWeekData();

    /**
     * 获取前六天的每日数据
     *
     * @return
     */
    List<EveryDayData> getLastSixDayData();

}
