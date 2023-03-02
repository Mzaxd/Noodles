package com.mzaxd.noodles.service.impl;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mzaxd.noodles.constant.RedisConstant;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.constant.UrlConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.EveryDayData;
import com.mzaxd.noodles.domain.entity.HostDetector;
import com.mzaxd.noodles.domain.entity.HostMachine;
import com.mzaxd.noodles.domain.message.DynamicData;
import com.mzaxd.noodles.domain.message.Server;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.exception.SystemException;
import com.mzaxd.noodles.mapper.HostDetectorMapper;
import com.mzaxd.noodles.service.HostDetectorService;
import com.mzaxd.noodles.service.HostMachineService;
import com.mzaxd.noodles.util.BeanCopyUtils;
import com.mzaxd.noodles.util.RedisCache;
import com.mzaxd.noodles.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author root
 * @description 针对表【host_detector】的数据库操作Service实现
 * @createDate 2023-02-05 13:18:46
 */
@Slf4j
@Service
public class HostDetectorServiceImpl extends ServiceImpl<HostDetectorMapper, HostDetector>
        implements HostDetectorService {

    @Resource
    private HostMachineService hostMachineService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RedisCache redisCache;

    @Override
    public List<HostDetector> getAllALiveDetectors() {
        LambdaQueryWrapper<HostMachine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HostMachine::getHostMachineState, SystemConstant.HOST_MACHINE_STATE_ONLINE);
        queryWrapper.eq(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        List<HostMachine> hostMachines = hostMachineService.list(queryWrapper);

        List<Long> hostMachineIds = hostMachines.stream().map(HostMachine::getId).collect(Collectors.toList());

        LambdaQueryWrapper<HostDetector> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(HostDetector::getHostMachineId, hostMachineIds);
        List<HostDetector> hostDetectors = list(wrapper);
        return hostDetectors;
    }

    @Override
    public Map<Long, DynamicData> getDynamicData(List<HostDetector> allAliveDetector) {
        HashMap<Long, DynamicData> map = new HashMap<>();
        for (HostDetector detector : allAliveDetector) {
            DynamicData dynamicData = (DynamicData) redisCache.getCacheObject(RedisConstant.DYNAMIC_DATA + detector.getDetectorUuid());
            map.put(detector.getHostMachineId(), dynamicData);
        }
        return map;
    }

    @Override
    public Map<Long, DynamicData> getDynamicDataByDetector(HostDetector detector) {
        HashMap<Long, DynamicData> map = new HashMap<>();
        DynamicData dynamicData = (DynamicData) redisCache.getCacheObject(RedisConstant.DYNAMIC_DATA + detector.getDetectorUuid());
        map.put(detector.getHostMachineId(), dynamicData);
        return map;
    }

    @Override
    public ResponseResult isValidUrl(String protocol, String ip, String port) {
        if (!NetUtil.isValidPort(Integer.parseInt(port))) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        String url = UrlUtil.getUrl(protocol, ip, port, UrlConstant.DETECTOR_IS_TRUE_URL);
        boolean validUrl = isValidUrl(url);
        if (validUrl) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS.getCode(), "连接成功");
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "连接失败");
    }

    @Override
    public boolean isValidUrl(String url) {
        log.info("尝试连接探测器：{}", url);
        ResponseResult response = restTemplate.getForObject(url, ResponseResult.class);
        if (response != null && response.getCode().equals(ResponseResult.okResult().getCode())) {
            log.info("连接{}成功", url);
            return true;
        }
        return false;
    }

    @Override
    public Server detectorGetInfoByUrl(String url) {
        ResponseResult response = restTemplate.getForObject(url, ResponseResult.class);
        if (response != null && response.getCode().equals(ResponseResult.okResult().getCode())) {
            log.info("获取信息成功");
            return JSON.parseObject(JSON.toJSONString(response.getData()), Server.class);
        }
        return null;
    }

    @Override
    public ResponseResult getMemInfo() {
        Map<String, Number> result = new HashMap<>();
        //当前在线数量
        int count = 0;
        //总内存大小
        int memoryTotal = 0;
        //单机最高内存使用率
        Double memoryUsedMaxRate = 0.0;
        //平均内存使用率
        Double memoryUsedAvgRate = 0.0;
        //平均内存使用率
        Double memoryTotalUsedRate = 0.0;
        //空闲内存
        Double memoryFree = 0.0;
        //已使用内存
        Double memoryUsed = 0.0;
        Map<Long, DynamicData> dynamicData = getDynamicData(getAllALiveDetectors());
        for (DynamicData data : dynamicData.values()) {
            count++;
            memoryFree += data.getMemFree();
            memoryUsed += data.getMemUsed();
            Double total = data.getMemFree() + data.getMemUsed();
            memoryTotal += total.intValue();
            double memoryUsedRate = memoryUsed / memoryTotal;
            memoryTotalUsedRate += memoryUsedRate;
            if (memoryUsedRate > memoryUsedMaxRate) {
                memoryUsedMaxRate = memoryUsedRate;
            }
        }
        memoryUsedAvgRate = memoryTotalUsedRate / count;
        result.put("memoryTotal", memoryTotal);
        result.put("memoryFree", memoryFree);
        result.put("memoryUsed", memoryUsed);
        result.put("memoryUsedMaxRate", NumberUtil.round(memoryUsedMaxRate, 4));
        result.put("memoryUsedAvgRate", NumberUtil.round(memoryUsedAvgRate, 4));
        return ResponseResult.okResult(result);
    }

}




