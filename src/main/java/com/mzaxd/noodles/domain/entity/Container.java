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
 * @TableName container
 */
@TableName(value ="container")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Container implements Serializable {
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
     * 镜像名
     */
    private String imageName;

    /**
     * WebUi URL
     */
    private String webUi;

    /**
     * 宿主机id
     */
    private Long hostMachineId;

    /**
     * ssh连接信息表id
     */
    private Long sshId;

    /**
     * 状态（0运行 1停止 2暂停 3未知）
     */
    private Integer containerState;

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

    /**
     * 容器编号
     */
    private String containerId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}