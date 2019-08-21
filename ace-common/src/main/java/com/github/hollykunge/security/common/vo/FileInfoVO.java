package com.github.hollykunge.security.common.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author zhhongyu
 * fastdfs上传文件后返回文件基本信息实体类
 */
@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FileInfoVO {
    /**
     * 附件id
     */
    private String fileId;
    /**
     * 附件名称
     */
    private String fileName;
    /**
     * 文件后缀名
     */
    private String fileExt;
    /**
     * 附件转码为可读取的路径
     */
    private String readPath;
    /**
     * 文件在fast服务全路径
     */
    private String fullPath;
    /**
     * 文件分块时，成功上传到服务器的块数
     */
    private String isSuccessNo;
    /**
     * 下一块
     */
    private String nextNo;
    /**
     * 总块数
     */
    private String totalSize;
}