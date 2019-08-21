package com.github.hollykunge.biz;

import com.github.hollykunge.entity.FileInfoEntity;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.vo.FileInfoVO;
import com.github.hollykunge.vo.JwtInfoVO;
import com.netflix.hystrix.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: zhhongyu
 * @description: 隔离文件分块上传接口
 * @since: Create in 13:32 2019/8/21
 */
public class AppendFileHystrixCommondBiz extends HystrixCommand<ObjectRestResponse<FileInfoVO>> {
    private MultipartFile multipartFile;
    private String key;
    private String currentNo;
    private String totalSize;
    private FileInfoEntity fileInfoEntity;
    private FileInfoBiz fileInfoBiz;
    private JwtInfoVO jwtInfoVO;


    public AppendFileHystrixCommondBiz(FileInfoBiz fileInfoBiz,
                                       MultipartFile multipartFile,
                                       String key,
                                       String currentNo,
                                       String totalSize,
                                       FileInfoEntity fileInfoEntity,
                                       JwtInfoVO jwtInfoVO) {
        super(setter());
        this.fileInfoBiz = fileInfoBiz;
        this.multipartFile = multipartFile;
        this.key = key;
        this.currentNo = currentNo;
        this.totalSize = totalSize;
        this.fileInfoEntity = fileInfoEntity;
        this.jwtInfoVO = jwtInfoVO;
    }

    private static Setter setter() {
        HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("fdfs-file-append");
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("uploadAppendFile");
        HystrixThreadPoolKey hystrixThreadPoolKey = HystrixThreadPoolKey.Factory.asKey("fdfs-file-append");
        HystrixThreadPoolProperties.Setter threadProperties = HystrixThreadPoolProperties.Setter()
                .withCoreSize(50)
                .withKeepAliveTimeMinutes(5)
                .withQueueSizeRejectionThreshold(1000);
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
        return HystrixCommand.Setter.withGroupKey(groupKey).andCommandKey(commandKey)
                .andThreadPoolKey(hystrixThreadPoolKey)
                .andThreadPoolPropertiesDefaults(threadProperties)
                .andCommandPropertiesDefaults(commandProperties);
    }

    @Override
    protected ObjectRestResponse<FileInfoVO> getFallback() {
        ObjectRestResponse restResponse = new ObjectRestResponse();
        restResponse.setMessage("服务器正忙..");
        restResponse.setStatus(503);
        return restResponse;
    }

    @Override
    protected ObjectRestResponse<FileInfoVO> run() throws Exception {
        FileInfoVO fileInfoVO = fileInfoBiz.uploadAppendSensitiveFile(multipartFile, key, currentNo, totalSize, fileInfoEntity,jwtInfoVO);
        return new ObjectRestResponse<FileInfoVO>().rel(true).data(fileInfoVO);
    }
}
