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
public class SystemVo {

    /**
     * 过期时间(1、3、7、30)
     */
    private Integer logExpire;

    /**
     * 检查实例状态间隔(秒)
     */
    private Integer checkInstanceStatePeriod;

    /**
     * ch/en
     */
    private String defaultLang;

}
