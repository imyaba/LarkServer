package com.github.hollykunge.servicewebservice.config.mq;

import com.alibaba.fastjson.JSONArray;
import com.github.hollykunge.security.common.constant.CommonConstants;
import com.github.hollykunge.security.common.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * @Author: yzq
 * @Date: 创建于 2019/7/23 17:34
 */
@Component
@Slf4j
public class ProduceSenderConfig {
    @Resource(name = "orgRabbitTemplate")
    private RabbitTemplate orgRabbitTemplate;
    @Resource(name = "orgRabbitTemplate")
    private RabbitTemplate userRabbitTemplate;

    public void sendOrgList(String ids,List orgList){
        CorrelationData correlationData = new CorrelationData(ids);
        String json = JSONArray.toJSONString(orgList);
        Message message = MessageBuilder.withBody(json.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding("gb2312")
                .build();
        log.info(new String(message.getBody()));
        orgRabbitTemplate.convertAndSend(CommonConstants.WERSERVICE_ADMIN_USERANDORG_EXCHANGE,CommonConstants.ADMINORG_ROTEING_KEY,message,correlationData);
        log.info("org消息发送至admin:" + orgList.toString());
    }

    public void sendUserList(String ids,List userList){
        CorrelationData correlationData = new CorrelationData(ids);
        String json = JSONArray.toJSONString(userList);
        Message message = MessageBuilder.withBody(json.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding("gb2312")
                .build();
        log.info(new String(message.getBody()));
        userRabbitTemplate.convertAndSend(CommonConstants.WERSERVICE_ADMIN_USERANDORG_EXCHANGE,CommonConstants.ADMINUSER_ROTEING_KEY,message,correlationData);
        log.info("user消息发送至admin:" + userList.toString());
    }
}
