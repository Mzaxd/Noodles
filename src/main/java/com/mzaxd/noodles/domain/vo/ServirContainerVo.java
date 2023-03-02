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
public class ServirContainerVo {

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
     * WebUi URL
     */
    private String webUi;

}
