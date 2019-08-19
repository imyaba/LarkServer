package com.github.hollykunge.security.admin.config.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.hollykunge.security.admin.entity.User;
import com.github.hollykunge.security.admin.mapper.UserMapper;
import com.github.hollykunge.security.admin.util.MqSetBaseEntity;
import com.github.hollykunge.security.common.constant.CommonConstants;
import com.github.hollykunge.security.common.vo.mq.AdminUserVO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.*;

/**
 * 人员数据消费者
 *
 * @author zhhongyu
 */
@Component
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserConsumer {
    @Resource
    private UserMapper userMapper;

    @Autowired
    private ProduceSenderConfig produceSenderConfig;

    @RabbitHandler
    @RabbitListener(queues = CommonConstants.ADMINUSER_QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void handleMessage(Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {

        String msg = new String(message.getBody(), "gb2312");
        List<AdminUserVO> adminUserVOS = JSONArray.parseArray(msg, AdminUserVO.class);
        List<AdminUserVO> setMqUserVO = new ArrayList<AdminUserVO>();
        for (AdminUserVO adminUserVO :
                adminUserVOS) {
            if (StringUtils.isEmpty(adminUserVO.getPId())) {
                log.error("mq同步人员存在人员没有身份证号的信息集...该条mq消息属于错误数据，" +
                        "已经从mq队列中剔除,其中包含如下数据:"+adminUserVO.toString());
                setMqUserVO.add(adminUserVO);
                continue;
            }
            User user = new User();
            user.setPId(adminUserVO.getPId());
            User existUser = userMapper.selectOne(user);
            //数据库中没有改人员信息，属于新增
            if (existUser == null) {
                existUser = new User();
//                BeanUtils.copyProperties(adminUserVO, existUser);
                existUser = JSONObject.parseObject(JSONObject.toJSONString(adminUserVO), User.class);
                MqSetBaseEntity.setCreatData(existUser);
                userMapper.insertSelective(existUser);
                continue;
            }
            //数据库中含有人员信息，属于修改
            String priKey = existUser.getId();
            BeanUtils.copyProperties(adminUserVO, existUser);
            existUser.setId(priKey);
            MqSetBaseEntity.setUpdate(existUser);
            userMapper.updateByPrimaryKeySelective(existUser);
        }
        //如果setMqUserVO不是空数组，重新发送一个队列，这些数据为未被消费的错误数据信息集
        if (setMqUserVO.size() > 0) {
            //如果setMqUserVO不为空，通知提供者，该批量数据含有身份证号为空的数据。
            String jsonStr = JSONObject.toJSONString(setMqUserVO);
            Message noticeMessage = MessageBuilder.withBody(jsonStr.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
//                    .setContentEncoding("utf-8")
                    .setMessageId(UUID.randomUUID() + "").build();
            produceSenderConfig.adminUserOrOrgSend(noticeMessage, CommonConstants.ADMIN_UNACK_USER_KEY);
        }
        //手动ack
        long deliveryTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
        //手动签收消息
        channel.basicAck(deliveryTag, false);
    }
}
