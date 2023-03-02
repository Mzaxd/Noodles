package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.annotation.SysLog;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.NotificationService;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 13439
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @GetMapping("/notificationList")
    public ResponseResult getNotificationList(
            @RequestParam(value = "tab", required = false) Integer tab,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("currentPage") Integer currentPage) {
        return notificationService.getNotificationList(tab, perPage, currentPage);
    }

    @SysLog(operation = OperationEnum.NOTIFICATION_AFFIRM)
    @PutMapping("/{id}")
    public ResponseResult affirmNotification(@PathVariable("id") Long id) {
        return notificationService.affirmNotification(id);
    }

    @GetMapping("/count")
    public ResponseResult getNotificationCount() {
        return notificationService.getNotificationCount();
    }

}
