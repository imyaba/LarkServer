package com.workhub.larktools.vo;

import java.io.Serializable;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 14:07
 **/
public class ToolScoreVo implements Serializable {
    private static final long serialVersionUID = 5656472306753379866L;
    private String id;//主键
    private String fileId;//附件id
    private String fileName;//附件名称
    private String score;//得分
    private String creatorName;//评论人
    private String createTime;//评论时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
