package com.github.hollykunge.security.scheduling;

import com.github.hollykunge.security.biz.FileServerPathBiz;
import com.github.hollykunge.security.comtants.FileComtants;
import com.github.hollykunge.security.entity.FileServerPathEntity;
import com.github.hollykunge.security.util.FastDFSClientWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author: zhhongyu
 * @description: 临时碎片文件清除定时任务
 * @since: Create in 14:05 2019/8/20
 */
@Slf4j
@Component
@EnableScheduling
@Transactional(rollbackFor = Exception.class)
public class TempFileDropSchede {
    @Autowired
    private FileServerPathBiz fileServerPathBiz;

    @Autowired
    private FastDFSClientWrapper dfsClient;

    /**
     * 每天凌晨清除残存文件片
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void fileFrash() {
        FileServerPathEntity fileServerPathEntity = new FileServerPathEntity();
        fileServerPathEntity.setStatus(FileComtants.INVALID_FILE);
        List<FileServerPathEntity> invalidFiles = fileServerPathBiz.selectList(fileServerPathEntity);
        invalidFiles.stream().forEach((FileServerPathEntity fileEntity) ->{
            String path = fileEntity.getPath();
            if(StringUtils.isEmpty(path)){
                fileServerPathBiz.deleteById(fileEntity.getId());
            }
            fileServerPathBiz.deleteById(fileEntity.getId());
            dfsClient.deleteFile(path);
            log.info("残留文件片{}，已经被清除",path);
        });
    }
}

