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
 * @author 13439
 * @TableName host_machine
 */
@TableName(value ="host_machine")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class HostMachine implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 图像
     */
    private String avatar;

    /**
     * ssh连接信息表id
     */
    private Long sshId;

    /**
     * 操作系统id
     */
    private Long osId;

    /**
     * 管理ip地址
     */
    private String manageIp;

    /**
     * 宿主机id（0代表物理机没有宿主机）
     */
    private Long hostMachineId;

    /**
     * 核心线程数
     */
    private Long threads;

    /**
     * 内存
     */
    private Long memory;

    /**
     * 状态（0在线 1离线 2睡眠 3未知）
     */
    private Integer hostMachineState;

    /**
     * 提醒方式（0不提醒 1浏览器 2邮件 3浏览器&邮件）
     */
    private Integer notify;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建人id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 更新人id
     */
    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;

    /**
     * 删除标志（0代表未删除，1代表已删除）
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}