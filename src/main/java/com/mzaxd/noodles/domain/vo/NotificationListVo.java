package com.mzaxd.noodles.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author 13439
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NotificationListVo {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型（0代表掉线通知，1代表统计通知）
     */
    private Integer type;

    /**
     * 可能关联的实例的类型（0代表无 1代表host 2代表vm 3代表container）
     */
    private Integer instanceType;

    /**
     * 可能关联的实例id
     */
    private Long instanceId;

    /**
     * 提醒发送方式（0不提醒 1浏览器 1邮件 3代表两者）
     */
    private Integer sendType;

    /**
     * 用户是否确认（0代表未确认 1代表已确认）
     */
    private Integer affirm;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 关联的实例名称
     */
    private String instanceName;

    /**
     * 关联的实例图标
     */
    private String instanceAvatar;
}
