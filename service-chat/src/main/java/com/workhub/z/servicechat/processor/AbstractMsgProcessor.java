package com.workhub.z.servicechat.processor;

import com.alibaba.fastjson.JSON;
import com.github.hollykunge.security.api.vo.user.UserInfo;
import com.workhub.z.servicechat.VO.MsgAnswerVO;
import com.workhub.z.servicechat.config.common;
import com.workhub.z.servicechat.entity.ZzDictionaryWords;
import com.workhub.z.servicechat.entity.ZzGroup;
import com.workhub.z.servicechat.entity.ZzMessageInfo;
import com.workhub.z.servicechat.entity.ZzMsgReadRelation;
import com.workhub.z.servicechat.feign.IUserService;
import com.workhub.z.servicechat.server.IworkServerConfig;
import com.workhub.z.servicechat.service.ZzDictionaryWordsService;
import com.workhub.z.servicechat.service.ZzGroupService;
import com.workhub.z.servicechat.service.ZzMessageInfoService;
import com.workhub.z.servicechat.service.ZzMsgReadRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.websocket.common.WsResponse;

import java.util.Date;
import java.util.List;

import static com.workhub.z.servicechat.config.MessageType.MSG_ANSWER;
import static com.workhub.z.servicechat.config.MessageType.SUCCESS_ANSWER;
import static com.workhub.z.servicechat.config.RandomId.getUUID;
import static com.workhub.z.servicechat.config.common.getJsonStringKeyValue;
import static com.workhub.z.servicechat.config.common.stringSearch;
@Service
public class AbstractMsgProcessor {

    @Autowired
    ZzDictionaryWordsService dictionaryWordsService;
    @Autowired
    ZzMsgReadRelationService msgReadRelationService;
    @Autowired
    ZzMessageInfoService messageInfoService;
    @Autowired
    IUserService iUserServiceUserService;
    @Autowired
    ZzGroupService zzGroupService;


    public WsResponse getWsResponse(String msg){
        return WsResponse.fromText(msg, IworkServerConfig.CHARSET);
    }
    // TODO: 2019/5/31 保密，关键词过滤

    public String messageFiltering(String msg){
        // TODO: 2019/5/31  获取数据词典，涉密词汇
        List<ZzDictionaryWords> zzDictionaryWordsList =null;
        return stringSearch(msg,zzDictionaryWordsList);
    }
    // TODO: 2019/5/31 敏感词过滤
    
    public boolean checkUserOnline(ChannelContext channelContext, String userId){
        ChannelContext checkChannelContext =
                Tio.getChannelContextById(channelContext.getGroupContext(),userId);
        //检查是否在线
        boolean isOnline = checkChannelContext != null && !checkChannelContext.isClosed;
        return isOnline;
    }

    /**
    *@Description: 存储未读消息
    *@Param:
    *@return:
    *@Author: 忠
    *@date: 2019/6/12
    */
    public void saveNoReadMsg(String sender, String receiver){
        ZzMsgReadRelation msgReadRelation = new ZzMsgReadRelation();
        msgReadRelation.setId(getUUID());
        msgReadRelation.setReceiver(receiver);
        msgReadRelation.setSender(sender);
        msgReadRelation.setSendType("1");
        msgReadRelationService.insert(msgReadRelation);
    }

    /**
    *@Description: 清除未读消息
    *@Param:
    *@return:
    *@Author: 忠
    *@date: 2019/6/12
    */
    public void deleteNoReadMsg(String sender, String receiver){
        msgReadRelationService.deleteByConsumerAndSender(sender,receiver);
    }

    /**
    *@Description: 存储消息
    *@Param: sender，receiver，createtime，content，levels,megid
    *@return:
    *@Author: 忠
    *@date: 2019/6/23
    */
    public void saveMessageInfo(String type,String sender, String receiver, String levels, Date createtime, String content, String msgId){
        ZzMessageInfo messageInfo = new ZzMessageInfo();
        messageInfo.setContent(content);
        messageInfo.setCreatetime(createtime);
        messageInfo.setLevels(levels);
        messageInfo.setReceiver(receiver);
        messageInfo.setSender(sender);
        messageInfo.setMsgId(msgId);
        messageInfo.setType(type);
        String iscross = "0";//是否跨越场所1是0否
        if("GROUP".equals(type)){//如是群消息
            ZzGroup group = zzGroupService.queryById(receiver);
            iscross = group.getIscross();
        }else{//如果是私聊消息
            List<UserInfo> userInfoList = iUserServiceUserService.userList(sender+","+receiver);
            boolean crossFlg = common.isGroupCross(userInfoList);
            iscross = (crossFlg)?"1":"0";
        }
        messageInfo.setIscross(iscross);
        messageInfoService.insert(messageInfo);
    }

    /**
    *@Description: 应答信息
    *@Param:消息内容
    *@return:
    *@Author: 忠
    *@date: 2019/7/30
    */
    public void msgAnswer(String msg,String nId,ChannelContext channelContext) throws Exception {
        MsgAnswerVO msgAnswerVO = new MsgAnswerVO();
        msgAnswerVO.setCode(MSG_ANSWER);
        msgAnswerVO.setContactId((String)getJsonStringKeyValue(msg,"data.toId"));
        msgAnswerVO.setnId(nId);
        msgAnswerVO.setoId((String)getJsonStringKeyValue(msg,"data.id"));
        msgAnswerVO.setStatus(SUCCESS_ANSWER);
//        System.out.println((String)getJsonStringKeyValue(msg,"data.fromId"));
        Tio.sendToUser(channelContext.getGroupContext(),(String)getJsonStringKeyValue(msg,"data.fromId"),this.getWsResponse(JSON.toJSONString(msgAnswerVO)));
    }

}
