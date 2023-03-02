package com.mzaxd.noodles.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author 13439
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogListVo {

    /**
     * id
     */
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
    private Date createTime;

    /**
     * 创建人id
     */
    private Long createBy;

    /**
     * 操作人信息
     */
    private UserInfoVo user;

}
