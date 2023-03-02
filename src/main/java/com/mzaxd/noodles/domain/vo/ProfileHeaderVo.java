package com.mzaxd.noodles.domain.vo;

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
public class ProfileHeaderVo {

    /**
     * 用户名
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 登录地点
     */
    private String location;

    /**
     * 账号创建时间
     */
    private Date createTime;
}
