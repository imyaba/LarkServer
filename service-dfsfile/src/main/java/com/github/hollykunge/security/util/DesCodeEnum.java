package com.github.hollykunge.security.util;

/**
 * @author: zhhongyu
 * @description:
 * @since: Create in 16:59 2019/8/13
 */
public enum DesCodeEnum {
    DESEDE("DES"),
    /**
     * 使用不填充，文件服务自定义填充
     */
    DESEDE_ECB_NoPadding("DES/ECB/NoPadding"),
    /*
     * 加密前对数据字节长度对8取余，
     * 余数大于0,则差几个字节就补几个字节，
     * 字节数值即为补充的字节数，若为0则补充8个字节的8 ，
     * 解密后就取最后一个字节，值为m，则从数据尾部删除m个字节
     * ，剩余数据即为加密前的原文。
     */
    DESEDE_ECB_PKCS5Padding("DES/ECB/PKCS5Padding"),
    DESEDE_ECB_PKCS7Padding("DES/ECB/PKCS71Padding"),
    DESEDE_ECB_ISO10126Padding("DES/ECB/ISO10126Padding");

    private String code;

    DesCodeEnum(String code) {
        this.setCode(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
