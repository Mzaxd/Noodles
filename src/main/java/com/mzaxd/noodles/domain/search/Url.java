package com.mzaxd.noodles.domain.search;

import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

/**
 * @author ThinkBook
 */
@Getter
@ToString
@JSONType(serializeEnumAsJavaBean = true)
public enum Url {

    /**
     * 物理机
     */
    HOST("instances-server-list"),

    /**
     * 虚拟机
     */
    VM("instances-vm-list"),

    /**
     * 容器
     */
    CONTAINER("instances-container-list"),

    /**
     * 仪表盘
     */
    DASHBOARD("dashboard"),

    /**
     * 操作日志
     */
    AUDIT_LOG("history"),

    /**
     * 面板管理
     */
    PANEL_MANAGEMENT("setting-panel-tab-system"),

    /**
     * 账号管理
     */
    ACCOUNT_MANAGEMENT("user-profile-tab-profile"),

    /**
     * 提醒
     */
    NOTIFICATION("notification"),

    /**
     * 帮助
     */
    HELP("help-center");

    /**
     * 跳转链接
     */
    final String name;

    Url(String name) {
        this.name = name;
    }
}
