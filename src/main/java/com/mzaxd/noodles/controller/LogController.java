package com.mzaxd.noodles.controller;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 13439
 */
@RestController
@RequestMapping("/auditLog")
public class LogController {

    @Resource
    private AuditLogService auditLogService;

    @GetMapping("/logList")
    public ResponseResult getLogList(@RequestParam("perPage") Integer perPage,
                                     @RequestParam("currentPage") Integer currentPage) {
        return auditLogService.getLogList(perPage, currentPage);
    }

    @GetMapping("/getParam/{id}")
    public ResponseResult getParam(@PathVariable("id") Integer id) {
        return auditLogService.getParam(id);
    }

    @GetMapping("/userLog")
    public ResponseResult getUserLog() {
        return auditLogService.getUserLog();
    }
}
