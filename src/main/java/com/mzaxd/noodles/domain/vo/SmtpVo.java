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
public class SmtpVo {
    /**
     * 发送邮箱
     */
    private String serverEmail;

    /**
     * 邮箱密码
     */
    private String emailPass;

    /**
     * 接收邮箱
     */
    private String notificationEmail;
}
