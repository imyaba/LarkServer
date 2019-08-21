package com.workhub.z.servicechat.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.hollykunge.security.common.vo.rpcvo.MessageContent;
import com.workhub.z.servicechat.VO.GroupInfoVO;
import com.workhub.z.servicechat.entity.ZzGroup;
import com.workhub.z.servicechat.entity.ZzGroupMsg;
import com.workhub.z.servicechat.entity.ZzPrivateMsg;
import com.workhub.z.servicechat.model.CreateGroupDto;
import com.workhub.z.servicechat.model.GroupTaskDto;
import com.workhub.z.servicechat.model.UserListDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.workhub.z.servicechat.config.RandomId.getUUID;

/**
*@Description: entity和vo转换
*@Author: 忠
*@date: 2019/5/29
*/
public abstract class VoToEntity {
    // TODO: 2019/5/31 增加转换异常处理
    /**
    *@Description:  Msg转换
    *@Author: 忠
    *@date: 2019/5/29
    */
    public static Object MsgVOToModel(String text) {
        JSONObject json = JSON.parseObject(text);
        ZzPrivateMsg zpm = new ZzPrivateMsg();
        zpm.setMsgId(getUUID());//信息ID
        zpm.setMsgSender(json.getString("fromId"));
        zpm.setMsgReceiver(json.getString("toId"));
        zpm.setLevels(json.getObject("content",MessageContent.class).getSecretLevel().toString());
        zpm.setIsRead("0");
        zpm.setMsgPath("");
        zpm.setIsDelete("0");
        zpm.setMsg(json.getString("content"));
        zpm.setMsgType(json.getObject("content",MessageContent.class).getType().toString());
        zpm.setSendTime(json.getDate("time"));
        zpm.setReceiverTime(json.getDate("time"));
        return zpm;
    }

    /**
     *@Description:  群Msg转换
     *@Author: 忠
     *@date: 2019/5/29
     */
    public static Object GroupMsgVOToModel(String text) {
        JSONObject json = JSON.parseObject(text);
        ZzGroupMsg zpm = new ZzGroupMsg();
        zpm.setMsgId(getUUID());//信息ID
        zpm.setMsgSender(json.getString("fromId"));
        zpm.setMsgReceiver(json.getString("toId"));
        zpm.setLevels(json.getObject("content",MessageContent.class).getSecretLevel().toString());
        zpm.setMsgPath("");
        zpm.setIsDelete("0");
        zpm.setMsg(json.getString("content"));
        zpm.setMsgType(json.getObject("content",MessageContent.class).getType().toString());
        zpm.setSendTime(json.getDate("time"));
        zpm.setIsImportant("0");
        return zpm;
    }

    /**
    *@Description: 群操作转换实体GroupTaskDto
    *@Param: 群操作msg
    *@return: GroupTaskDto
    *@Author: 忠
    *@date: 2019/6/4
    */
    public static GroupTaskDto toGroupTaskDto (String msg) {
        GroupTaskDto groupTaskDto = new GroupTaskDto();
        JSONObject groupJson = JSONObject.parseObject(msg);

        groupTaskDto.setGroupId(getUUID());
//      groupTaskDto.setGroupId(groupJson.getString("groupId"));
        groupTaskDto.setReviser(groupJson.getString("reviser"));
        groupTaskDto.setTimestamp(groupJson.getTimestamp("timestamp"));
        groupTaskDto.setType(Integer.parseInt(groupJson.getString("type")));
        List<UserListDto> uList = new ArrayList();
        JSONArray userJsonArray = JSONObject.parseArray(groupJson.getString("userList"));
        for (int i = 0; i < userJsonArray.size(); i++) {
            JSONObject userJson = JSONObject.parseObject(userJsonArray.getString(i));
            UserListDto user = new UserListDto();
            user.setUserId(userJson.getString("userId"));
            user.setUserLevels(userJson.getString("userLevels"));
            user.setImg(userJson.getString("img"));
            uList.add(user);
        }
        groupTaskDto.setUserList(uList);
        return groupTaskDto;
    }

    /**
    *@Description: 创建群组实体转换
    *@Param: String
    *@return: CreateGroupDto
    *@Author: 忠
    *@date: 2019/6/10
    */
    public static Object CreateGroupVOToModel(String text) {
        CreateGroupDto createGroupDto =new CreateGroupDto();

        return createGroupDto;
    }

    /**
    *@Description: 群组详细信息
    *@Param:
    *@return:
    *@Author: 忠
    *@date: 2019/6/14
    */
    public static Object ZzGroupToGroupInfo(ZzGroup zzGroup){
        GroupInfoVO groupInfo = new GroupInfoVO();
        groupInfo.setAvatar(zzGroup.getGroupImg());
        groupInfo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(zzGroup.getCreateTime()));
        groupInfo.setCreator(zzGroup.getCreator());
        groupInfo.setDescription(zzGroup.getGroupDescribe());
        groupInfo.setId(zzGroup.getGroupId());
        groupInfo.setName(zzGroup.getGroupName());
        groupInfo.setSecurityClass(zzGroup.getLevels());
        groupInfo.setSubject(zzGroup.getPname());
        groupInfo.setCreatorName(common.nulToEmptyString(zzGroup.getCreatorName()));
        groupInfo.setGroupOwnerId(common.nulToEmptyString(zzGroup.getGroupOwnerId()));
        groupInfo.setGroupOwnerName(common.nulToEmptyString(zzGroup.getGroupOwnerName()));
        return groupInfo;
    }
}
