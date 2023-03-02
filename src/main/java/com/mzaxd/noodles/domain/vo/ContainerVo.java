package com.mzaxd.noodles.domain.vo;

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
public class ContainerVo {

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
     * 宿主机信息
     */
    private HostMachineVo hostMachine;

    /**
     * 状态（0在线 1离线 2孤立镜像）
     */
    private Integer containerState;

    /**
     * 容器编号
     */
    private String containerId;

    /**
     * 状态（0不提醒 1浏览器 2邮件 3浏览器&邮件）
     */
    private Integer notify;

    /**
     * SSH Id
     */
    private Long sshId;

    /**
     * 控制台类型
     */
    private String sshType;

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
