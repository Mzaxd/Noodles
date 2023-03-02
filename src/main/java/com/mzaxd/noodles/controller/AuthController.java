package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.annotation.SysLog;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.User;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.LoginService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author root
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user) {
        return loginService.login(user);
    }

    @SysLog(operation = OperationEnum.USER_FIRST_USE)
    @PostMapping("/updateAccount")
    public ResponseResult updateAccount(@RequestBody User user) {
        return loginService.updateAccount(user);
    }

    @PostMapping("/logout")
    public ResponseResult logout() {
        return loginService.logout();
    }

    @GetMapping("/deleteAccount")
    public ResponseResult deleteAccount() {
        return loginService.deleteAccount();
    }

}
