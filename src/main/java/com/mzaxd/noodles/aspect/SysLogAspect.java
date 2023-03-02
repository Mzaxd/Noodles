package com.mzaxd.noodles.aspect;

import com.alibaba.fastjson.JSON;
import com.mzaxd.noodles.annotation.SysLog;
import com.mzaxd.noodles.domain.entity.AuditLog;
import com.mzaxd.noodles.enums.OperationEnum;
import com.mzaxd.noodles.service.AuditLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author 13439
 */
@Component
@Aspect
public class SysLogAspect {

    @Resource
    private AuditLogService auditLogService;

    @Pointcut("@annotation(com.mzaxd.noodles.annotation.SysLog)")
    public void logPointCut() {

    }

    //方法执行前后都操作
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //执行方法
        Object result = point.proceed();
        //保存日志
        saveSysLog(point);
        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        AuditLog log = new AuditLog();
        SysLog syslog = method.getAnnotation(SysLog.class);
        if (syslog != null) {
            //注解上的描述
            log.setOperation(syslog.operation().getOperation());
            log.setOperationType(syslog.operation().getOperationType());
        }

        //请求的参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = JSON.toJSONString(args);
            log.setParam(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //保存系统日志
        auditLogService.save(log);
    }
}
