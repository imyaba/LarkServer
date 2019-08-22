package com.github.hollykunge.security.util;

import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.comtants.FileComtants;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@Component
public class FastDFSClientWrapper {
    @Value("${upload.picture.mast}")
    private String permission;
    @Value("${upload.sensitiveFile.original}")
    private String sensitiveOriginalFile;

    @Autowired
    private FastFileStorageClient storageClient;

    private DateTime sentiveStartDate;

    private DateTime sentiveEndDate;

    private DateTime uploadStartDate;

    private DateTime uploadEndDate;


    /**
     * 上传文件
     * @param file 文件对象
     * @return 文件访问地址相对路径，如果想要访问该文件使用全路径如：http://nginxIP:80/ + 返回值
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(),file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()),null);
        return storePath.getFullPath();
    }

    /**
     * 上传文件(base64加密方式上传到服务器后为加密文件)
     * @param file 文件对象
     * @return 文件访问地址相对路径，如果想要访问该文件使用全路径如：http://nginxIP:80/ + 返回值
     * @throws IOException
     */
    public String uploadbase64SensitiveFile(MultipartFile file) throws IOException{
        String fileStr = Base64Utils.fileToBase64(file);
        return this.uploadFile(fileStr,FilenameUtils.getExtension(sensitiveOriginalFile));
    }

    /**
     * 上传文件(位移加密加密方式上传到服务器后为加密文件)
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadByteMoveSensitiveFile(MultipartFile file) throws Exception {
        byte[] bytes = EncryptionAndDeciphering.encryptFile(file);
        return this.uploadFile(bytes,FilenameUtils.getExtension(sensitiveOriginalFile));
    }

    /**
     * 使用文件流加密
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadCipherSensitiveFile(MultipartFile file) throws Exception {
        this.sentiveStartDate = new DateTime();
        FileDeEncrypt deEncrypt = new FileDeEncrypt(FileComtants.ENCRYPT_ROLE);
        byte[] bytes = deEncrypt.encryptFile(file.getBytes());
        this.sentiveEndDate = new DateTime();
        this.getDatePoor(sentiveStartDate,sentiveEndDate);
        uploadStartDate = new DateTime();
        String path = this.uploadFile(bytes, FilenameUtils.getExtension(sensitiveOriginalFile));
        uploadEndDate = new DateTime();
        this.getDatePoor(uploadStartDate,uploadEndDate);
        return path;
    }
    public String getDatePoor(DateTime startTime, DateTime endTime) {
        Interval interval = new Interval(startTime, endTime);
        log.info("响应时间:{}毫秒", interval.toDurationMillis());
        return "";
    }
    /**
     * 下载文件(加密文件下载)
     * @param fileUrl 文件url
     * @return
     */
    public byte[]  downloadBase64SensitiveFile(String fileUrl) throws IOException {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        byte[] bytes = storageClient.downloadFile(group, path, new DownloadByteArray());
        //转为str，再解密
        String fileStr = new String(bytes);
        bytes = Base64Utils.stringToByte(fileStr);
        return bytes;
    }

    /**
     * 位移加密下载
     * @param fileUrl
     * @return
     * @throws IOException
     */
    public byte[]  downloadByteMoveSensitiveFile(String fileUrl) throws IOException {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        byte[] bytes = storageClient.downloadFile(group, path, new DownloadByteArray());
        bytes= EncryptionAndDeciphering.decipherFile(bytes);
        return bytes;
    }

    /**
     * 使用cipherFile解密下载文件
     * @param fileUrl
     * @return
     * @throws IOException
     */
    public byte[]  downloadCipherSensitiveFile(String fileUrl) throws IOException {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        byte[] bytes = storageClient.downloadFile(group, path, new DownloadByteArray());
        FileDeEncrypt deEncrypt = new FileDeEncrypt(FileComtants.ENCRYPT_ROLE);
        return deEncrypt.decryptFileContent(bytes);
    }
    /**
     * 将一段字符串生成一个文件上传
     * @param content 文件访问地址相对路径，如果想要访问该文件使用全路径如：http://nginxIP:80/ 返回值
     * @param fileExtension
     * @return
     */
    public String uploadFile(String content, String fileExtension) {
        byte[] buff = content.getBytes(Charset.forName("UTF-8"));
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        StorePath storePath = storageClient.uploadFile(stream,buff.length, fileExtension,null);
        return storePath.getFullPath();
    }
    public String uploadFile(byte[] bytes, String fileExtension) {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        StorePath storePath = storageClient.uploadFile(stream,bytes.length, fileExtension,null);
        return storePath.getFullPath();
    }


    // 封装图片完整URL地址
//    private String getResAccessUrl(StorePath storePath) {
//        String fileUrl = "http://" + appConfig.getResHost()
//                + ":" + appConfig.getStoragePort() + "/" + storePath.getFullPath();
//        return fileUrl;
//    }

    /**
     * 删除文件
     * @param fileUrl 文件访问地址
     * @return
     */
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            throw new BaseException("要删除的文件id,不能为null...");
        }
        try {
            StorePath storePath = StorePath.praseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            log.debug(e.getMessage());
        }
    }

    /**
     * 下载文件
     * @param fileUrl 文件url
     * @return
     */
    public byte[]  download(String fileUrl) {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        byte[] bytes = storageClient.downloadFile(group, path, new DownloadByteArray());
        return bytes;
    }

    /**
     * 上传图片并生成缩略图
     * @param file
     * @return 文件访问地址相对路径，如果想要访问该文件使用全路径如：http://nginxIP:80/ 返回值
     * @throws IOException
     */
    public String crtThumbImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if(StringUtils.isEmpty(originalFilename)){
            throw new BaseException("上传文件有错误...");
        }
        String originalName = originalFilename.substring(originalFilename.indexOf(".") + 1, originalFilename.length());
        if(permission.contains(originalName)){
            throw new BaseException("生成缩略图的图片类型不允许...");
        }
        System.out.printf(originalFilename);
        //上传图片的缩略图
        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(file.getInputStream(),
                file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()),null);
        return storePath.getFullPath();
    }
}