package com.github.hollykunge.security.util;

import com.ace.cache.api.impl.CacheRedis;
import com.alibaba.fastjson.JSONObject;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.comtants.FileComtants;
import com.github.hollykunge.security.vo.FileAppendInfoVO;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhhongyu
 * @description: 文件分块上传
 * @since: Create in 13:41 2019/8/13
 */
@Slf4j
@Component
public class AppendFileUtils {
    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    /**
     * 所在组卷
     */
    @Value("${fdfs.groupName}")
    private String groupName = "group1";

    @Value("${upload.sensitiveFile.original}")
    private String sensitiveOriginalFile;
    @Autowired
    private CacheRedis cacheRedis;

    private DateTime sentiveStartDate;

    private DateTime sentiveEndDate;

    private DateTime uploadStartDate;

    private DateTime uploadEndDate;


    /**
     * 分块上传第一个文件
     *
     * @param file
     * @return path
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws IOException {
        StorePath group = appendFileStorageClient.uploadAppenderFile(groupName, file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()));
        return group.getPath();
    }

    /**
     * 采用文件流加密文件(分块中的n-1块必须为8的倍数的长度，第n块可以为任意长度)
     *
     * @param file
     * @return
     * @throws IOException
     */
    public Map<String, Object> uploadCipherSensitiveFile(MultipartFile file, String fileKey,
                                                         String currentNo,String totalSize) throws Exception {
        if(Integer.parseInt(currentNo)>Integer.parseInt(totalSize)){
            throw new BaseException("currentNo more then totalSize...");
        }
        log.info(fileKey);
        Map<String, Object> result = new HashMap<String, Object>(256);
        //先从缓存中获取已经传成功的文件块
        String fileAppendInfoJson = cacheRedis.get(FileComtants.REDIS_KEY_APPEND_FILE+fileKey);
        if(StringUtils.isEmpty(fileAppendInfoJson)){
            //防止缓存穿透设置null值，时长30分钟
            cacheRedis.set(FileComtants.REDIS_KEY_APPEND_FILE,null, 30);
        }
        FileAppendInfoVO fileAppendInfoVO = JSONObject.parseObject(fileAppendInfoJson, FileAppendInfoVO.class);
        if (fileAppendInfoVO != null && Integer.parseInt(fileAppendInfoVO.getSuccessSize())
                >= Integer.parseInt(currentNo)) {
            result.put("isSuccessNo", fileAppendInfoVO.getSuccessSize());
            result.put("path", fileAppendInfoVO.getFilePath());
            return result;
        }
        String path = null;
        String fullPath = null;
        //是传的是第一块以后的文件,path已经存在在缓存中
        if(fileAppendInfoVO != null){
            fullPath = fileAppendInfoVO.getFilePath();
            path = fileAppendInfoVO.getFilePath();
            path = path.substring(path.indexOf("/")+1, path.length());
        }
        sentiveStartDate = new DateTime();
        FileDeEncrypt deEncrypt = new FileDeEncrypt(FileComtants.ENCRYPT_ROLE);
        byte[] bytes = deEncrypt.encryptFile(file.getBytes());
        sentiveEndDate = new DateTime();
        log.info("加密文件时间为：");
        this.getDatePoor(sentiveStartDate,sentiveEndDate);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        //1.传第一块的文件到服务器
        if (StringUtils.isEmpty(path)) {
            uploadStartDate = new DateTime();
            StorePath storePath = appendFileStorageClient.uploadAppenderFile(groupName, inputStream, bytes.length, FilenameUtils.getExtension(sensitiveOriginalFile));
            //1.1 缓存成功的文件块信息，文件块有效时长30分钟
            FileAppendInfoVO fileAppendInfoCache = this.tranferVO(fileKey,currentNo, storePath.getFullPath(),totalSize);
            cacheRedis.set(FileComtants.REDIS_KEY_APPEND_FILE+fileKey,
                    JSONObject.toJSONString(fileAppendInfoCache),30);
            //1.2该缓存值用于如果用户分块时，没有全部上传文件，可能存在垃圾文件片，删除这种情况下的垃圾文件
            cacheRedis.set(FileComtants.REDIS_KEY_PRE+fileKey,
                    JSONObject.toJSONString(fileAppendInfoCache),
                    35,"该缓存值35分钟失效，防止存在垃圾文件片情况");
            uploadEndDate = new DateTime();
            log.info("上传文件并进行redies时间为：");
            this.getDatePoor(uploadStartDate,uploadEndDate);
            result.put("isSuccessNo", currentNo);
            result.put("path", storePath.getFullPath());
            return result;
        }
        uploadStartDate = new DateTime();
        //2.续传文件到服务器
        appendFileStorageClient.appendFile(groupName, path, inputStream, bytes.length);
        //3.缓存成功的文件块信息,文件块有效时长30分钟
        FileAppendInfoVO fileAppendInfoCache = this.tranferVO(fileKey,currentNo, fullPath,totalSize);
        cacheRedis.set(FileComtants.REDIS_KEY_APPEND_FILE+fileKey,
                JSONObject.toJSONString(fileAppendInfoCache),30);
        //4. 该缓存值用于如果用户分块时，没有全部上传文件，可能存在垃圾文件片，删除这种情况下的垃圾文件
        cacheRedis.set(FileComtants.REDIS_KEY_PRE+fileKey,
                JSONObject.toJSONString(fileAppendInfoCache),
                35,"该缓存值35分钟失效，防止存在垃圾文件片情况");
        uploadEndDate = new DateTime();
        log.info("续传文件并进行redies时间为：");
        this.getDatePoor(uploadStartDate,uploadEndDate);
        result.put("isSuccessNo", currentNo);
        result.put("path", fullPath);
        return result;
    }

