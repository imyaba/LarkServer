package com.github.hollykunge.security.controller;

import com.github.hollykunge.security.biz.FileInfoBiz;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.rest.BaseController;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.github.hollykunge.security.common.vo.FileInfoVO;
import com.github.hollykunge.security.comtants.FileComtants;
import com.github.hollykunge.security.entity.FileInfoEntity;
import com.github.hollykunge.security.jwt.FileJwtInfo;
import com.github.hollykunge.security.util.FastDFSClientWrapper;
import com.github.hollykunge.security.util.FileTypeEnum;
import com.github.hollykunge.security.vo.JwtInfoVO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * file文件接口
 *
 * @author zhhongyu
 * @since 2019-06-18
 */
@Slf4j
@RestController
@RequestMapping("/fdfs/file")
public class FastDfsController extends BaseController<FileInfoBiz, FileInfoEntity> {
    @Autowired
    private FastDFSClientWrapper dfsClient;
    @Autowired
    private FileJwtInfo fileJwtInfo;


//    @PostMapping("/upload")
//    @ResponseBody
//    public ObjectRestResponse<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
//        String imgUrl = dfsClient.uploadFile(file);
//        return new ObjectRestResponse<>().data(imgUrl).rel(true);
//    }

    /**
     * 上传接口
     *
     * @param file file文件
     * @return 文件访问路径
     * @throws Exception
     */
    @PostMapping("/upload")
    @ResponseBody
    public ObjectRestResponse<FileInfoVO> upload(@RequestParam("file") MultipartFile file) throws Exception {
        FileInfoVO fileInfoVO = baseBiz.uploadFile(file);
        return new ObjectRestResponse<>().data(fileInfoVO).rel(true);
    }

//    @PostMapping("/sensitiveUpload")
//    @ResponseBody
//    public ObjectRestResponse<String> uploadSensitiveFile(@RequestParam("file") MultipartFile file) throws Exception {
//        String imgUrl = dfsClient.uploadSensitiveFile(file);
//        return new ObjectRestResponse<>().data(imgUrl).rel(true);
//    }

    /**
     * 上传加密文件接口（base64加密方式）
     *
     * @param file file文件
     * @return 文件访问路径
     * @throws Exception
     */
    @PostMapping("/sensitiveUpload")
    @ResponseBody
    public ObjectRestResponse<String> uploadChiperSensitiveFile(@RequestParam("file") MultipartFile file) throws Exception {
        //使用base64进行加密
        FileInfoVO fileInforVO = baseBiz.uploadSensitiveFile(file, FileComtants.SENSITIVE_CIPHER_TYPE);
        return new ObjectRestResponse<>().data(fileInforVO).rel(true);
    }

    /**
     * 采用位移加密方式上传文件接口
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/sensitiveUpload2")
    @ResponseBody
    public ObjectRestResponse<String> uploadByteMoveSensitiveFile(@RequestParam("file") MultipartFile file) throws Exception {
        //使用base64进行加密
        FileInfoVO fileInfoVO = baseBiz.uploadSensitiveFile(file, FileComtants.SENSITIVE_BYTEMOVE_TYPE);
        return new ObjectRestResponse<>().data(fileInfoVO).rel(true);
    }

    /**
     * 删除文件接口
     *
     * @param fileId 文件id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ObjectRestResponse<Boolean> removeFile(@RequestParam String fileId) throws Exception {
        baseBiz.deleteFile(fileId);
        return new ObjectRestResponse<>().rel(true);
    }

    /**
     * 文件下载(普通文件下载，没有加密)
     *
     * @param fileId   文件id
     * @param response
     * @throws Exception
     */
    @GetMapping("/download")
    public void download(@RequestParam String fileId, HttpServletResponse response) throws Exception {
        Map<String, Object> stringObjectMap = baseBiz.downLoadFile(fileId, FileComtants.NO_SENSITIVE_TYPE);
        String fileName = (String) stringObjectMap.get("fileName");
        byte[] data = (byte[]) stringObjectMap.get("fileByte");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(data, outputStream);
    }

    /**
     * 下载加密文件(文件流加密)
     *
     * @param fileId
     * @param response
     * @throws Exception
     */
    @GetMapping("/sensitiveDownload")
    public void downloadChiperSensitiveFile(@RequestParam String fileId, HttpServletResponse response) throws Exception {
        Map<String, Object> stringObjectMap = baseBiz.downLoadFile(fileId, FileComtants.SENSITIVE_CIPHER_TYPE);
        String fileName = (String) stringObjectMap.get("fileName");
        byte[] data = (byte[]) stringObjectMap.get("fileByte");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(data, outputStream);
    }

    /**
     * 下载加密文件（位移加密下载）
     *
     * @param fileId
     * @param response
     * @throws Exception
     */
    @GetMapping("/sensitiveDownload2")
    public void downloadByteMoveSensitiveFile(@RequestParam String fileId, HttpServletResponse response) throws Exception {
        Map<String, Object> stringObjectMap = baseBiz.downLoadFile(fileId, FileComtants.SENSITIVE_BYTEMOVE_TYPE);
        String fileName = (String) stringObjectMap.get("fileName");
        byte[] data = (byte[]) stringObjectMap.get("fileByte");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(data, outputStream);
    }

