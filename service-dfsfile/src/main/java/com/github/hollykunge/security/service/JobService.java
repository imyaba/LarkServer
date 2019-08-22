package com.github.hollykunge.security.service;

/**
 * 定时任务
 *
 * @author zhuqz
 * @since 2019-06-12
 */
public interface JobService {

    /**
     * 定时任务-处理无用附件
     *
     */
    void dealUnUsedFileList() throws Exception;
}