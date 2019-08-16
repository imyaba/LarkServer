package com.workhub.larktools.feign;

import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.workhub.larktools.config.FeignMultipartSupportConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

//调用fastdfs上传文件
@FeignClient(value = "service-dfsfile", configuration = FeignMultipartSupportConfig.class,fallbackFactory = FileClientFallbackFactory.class)
@Repository
public interface IFastDFSService {
    //上传接口
    @RequestMapping(method = RequestMethod.POST, value = "/file/sensitiveUpload",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectRestResponse sensitiveUpload(@RequestPart("file")  MultipartFile file) throws Exception;

    // 文件下载
    @RequestMapping(value = "/file/sensitiveDownload", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response sensitiveDownload(@RequestParam("fileId") String fileId) throws Exception;

    // 文件删除
    @RequestMapping(value = "/file/delete", method = RequestMethod.DELETE)
    public ObjectRestResponse removeFile(@RequestParam("fileId") String fileId) throws Exception;
}

