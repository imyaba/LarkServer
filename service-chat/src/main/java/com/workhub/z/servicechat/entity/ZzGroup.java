package com.workhub.z.servicechat.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 群组表(ZzGroup)实体类
 *
 * @author 忠
 * @since 2019-05-10 14:29:32
 */
public class ZzGroup implements Serializable {
    private static final long serialVersionUID = -36565429819302307L;
    //用户组id
    private String groupId;
    //组名称
    private String groupName;
    //组描述
    private String groupDescribe;
    //是否已经解散
    private String isdelete;
    //创建时间
    private Date createTime;
    //创建人
    private String creator;
    //更新时间
    private Date updateTime;
    //更新人
    private String updator;
    //项目名称
    private String pname;
    //参与范围
    private String scop;
    //是否公共
    private String ispublic;
    //讨论组等级
    private String levels;
    //是否关闭
    private String isclose;
    //群组头像
    private String groupImg;
    //是否跨越场所
    private String iscross;
    //创建人姓名
    private String creatorName;
    //群主id
    private String groupOwnerId;
    //群主姓名
    private String groupOwnerName;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescribe() {
        return groupDescribe;
    }

    public void setGroupDescribe(String groupDescribe) {
        this.groupDescribe = groupDescribe;
    }

    public String getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(String isdelete) {
        this.isdelete = isdelete;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getScop() {
        return scop;
    }

    public void setScop(String scop) {
        this.scop = scop;
    }

    public String getIspublic() {
        return ispublic;
    }

    public void setIspublic(String ispublic) {
        this.ispublic = ispublic;
    }

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
    }

    public String getIsclose() {
        return isclose;
    }

    public void setIsclose(String isclose) {
        this.isclose = isclose;
    }

    public String getGroupImg() {
        return groupImg;
    }

    public void setGroupImg(String groupImg) {
        this.groupImg = groupImg;
    }

    public String getIscross() {
        return iscross;
    }

    public void setIscross(String iscross) {
        this.iscross = iscross;
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

    @Override
    public String toString() {
        return "ZzGroup{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupDescribe='" + groupDescribe + '\'' +
                ", isdelete='" + isdelete + '\'' +
                ", createTime=" + createTime +
                ", creator='" + creator + '\'' +
                ", updateTime=" + updateTime +
                ", updator='" + updator + '\'' +
                ", pname='" + pname + '\'' +
                ", scop='" + scop + '\'' +
                ", ispublic='" + ispublic + '\'' +
                ", levels='" + levels + '\'' +
                ", isclose='" + isclose + '\'' +
                ", groupImg='" + groupImg + '\'' +
                ", iscross='" + iscross + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", groupOwnerId='" + groupOwnerId + '\'' +
                ", groupOwnerName='" + groupOwnerName + '\'' +
                '}';
    }

}