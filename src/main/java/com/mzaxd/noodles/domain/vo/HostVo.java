package com.mzaxd.noodles.domain.vo;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author root
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class HostVo {
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
     * 操作系统id
     */
    private Long sshId;

    /**
     * SSH连接地址
     */
    private String sshHost;

    /**
     * SSH连接地址端口号
     */
    private Integer sshPort;

    /**
     * SSH连接用户
     */
    private String sshUser;

    /**
     * SSH连接密码
     */
    private String sshPwd;

}

