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
 * @TableName every_data
 */
@TableName(value ="everyday_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EveryDayData implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主机总数
     */
    private Integer hostCount;

    /**
     * 主机在线数量
     */
    private Integer hostOnlineCount;

    /**
     * 主机离线数量
     */
    private Integer hostOfflineCount;

    /**
     * 主机状态未知数量
     */
    private Integer hostUnknownCount;

    /**
     * 虚拟机总数
     */
    private Integer vmCount;

    /**
     * 虚拟机在线数量
     */
    private Integer vmOnlineCount;

    /**
     * 虚拟机离线数量
     */
    private Integer vmOfflineCount;

    /**
     * 虚拟机状态未知数量
     */
    private Integer vmUnknownCount;

    /**
     * 容器总数
     */
    private Integer containerCount;

    /**
     * 容器在线数量
     */
    private Integer containerOnlineCount;

    /**
     * 容器离线数量
     */
    private Integer containerOfflineCount;

    /**
     * 容器状态未知数量
     */
    private Integer containerUnknownCount;

    /**
     * 服务总数
     */
    private Integer servirCount;

    /**
     * 操作数
     */
    private Integer auditCount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}