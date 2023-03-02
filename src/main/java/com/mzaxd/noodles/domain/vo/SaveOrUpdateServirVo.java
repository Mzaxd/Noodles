package com.mzaxd.noodles.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author 13439
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveOrUpdateServirVo {

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
     * 容器id
     */
    private List<Integer> containerIds;

    /**
     * 主机id
     */
    private List<Integer> hostIds;

    /**
     * 标签id
     */
    private List<Integer> tagIds;

}
