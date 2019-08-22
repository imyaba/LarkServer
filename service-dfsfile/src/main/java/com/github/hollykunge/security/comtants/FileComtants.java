package com.github.hollykunge.security.comtants;

import com.github.hollykunge.security.util.DesCodeEnum;

/**
 * file服务用常量类
 * @author zhhongyu
 * @since 2019-07-29
 */
public class FileComtants {
    public final static String FILE_REGIX_VALUE = ".";
    public final static String NO_SENSITIVE_TYPE = "0";
    /**
     * 采用base64编码
     */
    public final static String SENSITIVE_BASE64_TYPE = "1";
    public final static String SENSITIVE_BYTEMOVE_TYPE = "2";
    public final static String SENSITIVE_CIPHER_TYPE = "3";
    /**
     * 无效文件标识
     */
    public final static String INVALID_FILE = "0";
    /**
     * 有效文件标识
     */
    public final static String EFECTIVE_FILE = "1";
    /**
     * 如果数据库中没有文件名称，设置默认名称
     */
    public final static String DOWNLOAD_FILE_NAME = "test";
    /**
     * 如果数据库中没有文件后缀，设置默认后缀
     */
    public final static String DOWNLOAD_FILE_EXT = "txt";

    /**
     * 加密类型
     */
    public static final String ENCRYPT_TYPE = DesCodeEnum.DESEDE_ECB_NoPadding.getCode();

    public static final String KEY_ENCRYPT_TYPE = DesCodeEnum.DESEDE.getCode();
    /**
     * key生成规则
     *
     */
    public static final String ENCRYPT_ROLE = "FAST FILE DE-ENCRYPT";
    /**
     * 加密标识
     */
    public static final String FILE_ENCRYPT = "encrypt";
    /**
     * 解密标识
     */
    public static final String FILE_DECRYPT = "decrypt";
    /**
     * 自定义加密补位规则
     */
    public static final byte FILE_ENCRYPT_SUB_RULE = 22;
    /**
     * 文件分块上传标识
     */
    public static final String REDIS_KEY_APPEND_FILE = "append_file"+ FileComtants.REDIS_KEY_CON_APPEND_FILE;

    public static final String REDIS_KEY_PRE_APPEND_FILE = "temp";

    public static final String REDIS_KEY_CON_APPEND_FILE = ":";

    public static final String REDIS_KEY_PRE = FileComtants.REDIS_KEY_PRE_APPEND_FILE+ FileComtants.REDIS_KEY_CON_APPEND_FILE;

}
