package com.mzaxd.noodles.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 * @author 13439
 * @TableName audit_log
 */
@TableName(value ="audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(of = "param")
public class AuditLog implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作
     */
    private String operation;

    /**
     * 操作类型（用户/容器/虚拟机/物理机/服务/系统）
     */
    private String operationType;

    /**
     * 请求参数
     */
    private String param;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}