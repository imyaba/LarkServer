package com.github.hollykunge.security.entity;

import java.io.Serializable;
import java.util.Date;

public class FileManageInf  implements Serializable {

    private static final long serialVersionUID = 6076301102453313990L;
    //附件id
    private String fileId;
    //附件名称
    private String fileName;
    //文件后缀名
    private String fileExt;
    //附件类型（text、img、doc）
    private String fileType;
    //文件大小
    private Double sizes;
    //附件路径
    private String path;
    //附件转码为可读取的路径
    private String readPath;
    //创建时间
    private Date createTime;
    //创建人
    private String creator;
    //更新时间
    private Date updateTime;
    //更新人
    private String updator;
    //保密等级
    private String levels;
    //创建人姓名
    private String creatorName;

    //是否已经发送
    private String sendFlg;
    //加密标记
    private String fileEncrypeFlg;

    //类型0研讨服务1工具
    private String workType;

    public String getSendFlg() {
        return sendFlg;
    }

    public void setSendFlg(String sendFlg) {
        this.sendFlg = sendFlg;
    }

    public String getFileEncrypeFlg() {
        return fileEncrypeFlg;
    }

    public void setFileEncrypeFlg(String fileEncrypeFlg) {
        this.fileEncrypeFlg = fileEncrypeFlg;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Double getSizes() {
        return sizes;
    }

    public void setSizes(Double sizes) {
        this.sizes = sizes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getReadPath() {
        return readPath;
    }

    public void setReadPath(String readPath) {
        this.readPath = readPath;
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

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
    }
    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }
}
