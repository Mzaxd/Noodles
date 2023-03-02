package com.mzaxd.noodles.constant;

/**
 * @author root
 */
public class SystemConstant {

    /**
     * 宿主机id等于0表示没有宿主机，即物理机
     */
    public static final Integer PHYSICAL_MACHINE = 0;

    /**
     * 主机在线
     */
    public static final Integer HOST_MACHINE_STATE_ONLINE = 0;

    /**
     * 主机离线
     */
    public static final Integer HOST_MACHINE_STATE_OFFLINE = 1;

    /**
     * 主机睡眠
     */
    public static final Integer HOST_MACHINE_STATE_SLEEP = 2;

    /**
     * 主机状态未知
     */
    public static final Integer HOST_MACHINE_STATE_UNKNOWN = 3;

    /**
     * 物理机 host_machine_id 是 0
     */
    public static final Long HOST_MACHINE_ID_HOST = 0L;

    /**
     * 成功id
     */
    public static final Integer SUCCESS_CODE = 200;

    /**
     * 容器正在运行
     */
    public static final Integer CONTAINER_STATE_RUNNING = 0;

    /**
     * 容器停止
     */
    public static final Integer CONTAINER_STATE_EXITED = 1;

    /**
     * 容器暂停
     */
    public static final Integer CONTAINER_STATE_PAUSED = 2;

    /**
     * 容器状态未知
     */
    public static final Integer CONTAINER_STATE_UNKNOWN = 3;

    /**
     * 正常状态（非第一次使用） 通用常量
     */
    public static final Integer NORMAL_STATE = 1;

    /**
     * 日志类型为用户
     */
    public static final String LOG_USER = "用户";

    /**
     * 提醒方式-不提醒
     */
    public static final Integer NOTIFY_NO = 0;

    /**
     * 提醒方式-浏览器
     */
    public static final Integer NOTIFY_BROWSER = 1;

    /**
     * 提醒方式-邮件
     */
    public static final Integer NOTIFY_EMAIL = 2;

    /**
     * 提醒方式-浏览器和邮件
     */
    public static final Integer NOTIFY_BROWSER_EMAIL = 3;

    /**
     * 通知类型 掉线
     */
    public static final Integer OFFLINE_NOTIFICATION = 0;

    /**
     * 通知类型 统计
     */
    public static final Integer STATISTICS_NOTIFICATION = 1;

    /**
     * 未处理的通知
     */
    public static final Integer NOTIFICATION_NOT_AFFIRM = 0;

    /**
     * 已处理的通知
     */
    public static final Integer NOTIFICATION_AFFIRM = 1;

    /**
     * 前端页面通知提醒页面对应的tab
     */
    public static final Integer FRONTEND_NOTIFICATION_NOT_AFFIRM = 0;

    /**
     * 前端页面通知提醒页面对应的tab
     */
    public static final Integer FRONTEND_NOTIFICATION_AFFIRM = 1;

    /**
     * 前端页面通知提醒页面对应的tab
     */
    public static final Integer FRONTEND_NOTIFICATION_ALL = 2;

    /**
     * 前端页面通知提醒页面对应的tab
     */
    public static final Integer FRONTEND_NOTIFICATION_TYPE_OFFLINE = 3;

    /**
     * 前端页面通知提醒页面对应的tab
     */
    public static final Integer FRONTEND_NOTIFICATION_TYPE_STATISTICS = 4;

    /**
     * 通知实例类型 无
     */
    public static final Integer INSTANCETYPE_NONE = 0;

    /**
     * 通知实例类型 物理机
     */
    public static final Integer INSTANCETYPE_HOST = 1;

    /**
     * 通知实例类型 虚拟机
     */
    public static final Integer INSTANCETYPE_VM = 2;

    /**
     * 通知实例类型 容器
     */
    public static final Integer INSTANCETYPE_CONTAINER = 3;



}
