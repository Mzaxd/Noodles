package com.mzaxd.noodles.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName system_setting
 */
@TableName(value ="system_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSetting implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 过期时间(1、3、7、30)
     */
    private Integer logExpire;

    /**
     * 检查实例状态间隔(秒)
     */
    private Integer checkInstanceStatePeriod;

    /**
     * ch/en
     */
    private String defaultLang;

    /**
     * 发送邮箱
     */
    private String serverEmail;

    /**
     * 邮箱密码
     */
    private String emailPass;

    /**
     * 接收邮箱
     */
    private String notificationEmail;

    /**
     * 控制台渲染类型
     */
    private String rendererType;

    /**
     * 字体大小
     */
    private Integer fontSize;

    /**
     * 光标是否闪烁（0关1开）
     */
    private Integer cursorBlink;

    /**
     * 字体颜色
     */
    private String foreground;

    /**
     * 背景色
     */
    private String background;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}