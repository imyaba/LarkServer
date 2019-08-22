package com.github.hollykunge.security.vo;
//文件监控前端返回格式
public class FileMonitoringVO {
    //文件id
    private String fileId;

    //文件名称
    private String fileName;

    //文件路径
    private String filePath;

    //文件大小（M）
    private String fileSize;

    //上传时间 yyyy-MM-dd hh:mm:ss
    private String uploadTime;

    //上传人姓名
    private String uploadUserName;

    //文件密级
    private String levels;

    //群/私 文件 0私人1群
    private String isGroup;

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadUserName() {
        return uploadUserName;
    }

    public void setUploadUserName(String uploadUserName) {
        this.uploadUserName = uploadUserName;
    }

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }
}
