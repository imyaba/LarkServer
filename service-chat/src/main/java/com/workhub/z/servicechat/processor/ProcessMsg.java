package com.workhub.z.servicechat.processor;

import com.alibaba.fastjson.JSONObject;
import com.workhub.z.servicechat.service.ZzGroupService;
import com.workhub.z.servicechat.service.ZzUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;

import static com.workhub.z.servicechat.config.MessageType.*;

@Service
public class ProcessMsg extends AbstractMsgProcessor{

    @Autowired
    private ProcessPrivateMsg processPrivateMsg;
    @Autowired
    private ProcessEditGroup processEditGroup;
    @Autowired
    private ProcessGroupMsg processGroupMsg;
    @Autowired
    private ZzUserGroupService userGroupService;
    @Autowired
    private ZzGroupService groupService;
    /**
     * 消息处理入口
     */

    public Object process(ChannelContext channelContext, String msg) {
        try{
            JSONObject jsonObject = JSONObject.parseObject(msg);
            String code = jsonObject.getString("code");
            String message = jsonObject.getString("data");

            //文件上传信息更新begin
            try {
                String msgType = common.nulToEmptyString(common.getJsonStringKeyValue(message,"content.type"));
                //如果是文件或者图片上传
                if("2".equals(msgType)||"3".equals(msgType)){
                    String fileId = common.nulToEmptyString(common.getJsonStringKeyValue(message,"content.id"));
                    String level = common.nulToEmptyString(common.getJsonStringKeyValue(message,"content.secretLevel"));
                    String receiverId = common.nulToEmptyString(common.getJsonStringKeyValue(message,"toId"));
                    String sendId = common.nulToEmptyString(common.getJsonStringKeyValue(message,"fromId"));
                    String sendName = common.nulToEmptyString(common.getJsonStringKeyValue(message,"username"));
                    String receiverName = common.nulToEmptyString(common.getJsonStringKeyValue(message,"contactInfo.name"));
                    zzGroupFileService.fileUpdate(fileId,receiverId,level,sendId,sendName,receiverName);
                }
            } catch (Exception e) {
                //异常记录到日志
                log.error(common.getExceptionMessage(e));
            }
            //文件上传信息更新end

//            processMsg(channelContext,msg,Integer.parseInt(code));

            switch (Integer.parseInt(code)){
                case SYS_MSG:
//                    Tio.sendToAll(channelContext.getGroupContext(),wsResponse);
                    break;
                case GROUP_MSG:
                   return processGroupMsg.sendMsg(channelContext,msg);
                case PRIVATE_MSG:
                    return  processPrivateMsg.sendMsg(channelContext,msg);
                    //
                case GROUP_EDIT:
                     processEditGroup.processManage(channelContext,message);
//                    GroupEditVO groupEditVO = JSON.parseObject(message, GroupEditVO.class);
//                     JSONArray datas = jsonObject.getJSONArray("data");
//                     List<GroupEditVO> groupEditVO = JSON.parseObject(datas.toJSONString(), new TypeReference<List<GroupEditVO>>() {
//                     });
//                     Tio.bindGroup(channelContext,groupEditVO.getGroupId());
                    break;
                case GROUP_CREATE:
                    return processEditGroup.createGroup(channelContext,msg);
                case MSG_EDIT_READ:
                    JSONObject temp = JSONObject.parseObject(message);
                    super.deleteNoReadMsg(temp.getString("sender"),temp.getString("reviser"));
                default:
                    System.out.println("你说的什么鬼");
                    break;
            }
        }catch (Exception e){
            System.out.println("别提了又错了"+ e.getMessage());
            return null;
        }
        return null;
    }
}
