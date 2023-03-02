package com.mzaxd.noodles.enums;

/**
 * 操作日志-操作类型
 *
 * @author 13439
 */

public enum OperationEnum {

    /**
     * 新增物理机
     */
    HOST_ADD("新增物理机", "物理机"),

    /**
     * 修改物理机
     */
    HOST_UPDATE("修改物理机", "物理机"),

    /**
     * 删除物理机
     */
    HOST_DELETE("删除物理机", "物理机"),

    /**
     * 新增虚拟机
     */
    VM_ADD("新增虚拟机", "虚拟机"),

    /**
     * 修改虚拟机
     */
    VM_UPDATE("修改虚拟机", "虚拟机"),

    /**
     * 删除虚拟机
     */
    VM_DELETE("删除虚拟机", "虚拟机"),

    /**
     * 新增容器
     */
    CONTAINER_ADD("新增容器", "容器"),

    /**
     * 修改容器
     */
    CONTAINER_UPDATE("修改容器", "容器"),

    /**
     * 删除容器
     */
    CONTAINER_DELETE("删除容器", "容器"),

    /**
     * 新增服务
     */
    SERVIR_ADD("新增容器", "服务"),

    /**
     * 修改服务
     */
    SERVIR_UPDATE("修改容器", "服务"),

    /**
     * 删除服务
     */
    SERVIR_DELETE("删除容器", "服务"),

    /**
     * 第一次登录
     */
    USER_FIRST_USE("第一次登录", "用户"),

    /**
     * 更改账号信息
     */
    USER_UPDATE_USERINFO("更改账号信息", "用户"),

    /**
     * 更改邮箱账号
     */
    USER_UPDATE_EMAIL("更改邮箱账号", "用户"),

    /**
     * 更改密码
     */
    USER_UPDATE_PASSWORD("更改密码", "用户"),

    /**
     * 确认提醒
     */
    NOTIFICATION_AFFIRM("确认提醒", "提醒"),

    /**
     * 确认提醒
     */
    CONSOLE_CONNECT("连接控制台", "控制台"),

    /**
     * 默认操作
     */
    DEFAULT("默认操作", "默认操作类型");

    final String operation;
    final String operationType;

    OperationEnum(String operation, String operationType) {
        this.operation = operation;
        this.operationType = operationType;
    }

    public String getOperation() {
        return operation;
    }

    public String getOperationType() {
        return operationType;
    }
}
