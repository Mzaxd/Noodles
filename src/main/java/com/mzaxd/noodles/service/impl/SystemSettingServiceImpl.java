package com.mzaxd.noodles.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.SystemSetting;
import com.mzaxd.noodles.domain.vo.SmtpVo;
import com.mzaxd.noodles.domain.vo.SystemVo;
import com.mzaxd.noodles.domain.vo.TerminalVo;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.service.SystemSettingService;
import com.mzaxd.noodles.mapper.SystemSettingMapper;
import com.mzaxd.noodles.util.BeanCopyUtils;
import org.springframework.stereotype.Service;

/**
* @author 13439
* @description 针对表【system_setting】的数据库操作Service实现
* @createDate 2023-02-16 20:25:57
*/
@Service
public class SystemSettingServiceImpl extends ServiceImpl<SystemSettingMapper, SystemSetting>
    implements SystemSettingService{

    @Override
    public ResponseResult getSmtpSetting() {
        SystemSetting systemSetting = getSetting();
        SmtpVo smtpVo = BeanCopyUtils.copyBean(systemSetting, SmtpVo.class);
        return ResponseResult.okResult(smtpVo);
    }

    @Override
    public ResponseResult saveSmtpSetting(SmtpVo smtpVo) {
        SystemSetting systemSetting = getById(1);
        systemSetting.setServerEmail(smtpVo.getServerEmail());
        systemSetting.setEmailPass(smtpVo.getEmailPass());
        systemSetting.setNotificationEmail(smtpVo.getNotificationEmail());
        saveOrUpdate(systemSetting);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult getTerminalSetting() {
        SystemSetting systemSetting = getSetting();
        TerminalVo terminalVo = BeanCopyUtils.copyBean(systemSetting, TerminalVo.class);
        return ResponseResult.okResult(terminalVo);
    }

    @Override
    public ResponseResult saveTerminalSetting(TerminalVo terminalVo) {
        SystemSetting systemSetting = getById(1);
        systemSetting.setRendererType(terminalVo.getRendererType());
        systemSetting.setFontSize(terminalVo.getFontSize());
        systemSetting.setCursorBlink(terminalVo.getCursorBlink());
        systemSetting.setForeground(terminalVo.getForeground());
        systemSetting.setBackground(terminalVo.getBackground());
        saveOrUpdate(systemSetting);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult getSystemSetting() {
        SystemSetting systemSetting = getSetting();
        SystemVo systemVo = BeanCopyUtils.copyBean(systemSetting, SystemVo.class);
        return ResponseResult.okResult(systemVo);
    }

    @Override
    public ResponseResult saveSystemSetting(SystemVo systemVo) {
        SystemSetting systemSetting = getById(1);
        systemSetting.setLogExpire(systemVo.getLogExpire());
        systemSetting.setDefaultLang(systemVo.getDefaultLang());
        systemSetting.setCheckInstanceStatePeriod(systemVo.getCheckInstanceStatePeriod());
        saveOrUpdate(systemSetting);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private SystemSetting getSetting(){
        return getById(1);
    }
}




