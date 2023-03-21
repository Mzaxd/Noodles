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
public enum SearchItem {

    /**
     * HOST
     */
    HOST(SearchCategory.INSTANCE.title, "tabler-server", 1, "物理机", Url.HOST),

    /**
     * VM
     */
    VM(SearchCategory.INSTANCE.title, "tabler-chart-bubble", 1, "虚拟机", Url.VM),

    /**
     * CONTAINER
     */
    CONTAINER(SearchCategory.INSTANCE.title, "tabler-brand-docker", 1, "容器", Url.CONTAINER),

    /**
     * DASHBOARD
     */
    DASHBOARD(SearchCategory.STATISTICS.title, "tabler-dashboard", 1, "仪表盘", Url.DASHBOARD),

    /**
     * AUDIT_LOG
     */
    AUDIT_LOG(SearchCategory.STATISTICS.title, "tabler-book", 1, "操作记录", Url.AUDIT_LOG),

    /**
     * PANEL_MANAGEMENT
     */
    PANEL_MANAGEMENT(SearchCategory.SETTING.title, "tabler-settings", 1, "面板管理", Url.PANEL_MANAGEMENT),

    /**
     * ACCOUNT_SETTING
     */
    ACCOUNT_SETTING(SearchCategory.SETTING.title, "tabler-user", 1, "账号设置", Url.ACCOUNT_MANAGEMENT),

    /**
     * NOTIFICATION
     */
    NOTIFICATION(SearchCategory.OTHER.title, "tabler-bell", 1, "通知提醒", Url.NOTIFICATION),

    /**
     * HELP
     */
    HELP(SearchCategory.OTHER.title, "tabler-help", 1, "帮助文档", Url.HELP);

    final String category;

    final String icon;

    final Integer id;

    final String title;

    final Url url;

    SearchItem(String category, String icon, Integer id, String title, Url url) {
        this.category = category;
        this.icon = icon;
        this.id = id;
        this.title = title;
        this.url = url;
    }
}
