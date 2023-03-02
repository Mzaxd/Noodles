package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.Notification;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 13439
* @description 针对表【notification】的数据库操作Service
* @createDate 2023-02-16 16:04:53
*/
public interface NotificationService extends IService<Notification> {

    /**
     * 发送容器掉线通知
     * @param id
     */
    void sendContainerOfflineNotification(Long id);

    /**
     * 发送虚拟机掉线通知
     * @param id
     */
    void sendVmOfflineNotification(Long id);

    /**
     * 发送物理机掉线通知
     * @param id
     */
    void sendHostOfflineNotification(Long id);


    /**
     * 提醒列表
     * @param tab
     * @param perPage
     * @param currentPage
     * @return
     */
    ResponseResult getNotificationList(Integer tab, Integer perPage, Integer currentPage);

    /**
     * 获取新通知数量
     * @return
     */
    ResponseResult getNotificationCount();

    /**
     * 确认提醒
     *
     * @param id
     * @return
     */
    ResponseResult affirmNotification(Long id);
}
