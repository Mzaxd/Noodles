package com.mzaxd.noodles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.HostMachine;
import com.mzaxd.noodles.domain.entity.User;
import com.mzaxd.noodles.domain.vo.ProfileHeaderVo;
import com.mzaxd.noodles.domain.vo.ProfileVo;
import com.mzaxd.noodles.domain.vo.UserInfoVo;
import com.mzaxd.noodles.enums.AppHttpCodeEnum;
import com.mzaxd.noodles.mapper.UserMapper;
import com.mzaxd.noodles.service.ContainerService;
import com.mzaxd.noodles.service.HostMachineService;
import com.mzaxd.noodles.service.ServirService;
import com.mzaxd.noodles.service.UserService;
import com.mzaxd.noodles.util.BeanCopyUtils;
import com.mzaxd.noodles.util.IpUtil;
import com.mzaxd.noodles.util.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

/**
* @author root
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-01-28 09:16:07
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private HostMachineService hostMachineService;

    @Resource
    private ContainerService containerService;

    @Resource
    private ServirService servirService;

    @Override
    public ResponseResult<?> userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //根据id查询用户信息
        User user = getById(userId);
        //将用户信息封装Vo并返回
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    public ResponseResult<?> updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getProfileHeader(Integer id, String ip) {
        User user = getById(id);
        ProfileHeaderVo profileHeaderVo = BeanCopyUtils.copyBean(user, ProfileHeaderVo.class);
        try {
            profileHeaderVo.setLocation(IpUtil.getRegionByIp(ip));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseResult.okResult(profileHeaderVo);
    }

    @Override
    public ResponseResult getProfile() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        User user = getById(userId);
        ProfileVo profile = BeanCopyUtils.copyBean(user, ProfileVo.class);
        //查物理机数量
        LambdaQueryWrapper<HostMachine> hostMachineWrapper = new LambdaQueryWrapper<>();
        hostMachineWrapper.eq(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        profile.setHostNumber(hostMachineService.count(hostMachineWrapper));
        //查虚拟机数量
        LambdaQueryWrapper<HostMachine> vmWrapper = new LambdaQueryWrapper<>();
        vmWrapper.ne(HostMachine::getHostMachineId, SystemConstant.HOST_MACHINE_ID_HOST);
        profile.setVmNumber(hostMachineService.count(vmWrapper));
        //查容器数量
        profile.setContainerNumber(containerService.count());
        //查服务数量
        profile.setServirNumber(servirService.count());
        return ResponseResult.okResult(profile);
    }

    @Override
    public ResponseResult isFirstUse() {
        User user = getById(1);
        if (user.getUserState() == 0) {
            return ResponseResult.okResult(true);
        } else {
            return ResponseResult.okResult(false);
        }
    }

    @Override
    public ResponseResult changePassword(String password) {
        Long userId = SecurityUtils.getUserId();
        User user = getById(userId);
        user.setPassword(passwordEncoder.encode(password));
        saveOrUpdate(user);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS.getCode(), "密码修改成功");
    }

    @Override
    public ResponseResult getUserInfo(Integer id) {
        User user = getById(id);
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    public ResponseResult updateUserInfo(UserInfoVo userData) {
        Long userId = SecurityUtils.getUserId();
        User user = getById(userId);
        user.setEmail(userData.getEmail()).setUserName(userData.getUserName()).setNickName(userData.getNickName()).setAvatar(userData.getAvatar());
        saveOrUpdate(user);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS.getCode(), "账号信息修改成功");
    }
}




