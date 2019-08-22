package com.github.hollykunge.security.common.vo.mq;

import lombok.Data;

@Data
public class MsgQueue {
    //消息类型
    private String  MsgType;
    //消息来源
    private String  MsgSource;
    //消息内容
    private String  MsgContent;
    //消息回调
    private String  MsgCallback;
    //消息时间
    private String  MsgTime;
    //消息发送人
    private String  MsgSen;
    //消息接收方
    private String  MsgRec;
}
