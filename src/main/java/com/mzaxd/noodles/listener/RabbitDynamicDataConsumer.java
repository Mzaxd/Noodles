package com.mzaxd.noodles.listener;

import com.mzaxd.noodles.constant.RabbitMqConstant;
import com.mzaxd.noodles.constant.RedisConstant;
import com.mzaxd.noodles.domain.message.DynamicData;
import com.mzaxd.noodles.util.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author root
 */
@Slf4j
@Component
@RabbitListener(queuesToDeclare = @Queue(RabbitMqConstant.DYNAMIC_DATA_TOPIC))
public class RabbitDynamicDataConsumer {

    @Resource
    private RedisCache redisCache;

    @RabbitHandler
    public void process(@Payload DynamicData dynamicData) {
        log.info("开始消费动态数据消息");
        //将Redis中旧的数据删除
        redisCache.deleteObject(RedisConstant.DYNAMIC_DATA + dynamicData.getDetectorId());
        //将新的动态数据存入Redis
        redisCache.setCacheObject(RedisConstant.DYNAMIC_DATA + dynamicData.getDetectorId(), dynamicData);
        log.info("更新Redis中动态数据成功");
    }
}
