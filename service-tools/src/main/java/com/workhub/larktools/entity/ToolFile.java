package com.workhub.larktools.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * author:zhuqz
 * description:工具表映射
 * date:2019/8/15 16:43
 **/
public class ToolFile implements Serializable {

    private static final long serialVersionUID = -1006958525490353285L;
    private String id;//主键
    private String fileId;//文件id
    private String fileName;//文件名称
    private String fileExt;//后缀
    private  Date createTime ;//创建时间
    private String creator;//创建人id
    private String creatorName;//创建人姓名
    private Date updateTime  ;//更新时间
    private String updator ;//更新人id
    private String updatorName ;//更新人姓名
    private String treeId      ;//所属树节点
    private String isDelete    ;//是否已经删除
    private String describe;//描述
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

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
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

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
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

    public String getUpdatorName() {
        return updatorName;
    }

    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
