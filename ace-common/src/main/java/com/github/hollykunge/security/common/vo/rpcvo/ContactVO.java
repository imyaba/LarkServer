package com.github.hollykunge.security.common.vo.rpcvo;

/**
*@Description: 最近联系人前端交互实体类
*@Author: 忠
*@date: 2019/5/10
*/
public class ContactVO {
    private String id;//用户id
    private String name;//名称
    private String time;//最近联系时间
    private String fullTime;//最近联系时间-全，用来给mq消费端
    private MessageContent lastMessage;//最后一条消息
    private String sender;//消息发送人
    private String avatar;//头像
    private int unreadNum;//未读消息条数
    private boolean atMe;//是否@我
    private boolean isTop;//是否置顶
    private boolean isMute;//
    private boolean isGroup;//群组消息
    private int memberNum;//群组成员数/在线人数
    private int secretLevel;//群组密级
    //群主id
    private String groupOwnerId;
    //群主姓名
    private String groupOwnerName;

    public String getGroupOwnerId() {
        return groupOwnerId;
    }

    public void setGroupOwnerId(String groupOwnerId) {
        this.groupOwnerId = groupOwnerId;
    }

    public String getGroupOwnerName() {
        return groupOwnerName;
    }

    public void setGroupOwnerName(String groupOwnerName) {
        this.groupOwnerName = groupOwnerName;
    }

    public boolean isAtMe() {
        return atMe;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public int getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(int memberNum) {
        this.memberNum = memberNum;
    }

    public int getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(int secretLevel) {
        this.secretLevel = secretLevel;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }

    public void setLastMessage(MessageContent lastMessage) {
        this.lastMessage = lastMessage;
    }
    public MessageContent getLastMessage() {
        return lastMessage;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setUnreadNum(int unreadNum) {
        this.unreadNum = unreadNum;
    }
    public int getUnreadNum() {
        return unreadNum;
    }

    public void setAtMe(boolean atMe) {
        this.atMe = atMe;
    }
    public boolean getAtMe() {
        return atMe;
    }

    public void setIsTop(boolean isTop) {
        this.isTop = isTop;
    }
    public boolean getIsTop() {
        return isTop;
    }

    public void setIsMute(boolean isMute) {
        this.isMute = isMute;
    }
    public boolean getIsMute() {
        return isMute;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }
    public boolean getIsGroup() {
        return isGroup;
    }

    public String getFullTime() {
        return fullTime;
    }

    public void setFullTime(String fullTime) {
        this.fullTime = fullTime;
    }
}