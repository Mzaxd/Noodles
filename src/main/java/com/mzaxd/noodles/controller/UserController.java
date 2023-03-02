package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.annotation.SysLog;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.User;
import com.mzaxd.noodles.domain.vo.UserInfoVo;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.UserService;
import com.mzaxd.noodles.util.IpUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author root
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/userInfo")
    public ResponseResult userInfo() {
        return userService.userInfo();
    }

    @PutMapping("/userInfo")
    public ResponseResult updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }

    @GetMapping("/profileHeader/{id}")
    public ResponseResult profileHeader(@PathVariable("id") Integer id, HttpServletRequest request) {
        String ipAddr = IpUtil.getIpAddr(request);
        return userService.getProfileHeader(id, ipAddr);
    }

    @GetMapping("/profile")
    public ResponseResult profile() {
        return userService.getProfile();
    }

    @GetMapping("/{id}")
    public ResponseResult getUserInfo(@PathVariable("id") Integer id) {
        return userService.getUserInfo(id);
    }

    @SysLog(operation = OperationEnum.USER_UPDATE_PASSWORD)
    @PostMapping("/changePassword")
    public ResponseResult changePassword(@RequestBody Map<String, String> map) {
        return userService.changePassword(map.get("password"));
    }

    @SysLog(operation = OperationEnum.USER_UPDATE_USERINFO)
    @PostMapping("/updateUserInfo")
    public ResponseResult updateUserInfo(@RequestBody Map<String, UserInfoVo> map) {
        return userService.updateUserInfo(map.get("user"));
    }


}