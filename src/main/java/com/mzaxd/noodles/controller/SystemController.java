package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 13439
 */

@RestController
@RequestMapping("/system")
public class SystemController {

    @Resource
    private UserService userService;

    @RequestMapping("/isFirstUse")
    private ResponseResult isFirstUse() {
        return userService.isFirstUse();
    }

}
