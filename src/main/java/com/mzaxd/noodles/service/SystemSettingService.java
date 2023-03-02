package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.SystemSetting;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mzaxd.noodles.domain.vo.SmtpVo;
import com.mzaxd.noodles.domain.vo.SystemVo;
import com.mzaxd.noodles.domain.vo.TerminalVo;

/**
* @author 13439
* @description 针对表【system_setting】的数据库操作Service
* @createDate 2023-02-16 20:25:57
*/
public interface SystemSettingService extends IService<SystemSetting> {

    /**
     * 获取SMTP服务器设置
     *
     * @return
     */
    ResponseResult getSmtpSetting();

    /**
     * 保存smtp设置
     *
     * @param smtpVo
     * @return
     */
    ResponseResult saveSmtpSetting(SmtpVo smtpVo);

    /**
     * 获取终端设置
     *
     * @return
     */
    ResponseResult getTerminalSetting();

    /**
     * 报错终端设置
     *
     * @param terminalVo
     * @return
     */
    ResponseResult saveTerminalSetting(TerminalVo terminalVo);

    /**
     * 获取系统设置
     *
     * @return
     */
    ResponseResult getSystemSetting();


    /**
     * 保存系统设置
     *
     * @param systemVo
     * @return
     */
    ResponseResult saveSystemSetting(SystemVo systemVo);
}
