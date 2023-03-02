package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mzaxd.noodles.domain.vo.UserInfoVo;

/**
* @author root
* @description 针对表【user】的数据库操作Service
* @createDate 2023-01-28 09:16:07
*/
public interface UserService extends IService<User> {

    /**
     * 查询用户信息
     *
     * @author mzaxd
     * @date 1/29/23 4:16 AM
     * @return ResponseResult
     */
    ResponseResult userInfo();

    /**
     * 修改用户信息
     *
     * @author mzaxd
     * @date 1/29/23 4:16 AM
     * @param user
     * @return ResponseResult
     */
    ResponseResult updateUserInfo(User user);

    /**
     * 返回个人信息页面头部数据
     *
     * @param id
     * @return
     */
    ResponseResult getProfileHeader(Integer id, String ip);

    /**
     * 返回profile页面所需数据
     *
     * @return
     */
    ResponseResult getProfile();

    /**
     * 通过判断默认账户的state状态来判断是否第一次使用
     * @return
     */
    ResponseResult isFirstUse();

    /**
     * 修改密码
     * @param password
     * @return
     */
    ResponseResult changePassword(String password);

    /**
     * 通过id获取UserInfo
     *
     * @param id
     * @return
     */
    ResponseResult getUserInfo(Integer id);

    /**
     * 修改账号信息
     * @param userData
     * @return
     */
    ResponseResult updateUserInfo(UserInfoVo userData);
}
