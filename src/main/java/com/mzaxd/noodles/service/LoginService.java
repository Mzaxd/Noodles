package com.mzaxd.noodles.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.User;

/**
 * @author root
 */
public interface LoginService extends IService<User> {

    /**
     * 后台登录接口
     *
     * @author mzaxd
     * @date 12/4/22 1:29 PM
     * @param user
     * @return ResponseResult
     */
    ResponseResult login(User user);

    /**
     * 后台用户退出登陆
     *
     * @author mzaxd
     * @date 12/6/22 2:07 PM
     * @return ResponseResult
     */
    ResponseResult logout();

    /**
     * 首次登陆时 修改账户信息
     *
     * @param user
     * @return
     */
    ResponseResult updateAccount(User user);

    /**
     * 删除账号
     *
     * @return
     */
    ResponseResult deleteAccount();
}
