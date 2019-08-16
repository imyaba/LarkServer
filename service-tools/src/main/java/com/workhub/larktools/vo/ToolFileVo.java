package com.workhub.larktools.vo;

import java.io.Serializable;

/**
 * author:admin
 * description:
 * date:2019/8/15 17:01
 **/
public class ToolFileVo implements Serializable {
    private static final long serialVersionUID = 328172765656383294L;
    private String fileName;//附件名称
    private String fileId;//附件id
    private String uploaderName;//上传人
    private String uploadDate;//上传时间
    private String nodeName;//所属节点
    private String describe;//描述

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
