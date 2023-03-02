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
public class HostMachineVo {

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

}
