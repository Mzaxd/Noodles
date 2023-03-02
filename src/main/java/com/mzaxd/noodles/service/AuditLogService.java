package com.mzaxd.noodles.service;

import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.AuditLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 13439
* @description 针对表【audit_log】的数据库操作Service
* @createDate 2023-02-13 12:19:26
*/
public interface AuditLogService extends IService<AuditLog> {

    /**
     * 返回日志列表
     *
     * @param perPage
     * @param currentPage
     * @return
     */
    ResponseResult getLogList(Integer perPage, Integer currentPage);

    /**
     * 获取参数
     *
     * @param id
     * @return
     */
    ResponseResult getParam(Integer id);

    /**
     * 获取用户日志
     *
     * @return
     */
    ResponseResult getUserLog();
}
