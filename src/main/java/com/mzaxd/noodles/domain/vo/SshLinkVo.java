package com.mzaxd.noodles.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 13439
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SshLinkVo {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 账号
     */
    private String host;

    /**
     * 名称
     */
    private String name;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 密码
     */
    private String password;

    /**
     * 控制台类型（bash/sh）
     */
    private String consoleType;

}
