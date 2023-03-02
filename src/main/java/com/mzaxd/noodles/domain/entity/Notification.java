package com.mzaxd.noodles.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 * @TableName notification
 */
@TableName(value ="notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Notification implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型（0代表掉线通知，1代表统计通知）
     */
    private Integer type;

    /**
     * 可能关联的实例的类型（0代表无 1代表host 2代表vm 3代表container）
     */
    private Integer instanceType;

    /**
     * 可能关联的实例id
     */
    private Long instanceId;

    /**
     * 提醒发送方式（0不提醒 1浏览器 1邮件 3代表两者）
     */
    private Integer sendType;

    /**
     * 用户是否确认（0代表未确认 1代表已确认）
     */
    private Integer affirm;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 删除标志（0代表未删除，1代表已删除）
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}