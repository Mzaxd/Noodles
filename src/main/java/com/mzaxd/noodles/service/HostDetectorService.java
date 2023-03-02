package com.mzaxd.noodles.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.HostDetector;
import com.mzaxd.noodles.domain.message.DynamicData;
import com.mzaxd.noodles.domain.message.Server;

import java.util.List;
import java.util.Map;

/**
* @author root
* @description 针对表【host_detector】的数据库操作Service
* @createDate 2023-02-05 13:18:46
*/
public interface HostDetectorService extends IService<HostDetector> {

    /**
     * 判断探测器IP是否有效
     *
     * @author mzaxd
     * @date 2/7/23 6:32 AM
     * @param url
     * @return ResponseResult
     */
    boolean isValidUrl(String url);

    /**
     * 获取所有在线的探测器
     *
     * @author mzaxd
     * @date 2/8/23 4:38 AM
     * @return HostDetector
     */
    List<HostDetector> getAllALiveDetectors();

    /**
     * 返回所有探测器获取动态数据
     *
     * @author mzaxd
     * @date 2/8/23 6:28 AM
     * @param allAliveDetector
     * @return Map<DynamicData>
     */
    Map<Long, DynamicData> getDynamicData(List<HostDetector> allAliveDetector);

    /**
     * 指定探测器获取动态数据
     *
     * @author mzaxd
     * @date 2/8/23 6:28 AM
     * @param detector
     * @return Map<DynamicData>
     */
    Map<Long, DynamicData> getDynamicDataByDetector(HostDetector detector);

    /**
     * 检查是否是有效探测器地址
     *
     * @param protocol
     * @param ip
     * @param port
     * @return
     */
    ResponseResult isValidUrl(String protocol, String ip, String port);

    /**
     * 根据url获取远程主机信息
     *
     * @param url
     * @return
     */
    Server detectorGetInfoByUrl(String url);

    /**
     * 返回内存整体使用信息
     *
     * @return
     */
    ResponseResult getMemInfo();
}
