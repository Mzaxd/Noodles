package com.mzaxd.noodles.constant;

/**
 * @author root
 */
public class RabbitMqConstant {

    /**
     * RabbitMQ的队列主题名称
     */
    public static final String DYNAMIC_DATA_TOPIC = "DynamicDataTopic";

    /**
     * RabbitMQ的direct交换机名称
     */
    public static final String DYNAMIC_DATA_EXCHANGE = "DynamicDataExchange";

    /**
     * RabbitMQ的direct交换机和队列绑定的匹配键 DirectRouting
     */
    public static final String DYNAMIC_DATA_ROUTING = "DynamicDataRouting";
}
