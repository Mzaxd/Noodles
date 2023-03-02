package com.mzaxd.noodles.util;

import cn.hutool.extra.mail.MailAccount;
import com.mzaxd.noodles.domain.entity.SystemSetting;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.exception.SystemException;
import com.mzaxd.noodles.service.SystemSettingService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author 13439
 */
@Component
public class SystemSettingUtils {

    @Resource
    private SystemSettingService systemSettingService;

    /**
     * 获取邮件服务器Bean
     * @return
     */
    public MailAccount getMailAccount() {
        //获取系统设置
        SystemSetting setting = systemSettingService.getById(1);
        MailAccount account = new MailAccount();
        if (!StringUtils.hasText(setting.getEmailPass())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        account.setFrom(setting.getServerEmail());
        account.setUser(setting.getServerEmail());
        account.setPass(setting.getEmailPass());
        return account;
    }

    public String getMailTarget() {
        //获取系统设置
        SystemSetting setting = systemSettingService.getById(1);
        //如果不配置目标邮箱的话，默认目标邮箱为服务器邮箱地址
        String target = "";
        if (StringUtils.hasText(setting.getNotificationEmail())) {
            target = setting.getNotificationEmail();
        } else {
            target = setting.getServerEmail();
        }
        return target;
    }
}
