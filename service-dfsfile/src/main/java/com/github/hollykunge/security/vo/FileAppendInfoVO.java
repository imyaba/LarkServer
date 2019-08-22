package com.github.hollykunge.security.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author: zhhongyu
 * @description: 文件分块缓存在redies中的文件块基本信息实体类
 * @since: Create in 13:09 2019/8/15
 */
@Data
@ToString
public class FileAppendInfoVO {
    /**
     * 文件唯一性编码
     */
    private String md5Key;
    /**
     * 文件对应文件服务器路径
     */
    private String filePath;
    /**
     * 传成功的块数
     */
    private String successSize;
    /**
     * 总块数
     */
    private String totalSize;
}
