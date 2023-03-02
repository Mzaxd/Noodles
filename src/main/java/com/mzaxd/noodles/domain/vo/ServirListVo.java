package com.mzaxd.noodles.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 13439
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ServirListVo {

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
    private List<ServirHostVo> hosts;

    /**
     * 所有关联的容器
     */
    private List<ServirContainerVo> containers;

    /**
     * 所有标签
     */
    private List<TagVo> tags;

}
