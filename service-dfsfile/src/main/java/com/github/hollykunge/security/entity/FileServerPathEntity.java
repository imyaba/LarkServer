package com.github.hollykunge.security.entity;

import com.github.hollykunge.security.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 文件映射fastdfs服务路径实体类<br/>
 * @author zhhongyu
 */
@Data
@Table(name = "FILE_SERVER_PATH")
public class FileServerPathEntity extends BaseEntity {
    /**
     * 加密标记(0为未加密文件，1为使用base64加密文件，2为使用位移加密文件)
     */
    @Column(name = "FILE_ENCRYPE")
    private String fileEncrype;

    /**
     * 附件路径
     */
    @Column(name = "PATH")
    private String path;

    /**
     * 数据有效状态（1为有效，0为无效）
     */
    @Column(name = "STATUS")
    private String status;
}
