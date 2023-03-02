package com.mzaxd.noodles.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.EveryDayData;
import com.mzaxd.noodles.mapper.EveryDayDataMapper;
import com.mzaxd.noodles.service.EveryDayDataService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* @author 13439
* @description 针对表【every_data】的数据库操作Service实现
* @createDate 2023-02-21 12:52:12
*/
@Service
public class EveryDayDataServiceImpl extends ServiceImpl<EveryDayDataMapper, EveryDayData>
    implements EveryDayDataService {

    @Override
    public ResponseResult getAuditLogCountYesterday() {
        ArrayList<Integer> result = new ArrayList<>();
        for (EveryDayData lastWeekDatum : getLastWeekData()) {
            result.add(lastWeekDatum.getAuditCount());
        }
        return ResponseResult.okResult(result);
    }

    @Override
    public ResponseResult getInstancesHistory() {
        EveryDayData yesterdayData = getYesterdayData();
        HashMap<String, Integer> result = new HashMap<>();
        result.put("hostCount", yesterdayData.getHostCount());
        result.put("hostOnlineCount", yesterdayData.getHostOnlineCount());
        result.put("hostOfflineCount", yesterdayData.getHostOfflineCount());
        result.put("hostUnknownCount", yesterdayData.getHostUnknownCount());
        result.put("vmCount", yesterdayData.getVmCount());
        result.put("vmOnlineCount", yesterdayData.getVmOnlineCount());
        result.put("vmOfflineCount", yesterdayData.getVmOfflineCount());
        result.put("vmUnknownCount", yesterdayData.getVmUnknownCount());
        result.put("containerCount", yesterdayData.getContainerCount());
        result.put("containerOnlineCount", yesterdayData.getContainerOnlineCount());
        result.put("containerOfflineCount", yesterdayData.getContainerOfflineCount());
        result.put("containerUnknownCount", yesterdayData.getContainerUnknownCount());
        return ResponseResult.okResult(result);
    }

    private LambdaQueryWrapper<EveryDayData> getYesterdayLambdaWrapper() {
        LambdaQueryWrapper<EveryDayData> everyDayDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        everyDayDataLambdaQueryWrapper.ge(EveryDayData::getCreateTime, DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -1)));
        everyDayDataLambdaQueryWrapper.lt(EveryDayData::getCreateTime, DateUtil.endOfDay(DateUtil.offsetDay(DateUtil.date(), -1)));
        return everyDayDataLambdaQueryWrapper;
    }

    @Override
    public EveryDayData getYesterdayData() {
        return getOne(getYesterdayLambdaWrapper());
    }

    private LambdaQueryWrapper<EveryDayData> getLastWeekLambdaWrapper() {
        LambdaQueryWrapper<EveryDayData> everyDayDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        everyDayDataLambdaQueryWrapper.ge(EveryDayData::getCreateTime, DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -7)));
        everyDayDataLambdaQueryWrapper.lt(EveryDayData::getCreateTime, DateUtil.endOfDay(DateUtil.offsetDay(DateUtil.date(), -1)));
        return everyDayDataLambdaQueryWrapper;
    }

    @Override
    public List<EveryDayData> getLastWeekData() {
        return list(getLastWeekLambdaWrapper());
    }

    private LambdaQueryWrapper<EveryDayData> getLastSixDayLambdaWrapper() {
        LambdaQueryWrapper<EveryDayData> everyDayDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        everyDayDataLambdaQueryWrapper.ge(EveryDayData::getCreateTime, DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -6)));
        everyDayDataLambdaQueryWrapper.lt(EveryDayData::getCreateTime, DateUtil.endOfDay(DateUtil.offsetDay(DateUtil.date(), -1)));
        return everyDayDataLambdaQueryWrapper;
    }

    @Override
    public List<EveryDayData> getLastSixDayData() {
        return list(getLastSixDayLambdaWrapper());
    }
}




