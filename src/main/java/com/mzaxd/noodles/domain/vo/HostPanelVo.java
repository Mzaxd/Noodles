package com.mzaxd.noodles.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 13439
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class HostPanelVo {

    /**
     * 主键id
     */
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
     * 管理ip地址
     */
    private String manageIp;

    /**
     * 协议
     */
    private String protocol;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 端口
     */
    private String port;

    /**
     * 1代表Linux 2代表WindowsNT 3代表FreeBSD
     */
    private Integer osKernel;

    /**
     * 状态（0不提醒 1浏览器 2邮件 3浏览器&邮件）
     */
    private Integer notify;

    /**
     * 状态（0在线 1离线 2睡眠）
     */
    private Integer hostMachineState;

    /**
     * 虚拟机数量
     */
    private Integer vmCount;

    /**
     * 容器数量
     */
    private Integer containerCount;

}
