package com.workhub.larktools.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 14:04
 **/
public class ToolScore implements Serializable {
    private static final long serialVersionUID = -1890131987102907831L;
    private String id;
    private String toolId;
    private String creator;
    private String creatorName;
    private Date createTime;
    private String updator;
    private String updatorName;
    private String update_time;
    private String isDelete;
    private Double score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToolId() {
        return toolId;
    }

    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getUpdatorName() {
        return updatorName;
    }

    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
