package com.mzaxd.noodles.constant;

/**
 * @author root
 */
public class UrlConstant {

    /**
     * isTrueUrl判断地址是否有效的链接
     */
    public static final String DETECTOR_IS_TRUE_URL = "/isTrueUrl";

    /**
     * 获取远程主机信息链接
     */
    public static final String DETECTOR_GET_INFO = "/getInfo";

    /**
     * 获取远程主机信息链接
     */
    public static final String DETECTOR_GET_DISK_INFO = "/getDiskInfo";

    /**
     * 获取远程主机信息链接
     */
    public static final String DETECTOR_GET_NETWORK_IF_INFO = "/getNetworkIfInfo";

    /**
     * 获取探测器uuid
     */
    public static final String DETECTOR_GET_DETECTOR_ID = "/getDetectorID";

    /**
     * 通知探测器开始发送动态数据
     */
    public static final String DETECTOR_START_SEND_DYNAMIC_DATA = "/startSendDynamicDataFromMq";

    /**
     * 通知探测器停止发送动态数据
     */
    public static final String DETECTOR_STOP_SEND_DYNAMIC_DATA = "/stopSendDynamicDataFromMq";

    /**
     * 协议
     */
    public static final String PROTOCOL = "protocol";

    /**
     * ip
     */
    public static final String IP = "IP";

    /**
     * 端口号
     */
    public static final String PORT = "port";
}
