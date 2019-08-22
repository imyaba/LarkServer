package com.github.hollykunge.security.util;

import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.comtants.FileComtants;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;


/***
 * Des文件加密解密
 * @author zhhongyu
 */
@Slf4j
public class FileDeEncrypt {


    public FileDeEncrypt(String keyStr) {
        genKey(keyStr);
        initCipher();
    }

    private Key key;
    /**
     * 解密密码
     */
    private Cipher cipherDecrypt;
    /**
     * 加密密码
     */
    private Cipher cipherEncrypt;


    /**
     * 加密文件的核心
     *
     * @param file 要加密的文件
     */
    public byte[] encryptFile(byte[] file) throws IOException {
        try {
            return this.fileTranfer(file, cipherEncrypt,"encrypt");
        } catch (Exception e) {
            log.error("加密文件出现异常", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    /***
     * 解密文件
     * @param file
     */
    public byte[] decryptFileContent(byte[] file) throws IOException {
        try {
            return this.fileTranfer(file, cipherDecrypt,"decrypt");
        } catch (Exception e) {
            log.error("解密文件{}出现异常", e.getMessage());
            throw e;
        }
    }

    private byte[] fileTranfer(byte[] file, Cipher cipher,String flag) throws IOException {
        if(file == null){
            throw new BaseException("加密或解密的文件不能为空...");
        }
        byte[] inputByte = null;
        int i = 0;
        //使用算法，如果file不是8的倍数，则用0补位到8的倍数
        if ((i = (file.length % 8)) != 0) {
            inputByte = new byte[file.length + (8 - i)];
            System.arraycopy(file, 0, inputByte, 0, file.length);
            //自定义补位，增加一个补位规则
            byte[] subByte = new byte[8-i];
            subByte = this.bytesData(subByte);
            System.arraycopy(subByte, 0, inputByte, file.length, subByte.length);
        } else {
            inputByte = file;
        }
        InputStream is = new ByteArrayInputStream(inputByte);
        OutputStream out = new ByteArrayOutputStream();
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
        }
        byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
        if(FileComtants.FILE_DECRYPT.equals(flag)){
            //解密时去掉末尾为定义规则的数据
            bytes = trimByte(bytes);
        }
        out.flush();
        cis.close();
        is.close();
        out.close();
        return bytes;
    }

    private byte[] bytesData(byte[] args) {
        for(int i = 0;i<args.length;i++){
            args[i] = FileComtants.FILE_ENCRYPT_SUB_RULE;
        }
        return args;
    }

    /**
     * 去掉末尾为定义规则的数据
     *
     * @param args
     * @return
     */
    private byte[] trimByte(byte[] args) {
        int pos = 0;
        //解密时最多循环8次，去除数组末尾为自定义规则的值
        for (int i = 0; i < 8; i++) {
            if(args[args.length-1-i] != FileComtants.FILE_ENCRYPT_SUB_RULE){
                pos = args.length-i;
                break;
            }
        }
        //如果pos为0，则证明末尾8个都为补位的，则全部去掉补位数据
        if(pos == 0){
            pos = args.length - 8;
        }
        //如果pos为原始数组长度，则结尾没有补位规则值，直接返回
        if(pos == args.length){
            return args;
        }
        byte[] result = new byte[pos];
        System.arraycopy(args,0,result,0,pos);
        return result;
    }

    private void initCipher() {
        try {
            // 加密的cipher
            cipherEncrypt = Cipher.getInstance(FileComtants.ENCRYPT_TYPE);
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, key);
            // 解密的cipher
            cipherDecrypt = Cipher.getInstance(FileComtants.ENCRYPT_TYPE);
            cipherDecrypt.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            log.info("加密初始化出现异常:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 自定义一个key
     *
     * @param keyRule
     */
    public void genKey(String keyRule) {
        // Key key = null;
        byte[] keyByte = keyRule.getBytes();
        // 创建一个空的八位数组,默认情况下为0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        key = new SecretKeySpec(byteTemp, FileComtants.KEY_ENCRYPT_TYPE);
    }

    public static void main(String[] args) {
//        int a = 17;
//        int i = 0;
//        i = a % 8;
//        int j = a + (8-i);
//        System.out.println(i+"  "+j);
//        String aa = "34234===   ";
//        byte[] bytes = aa.getBytes();
//        System.out.println("args = [" + bytes + "]");
        byte[] a = new byte[]{1, 2, 3, 0, 0, 0};
        String aa = new String(a);
        System.out.println("args = [" + aa + "]");
    }
}