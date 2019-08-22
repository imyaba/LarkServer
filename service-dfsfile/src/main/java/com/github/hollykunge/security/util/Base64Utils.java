package com.github.hollykunge.security.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

/**
 * base64转码工具类
 * @author zhhongyu
 */
public class Base64Utils {

    private static Base64.Encoder base64Encoder = Base64.getEncoder();
    private static Base64.Decoder base64Decoder =Base64.getDecoder();

    /**
     * 将file文件转为base64编码
     * @param multipartFile 文件
     * @return base64字符串
     */
    public static  String fileToBase64(MultipartFile multipartFile) throws IOException{
        String base64EncoderImg = byteToString(multipartFile.getBytes());
        return base64EncoderImg;
    }
    /**
     * base64将byte[]转为String
     * @param source byte[]
     * @return String
     */
    public static String byteToString(byte[] source) {
        //Base64 Encoded
        String encoded = base64Encoder.encodeToString(source);
        return encoded;
    }

    /**
     * base64将String转为byte[]
     * @param source string
     * @return byte[]
     */
    public static byte[] stringToByte(String source) throws IOException {
        return base64Decoder.decode(source);
    }
}
