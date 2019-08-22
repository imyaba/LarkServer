package com.github.hollykunge.security.util;

import com.github.hollykunge.security.common.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

//文件加密解密
public class EncryptionAndDeciphering {
    private static Logger log = LoggerFactory.getLogger(EncryptionAndDeciphering.class);
    private static final int OFFSET = 5;
    private static final String ENCRYPTION_PREFIX = "Encrypted_";
    private static final String DECIPHER_PREFIX = "Deciphered_";

    public static boolean changeFile(File inFile, int offset, String prefix) {
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
        //check input file
        if (!inFile.exists()) {
            log.error("文件加密错误：文件不存在");
            return false;
        }

        //create output file
        File outFile = new File(inFile.getParent()+"/"+prefix + inFile.getName());
        if (outFile.exists()) {
            log.error("文件加密错误：文件已经存在");
            return false;
        }
        try {
            if (!outFile.createNewFile()) {
                log.error("文件加密错误：文件无法创建");
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        //IO
        try {
            input = new BufferedInputStream(new FileInputStream(inFile));
            output = new BufferedOutputStream(new FileOutputStream(outFile));
            int value;
            for (; ; ) {
                value = input.read();
                if (value == -1) {
                    break;
                }
                output.write(value + offset); //change
            }
            output.flush();
        } catch (IOException e) {
            log.error(CommonUtil.getExceptionMessage(e));
            return false;
        }finally {
            if(input!=null){
                try {
                    input.close();
                    inFile.delete();
                } catch (IOException e) {
                    log.error(CommonUtil.getExceptionMessage(e));
                }
            }
            if(output!=null){
                try {
                    output.close();
                    outFile.renameTo(inFile);
                } catch (IOException e) {
                    log.error(CommonUtil.getExceptionMessage(e));
                }
            }
        }
        return true;
    }
    public static byte[] changeFile(MultipartFile inFile, int offset) throws Exception{
        byte[] bytes = inFile.getBytes();
        for(int i=0;i<bytes.length;i++){
            int j = bytes[i];
            bytes[i]=(byte) (j+offset);
        }
       return bytes;
    }
    public static String  changeFileBack(File inFile, int offset, String prefix) {
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
        //check input file
        if (!inFile.exists()) {
            log.error("文件解密错误：文件不存在");
            return "0";
        }

        //create output file
        File outFile = new File(inFile.getParent()+"/"+prefix + UUIDUtils.generateShortUuid());
        if (outFile.exists()) {
            log.error("文件解密错误：文件已经存在");
        }
        try {
            if (!outFile.createNewFile()) {
                log.error("文件解密错误：文件无法创建");
                return "-1";
            }
        } catch (IOException e) {
            log.error(CommonUtil.getExceptionMessage(e));
            return "-1";
        }

        //IO
        try {
            input = new BufferedInputStream(new FileInputStream(inFile));
            output = new BufferedOutputStream(new FileOutputStream(outFile));
            int value;
            for (; ; ) {
                value = input.read();
                if (value == -1) {
                    break;
                }
                output.write(value + offset); //change
            }
            output.flush();
        } catch (IOException e) {
            log.error(CommonUtil.getExceptionMessage(e));
            return "-1";
        }finally {
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(CommonUtil.getExceptionMessage(e));
                }
            }
            if(output!=null){
                try {
                    output.close();
                } catch (IOException e) {
                    log.error(CommonUtil.getExceptionMessage(e));
                }
            }
        }
        return outFile.getPath();
    }
    public static byte[]  changeFileBack(byte[] bytes, int offset) {
           for(int i=0;i<bytes.length;i++){
               int j = bytes[i];
               bytes[i]=(byte) (j+offset);
           }
           return  bytes;
    }
    public static boolean encryptFile(File inFile, int offset, String prefix) {
        return changeFile(inFile, offset, prefix);
    }
//调用的加密文件算法
    public static boolean encryptFile(File inFile) {
        return changeFile(inFile, OFFSET, ENCRYPTION_PREFIX);
    }
    public static byte[] encryptFile(MultipartFile inFile) throws Exception {
        return changeFile(inFile, OFFSET);
    }
    public static boolean decipherFile(File inFile, int offset, String prefix) {
        return changeFile(inFile, -offset, prefix);
    }
//调用的文件解密算法

    public static String decipherFile(File inFile) {
        return changeFileBack(inFile, -OFFSET, DECIPHER_PREFIX);
    }
    public static byte[] decipherFile(byte[] bytes) {
        return changeFileBack(bytes, -OFFSET);
    }
}