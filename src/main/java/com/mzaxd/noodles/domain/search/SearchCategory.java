package com.mzaxd.noodles.domain.search;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.ToString;

/**
 * @author ThinkBook
 */
@Getter
@ToString
@JSONType(serializeEnumAsJavaBean = true)
public enum SearchCategory {

    /**
     * 实例统计
     */
    INSTANCE("实例统计", "实例统计"),

    /**
     * 统计
     */
    STATISTICS("统计", "统计"),

    /**
     * 设置
     */
    SETTING("设置", "设置"),

    /**
     * 其他
     */
    OTHER("其他", "其他");

    /**
     * 搜索标题
     */
    final String header;

    /**
     * 搜搜名字
     */
    final String title;

    SearchCategory(String header, String title) {
        this.header = header;
        this.title = title;
    }

}
