package com.mzaxd.noodles.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.LoginUser;
import com.mzaxd.noodles.domain.entity.User;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.mapper.UserMapper;
import com.mzaxd.noodles.service.LoginService;
import com.mzaxd.noodles.service.UserService;
import com.mzaxd.noodles.util.JwtUtil;
import com.mzaxd.noodles.util.RedisCache;
import com.mzaxd.noodles.util.SecurityUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author root
 */
@Service
public class LoginServiceImpl extends ServiceImpl<UserMapper, User> implements LoginService {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisCache redisCache;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserMapper userMapper;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("邮箱或密码错误");
        }
        //获取userid 生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject("login:" + userId, loginUser);
        //把token封装 返回
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        map.put("userId", userId);
        return ResponseResult.okResult("登录成功", map);
    }

    @Override
    public ResponseResult logout() {
        //获取当前登录的用户id
        Long userId = SecurityUtils.getUserId();
        //删除redis中对应的值
        redisCache.deleteObject("login:" + userId);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateAccount(User user) {
        //获取当前登录的用户id
        Long userId = SecurityUtils.getUserId();
        User defaultUser = getById(userId);
        defaultUser.setEmail(user.getEmail());
        defaultUser.setPassword(passwordEncoder.encode(user.getPassword()));
        defaultUser.setUserState(SystemConstant.NORMAL_STATE);
        saveOrUpdate(defaultUser);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS.getCode(),"修改账号信息成功");
    }

    @Override
    public ResponseResult deleteAccount() {
        Long userId = SecurityUtils.getUserId();
        userMapper.deleteById(userId);
        return ResponseResult.okResult();
    }
}
