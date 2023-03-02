package com.mzaxd.noodles.annotation;

import com.mzaxd.noodles.enums.OperationEnum;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * @author 13439
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    OperationEnum operation();
}
