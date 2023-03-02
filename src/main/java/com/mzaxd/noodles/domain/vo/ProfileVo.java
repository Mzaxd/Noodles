package com.mzaxd.noodles.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author 13439
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileVo {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 总服务数
     */
    private Integer servirNumber;

    /**
     * 总物理机数
     */
    private Integer hostNumber;

    /**
     * 总虚拟机数
     */
    private Integer vmNumber;

    /**
     * 总容器数
     */
    private Integer containerNumber;
}