    public String getDatePoor(DateTime startTime, DateTime endTime) {
        Interval interval = new Interval(startTime, endTime);
        log.info("响应时间:{}毫秒", interval.toDurationMillis());
        return "";
    }

    /**
     * 文件分块要拼接的文件
     *
     * @param file
     * @param path
     * @throws IOException
     */
    private void appendFile(MultipartFile file, String path) throws IOException {
        appendFileStorageClient.appendFile(groupName, path, file.getInputStream(), file.getSize());
    }

    private FileAppendInfoVO tranferVO(String md5Key, String currentNo, String path, String totalSize) {
        FileAppendInfoVO fileAppendInfoVO = new FileAppendInfoVO();
        fileAppendInfoVO.setSuccessSize(currentNo);
        fileAppendInfoVO.setFilePath(path);
        fileAppendInfoVO.setMd5Key(md5Key);
        fileAppendInfoVO.setTotalSize(totalSize);
        return fileAppendInfoVO;
    }

    public void test() {
        //1.测试分为两块上传，将二进制数组拆分为两块
//        byte[] b = file.getBytes();
//        byte[] b1 = new byte[8];
//        byte[] b2 = new byte[b.length-8];
//        System.arraycopy(b,0,b1,0,8);
//        System.arraycopy(b,8,b2,0,b.length-8);
//        //先上传第一份，再追加第二个
//        b1 = deEncrypt.encryptFile(b1);
//        b2 = deEncrypt.encryptFile(b2);
//        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(b1);
//        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(b2);
//        StorePath storePath = appendFileStorageClient.uploadAppenderFile(groupName, inputStream1, b1.length, FilenameUtils.getExtension(file.getOriginalFilename()));
//        appendFileStorageClient.appendFile(groupName,storePath.getPath(),inputStream2,b2.length);
//        return storePath.getFullPath();
        //测试结束
    }

    public void setRedies(){
        cacheRedis.set("service-dfsfile:1111111111111111:path:group1/M00/00/03/CgsYil1U-SKAEEvEAARUwCdlvQI949.png",123,1);
    }

}
