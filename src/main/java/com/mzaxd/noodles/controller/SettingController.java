package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.vo.SmtpVo;
import com.mzaxd.noodles.domain.vo.SystemVo;
import com.mzaxd.noodles.domain.vo.TerminalVo;
import com.mzaxd.noodles.service.SystemSettingService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 13439
 */
@RestController
@RequestMapping("/setting")
public class SettingController {

    @Resource
    private SystemSettingService systemSettingService;

    @GetMapping("/smtp")
    public ResponseResult getSmtpSetting() {
        return systemSettingService.getSmtpSetting();
    }

    @PostMapping("/smtp")
    public ResponseResult saveSmtpSetting(@RequestBody SmtpVo smtpVo) {
        return systemSettingService.saveSmtpSetting(smtpVo);
    }
    @GetMapping("/terminal")
    public ResponseResult getTerminalSetting() {
        return systemSettingService.getTerminalSetting();
    }

    @PostMapping("/terminal")
    public ResponseResult saveTerminalSetting(@RequestBody TerminalVo terminalVo) {
        return systemSettingService.saveTerminalSetting(terminalVo);
    }

    @GetMapping("/system")
    public ResponseResult getSystemSetting() {
        return systemSettingService.getSystemSetting();
    }

    @PostMapping("/system")
    public ResponseResult saveSystemSetting(@RequestBody SystemVo systemVo) {
        return systemSettingService.saveSystemSetting(systemVo);
    }
}
