package com.workhub.z.servicechat.VO;
/**
*@Description: 前端接口，群组信息
*@Author: 忠
*@date: 2019/5/14
*/
public class GroupInfoVO
{
//  群id
    private String id;
//  群名
    private String name;
//  群头像
    private String avatar;
//  描述
    private String description;
//  主题
    private String subject;
//  密级
    private String securityClass;
//  创建时间
    private String createTime;
//  创建人
    private String creator;
    //  创建人
    private String creatorName;
//  群组成员数量
    private Integer memberNum;
    //群主id
    private String groupOwnerId;
    //群主姓名
    private String groupOwnerName;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setAvatar(String avatar){
        this.avatar = avatar;
    }
    public String getAvatar(){
        return this.avatar;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }
    public void setSubject(String subject){
        this.subject = subject;
    }
    public String getSubject(){
        return this.subject;
    }
    public void setSecurityClass(String securityClass){
        this.securityClass = securityClass;
    }
    public String getSecurityClass(){
        return this.securityClass;
    }
    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    public String getCreateTime(){
        return this.createTime;
    }
    public void setCreator(String creator){
        this.creator = creator;
    }
    public String getCreator(){
        return this.creator;
    }

    public Integer getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(Integer memberNum) {
        this.memberNum = memberNum;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

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
}
