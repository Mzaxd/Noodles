package com.mzaxd.noodles.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.EveryDayData;
import com.mzaxd.noodles.mapper.EveryDayDataMapper;
import com.mzaxd.noodles.service.EveryDayDataService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

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
        EveryDayData everyData = getOne(getYesterdayLambdaWrapper());
        if (Objects.nonNull(everyData)) {
            return everyData;
        } else {
            everyData = createDefaultEverydayData();
        }
        return everyData;
    }

    public EveryDayData createDefaultEverydayData() {
        EveryDayData everyDayData = new EveryDayData();
        everyDayData.setHostCount(0).setHostOnlineCount(0).setHostOfflineCount(0).setHostUnknownCount(0)
                .setVmCount(0).setVmOnlineCount(0).setVmOfflineCount(0).setVmUnknownCount(0)
                .setContainerCount(0).setContainerOnlineCount(0).setContainerOfflineCount(0).setContainerUnknownCount(0)
                .setAuditCount(0).setServirCount(0);
        return everyDayData;
    }

    private LambdaQueryWrapper<EveryDayData> getLastWeekLambdaWrapper() {
        LambdaQueryWrapper<EveryDayData> everyDayDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        everyDayDataLambdaQueryWrapper.ge(EveryDayData::getCreateTime, DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -7)));
        everyDayDataLambdaQueryWrapper.lt(EveryDayData::getCreateTime, DateUtil.endOfDay(DateUtil.offsetDay(DateUtil.date(), -1)));
        return everyDayDataLambdaQueryWrapper;
    }

    @Override
    public List<EveryDayData> getLastWeekData() {
        return getLastDate(7);
    }

    public List<EveryDayData> getLastDate(int dayNum) {
        // 今天的前一天是截止日期
        LocalDate end = LocalDate.now().minusDays(1);
        // 七天前是起始日期
        LocalDate start = end.minusDays(6);
        List<EveryDayData> list = list(getLastWeekLambdaWrapper());

        // 如果不足7条，则补充空数据
        int missingDays = dayNum - list.size();
        if (missingDays > 0) {
            List<EveryDayData> emptyData = new ArrayList<>();
            for (int i = 0; i < missingDays; i++) {
                EveryDayData data = createDefaultEverydayData();
                data.setCreateTime(Date.from(start.plusDays(i).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                emptyData.add(data);
            }
            emptyData.addAll(list);
            list = emptyData;
        }
        return list;
    }


    private LambdaQueryWrapper<EveryDayData> getLastSixDayLambdaWrapper() {
        LambdaQueryWrapper<EveryDayData> everyDayDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        everyDayDataLambdaQueryWrapper.ge(EveryDayData::getCreateTime, DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -6)));
        everyDayDataLambdaQueryWrapper.lt(EveryDayData::getCreateTime, DateUtil.endOfDay(DateUtil.offsetDay(DateUtil.date(), -1)));
        return everyDayDataLambdaQueryWrapper;
    }

    @Override
    public List<EveryDayData> getLastSixDayData() {
        return getLastDate(6);
    }
}




