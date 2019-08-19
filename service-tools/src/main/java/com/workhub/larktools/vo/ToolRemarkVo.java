package com.workhub.larktools.vo;

import java.io.Serializable;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 10:16
 **/
public class ToolRemarkVo implements Serializable {
    private static final long serialVersionUID = -7668631905572605732L;
    private String id;//评论id
    private String fileId;//工具下载id
    private String toolName;//工具名称
    private String creatorName;
    private String createTime;
    private String remark;

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

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
