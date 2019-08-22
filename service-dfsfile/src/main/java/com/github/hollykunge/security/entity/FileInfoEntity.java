package com.github.hollykunge.security.entity;

import com.github.hollykunge.security.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 文件基础数据实体类<br/>
 * @author zhhongyu
 */
@Data
@Table(name = "FILE_INFO")
public class FileInfoEntity extends BaseEntity {
    /**
     * 附件名称
     */
    @Column(name = "FILE_NAME")
    private String fileName;
    /**
     * 文件后缀名
     */
    @Column(name = "FILE_EXT")
    private String fileExt;
    /**
     * 附件类型（text、img、doc）
     */
    @Column(name = "FILE_TYPE")
    private String fileType;
    /**
     * 文件密级
     */
    @Column(name = "LEVELS")
    private String levels;

//    /**
//     * 加密标记(0为未加密文件，1为使用base64加密文件，2为使用位移加密文件)
//     */
//    @Column(name = "FILE_ENCRYPE")
//    private String fileEncrype;
    /**
     * 文件大小
     */
    @Column(name = "FILE_SIZE")
    private Double fileSize;
//    /**
//     * 附件路径
//     */
//    @Column(name = "PATH")
//    private String path;
    /**
     * 附件转码为可读取的路径
     */
    @Column(name = "READ_PATH")
    private String readPath;
    /**
     * 数据有效状态（1为有效，0为无效）
     */
    @Column(name = "STATUS")
    private String status;


    /**以下为新增字段，以上被注释掉的字段为需要在表中将其删除*/
    /**
     * 文件路径id
     */
    @Column(name = "FILE_PATH_ID")
    private String filePathId;

}
