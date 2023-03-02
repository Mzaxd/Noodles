package com.mzaxd.noodles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.domain.ResponseResult;
import com.mzaxd.noodles.domain.entity.*;
import com.mzaxd.noodles.domain.vo.*;
import com.mzaxd.noodles.mapper.UserMapper;
import com.mzaxd.noodles.service.AuditLogService;
import com.mzaxd.noodles.mapper.AuditLogMapper;
import com.mzaxd.noodles.util.BeanCopyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 13439
* @description 针对表【audit_log】的数据库操作Service实现
* @createDate 2023-02-13 12:19:26
*/
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog>
    implements AuditLogService{

    @Resource
    private UserMapper userMapper;

    @Override
    public ResponseResult getLogList(Integer perPage, Integer currentPage) {
        //分页
        PageHelper.startPage(currentPage, perPage);
        LambdaQueryWrapper<AuditLog> auditLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        auditLogLambdaQueryWrapper.orderByDesc(AuditLog::getCreateTime);
        //查询
        List<AuditLog> auditLogs = list(auditLogLambdaQueryWrapper);
        //封装分页信息
        PageInfo pageInfo = new PageInfo<>(auditLogs, perPage);

        //封装结果返回
        List<AuditLogListVo> auditLogListVos = BeanCopyUtils.copyBeanList(auditLogs, AuditLogListVo.class);
        auditLogListVos.forEach(auditLogListVo -> {
            User user = userMapper.selectById(auditLogListVo.getCreateBy());
            UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
            auditLogListVo.setUser(userInfoVo);
        });
        pageInfo.setList(auditLogListVos);
        return ResponseResult.okResult(pageInfo);
    }

    @Override
    public ResponseResult getParam(Integer id) {
        AuditLog auditLog = getById(id);
        return ResponseResult.okResult(auditLog.getParam());
    }

    @Override
    public ResponseResult getUserLog() {
        LambdaQueryWrapper<AuditLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuditLog::getOperationType, SystemConstant.LOG_USER);
        queryWrapper.orderByDesc(AuditLog::getCreateTime);
        List<AuditLog> list = list(queryWrapper);
        return ResponseResult.okResult(list);
    }
}




