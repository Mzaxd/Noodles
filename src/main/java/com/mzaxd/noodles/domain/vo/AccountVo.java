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
public class AccountVo {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 名称
     */
    private String name;

    /**
     * 0代表管理员 1代表非管理员
     */
    private Integer type;

    /**
     * 描述
     */
    private String description;
}
