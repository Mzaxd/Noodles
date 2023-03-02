package com.mzaxd.noodles.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author root
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirtualMachineListVo {
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
     * 控制台id
     */
    private Long sshId;

    /**
     * 操作系统id
     */
    private Long osId;

    /**
     * 操作系统Vo
     */
    private OsVo os;

    /**
     * 管理ip地址
     */
    private String manageIp;

    /**
     * 核心线程数
     */
    private Long threads;

    /**
     * 内存
     */
    private Long memory;

    /**
     * 状态（0在线 1离线 2睡眠）
     */
    private Integer hostMachineState;

    /**
     * 状态（0不提醒 1浏览器 2邮件 3浏览器&邮件）
     */
    private Integer notify;

}