    /**
     * 图片展示接口（无加密图片）
     *
     * @param fileId
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getImage")
    public void getFile(@RequestParam String fileId, HttpServletResponse response) throws IOException {
        baseBiz.getImg(fileId, response, FileComtants.NO_SENSITIVE_TYPE);
    }

    /**
     * 加密图片展示(文件流加密)
     *
     * @param fileId
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getSensitiveImage")
    public void getSensitiveImage(@RequestParam String fileId, HttpServletResponse response) throws IOException {
        baseBiz.getImg(fileId, response, FileComtants.SENSITIVE_CIPHER_TYPE);
    }

    /**
     * 加密图片展示（位移加密图片）
     *
     * @param fileId
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getSensitiveImage2")
    public void getSensitiveImage2(@RequestParam String fileId, HttpServletResponse response) throws IOException {
        baseBiz.getImg(fileId, response, FileComtants.SENSITIVE_BYTEMOVE_TYPE);
    }

    @PostMapping("/thumbImage")
    @ResponseBody
    public ObjectRestResponse<String> crtThumbImage(@RequestParam("file") MultipartFile file) throws Exception {
        String imgUrl = dfsClient.crtThumbImage(file);
        return new ObjectRestResponse<>().data(imgUrl).rel(true);
    }

    /**
     * 文件分块上传接口（0到n-1块文件位数必须为8的倍数，最后一块文件可为任意）
     * 下载这种文件时需要采用文件流加密下载接口，文件分块不能做秒传功能，因为不能确定唯一文件性
     *
     * @param file 文件
     * @return 最后一块文件时返回文件基本信息实体，供前端使用
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/appendUploadFile",method = RequestMethod.POST)
    @HystrixCommand(
            fallbackMethod = "uploadAppendFileFallback",
            threadPoolProperties = {
                @HystrixProperty(name = "coreSize", value = "50"),
                @HystrixProperty(name = "maxQueueSize", value = "500"),
                @HystrixProperty(name = "queueSizeRejectionThreshold", value = "20")},
            commandProperties = {
                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "30000"),
                @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20")
            }
            )
    public ObjectRestResponse<FileInfoVO> uploadAppendFile(@RequestParam("file") MultipartFile file,HttpServletRequest request
    ) throws Exception {
        String path = request.getHeader("path");
        String currentNo = request.getHeader("currentNo");
        String totalSize = request.getHeader("totalSize");
        String key = request.getHeader("key");
        String fileName = request.getHeader("fileName");
        String fileSize = request.getHeader("fileSize");
        if (StringUtils.isEmpty(currentNo)) {
            throw new BaseException("当前段数不能为空...");
        }
        if (StringUtils.isEmpty(totalSize)) {
            throw new BaseException("总段数不能为空...");
        }
        if (StringUtils.isEmpty(key)) {
            throw new BaseException("文件唯一性不能为空...");
        }
        if (file == null || file.getBytes() == null) {
            throw new BaseException("文件不能为空...");
        }
        if (StringUtils.isEmpty(fileName)) {
            throw new BaseException("文件名不能为空...");
        }
        if (StringUtils.isEmpty(fileSize)) {
            throw new BaseException("文件大小不能为空...");
        }
        currentNo = URLDecoder.decode(currentNo, "UTF-8");
        totalSize = URLDecoder.decode(totalSize, "UTF-8");
        key = URLDecoder.decode(key, "UTF-8");
        fileName = URLDecoder.decode(fileName, "UTF-8");
        fileSize = URLDecoder.decode(fileSize, "UTF-8");
        log.info("接收到文件{}开始...", fileName);
        FileInfoEntity fileInfoEntity = null;
        JwtInfoVO jwtInfoVO = fileJwtInfo.getJwtInfoVO(request);
        if (Integer.parseInt(currentNo) == Integer.parseInt(totalSize)) {
            fileInfoEntity = this.transferFileInfo(fileName, Double.valueOf(fileSize), jwtInfoVO);
        }
        //文件分块上传，加密方式采用文件流加密
        FileInfoVO fileInfoVO = baseBiz.uploadAppendSensitiveFile(file, key, currentNo, totalSize, fileInfoEntity, jwtInfoVO);
        return new ObjectRestResponse<FileInfoVO>().data(fileInfoVO).rel(true);
    }

    public ObjectRestResponse<FileInfoVO> uploadAppendFileFallback( MultipartFile file,HttpServletRequest request
    ) throws Exception {
        ObjectRestResponse<FileInfoVO> result = new ObjectRestResponse<FileInfoVO>();
        result.setStatus(503);
        result.setMessage("服务忙...稍后重试");
        return result;
    }

    private FileInfoEntity transferFileInfo(String fileName, Double fileSize, JwtInfoVO jwtInfoVO) throws Exception {
        FileInfoEntity fileInfoEntity = new FileInfoEntity();
        fileInfoEntity.setId(UUIDUtils.generateShortUuid());
        if (jwtInfoVO != null) {
            BeanUtils.copyProperties(jwtInfoVO, fileInfoEntity);
        }
        String suffix = "";
        String fileExt = "";
        String regixValue = FileComtants.FILE_REGIX_VALUE;
        if (fileName.indexOf(regixValue) != -1) {
            suffix = fileName.substring(fileName.lastIndexOf(regixValue));
            fileName = fileName.substring(0, fileName.lastIndexOf(regixValue));
        }
        if (!"".equals(suffix) && !regixValue.equals(suffix)) {
            fileExt = suffix.substring(suffix.indexOf(regixValue) + 1);
        }
        fileInfoEntity.setFileExt(fileExt);
        fileInfoEntity.setFileName(fileName);
        String fileType = "";
        FileTypeEnum fileTypeEnum = FileTypeEnum.getEnumByValue(fileExt);
        fileType = fileTypeEnum.getType();
        fileInfoEntity.setFileType(fileType.toLowerCase());
        fileInfoEntity.setFileSize(fileSize);
        fileInfoEntity.setStatus(FileComtants.EFECTIVE_FILE);

        return fileInfoEntity;
    }
}
