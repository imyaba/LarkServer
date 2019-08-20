package com.workhub.z.servicechat.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 消息存储(ZzMessageInfo)实体类
 *
 * @author makejava
 * @since 2019-06-23 14:40:58
 */
public class ZzMessageInfo implements Serializable {
    private static final long serialVersionUID = -38086025213877508L;
    
    private String msgId;
    //当前用户
    private String sender;
    //联系人
    private String receiver;
    //产生时间
    private Date createtime;
    //消息密级
    private String levels;
    //消息内容
    private String content;
    //群组（greoup）,私聊（user）
    private String type;
    //是否跨越场所1是0否
    private String iscross;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIscross() {
        return iscross;
    }

    public void setIscross(String iscross) {
        this.iscross = iscross;
    }
}