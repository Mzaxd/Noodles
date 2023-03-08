package com.mzaxd.noodles.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Mzaxd
 * @since 2023-03-08 10:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AffectedServirListVo {

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
     * 备注
     */
    private String remark;

    /**
     * 所有关联的host
     */
    private List<HostVo> hosts;

    /**
     * 所有关联的容器
     */
    private List<ContainerVo> containers;

}
