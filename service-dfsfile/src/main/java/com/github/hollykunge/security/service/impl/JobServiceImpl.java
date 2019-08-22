package com.github.hollykunge.security.service.impl;


import com.github.hollykunge.security.dao.JobDao;
import com.github.hollykunge.security.service.JobService;
import com.github.hollykunge.security.util.CommonUtil;
import com.github.hollykunge.security.util.FastDFSClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时任务-历史消息处理
 *
 * @author zhuqz
 * @since 2019-06-12
 */
@Service("jobServiceImpl")
public class JobServiceImpl implements JobService {
    private static Logger log = LoggerFactory.getLogger(JobServiceImpl.class);
    @Resource
    private JobDao jobDao;
    @Autowired
    private FastDFSClientWrapper dfsClient;
    /**
     * 定时任务-处理无用附件
     *
     */
    public void dealUnUsedFileList() throws Exception{
        try {
            List<String> filePathList=this.jobDao.getUnUsedFileList();
            this.jobDao.delUnUsedFileList();
            for(String path:filePathList){
                try {
                    dfsClient.deleteFile(path);
                } catch (Exception e) {
                    throw e;
                }

            }
            log.info("定时任务执行成功，共删除无用附件"+filePathList.size()+"个");
        }catch (Exception e){
            log.error("定时任务删除无用附件出错：");
            log.error(CommonUtil.getExceptionMessage(e));
        }

    }
}
