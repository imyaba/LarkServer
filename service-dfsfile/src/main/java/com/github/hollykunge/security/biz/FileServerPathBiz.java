package com.github.hollykunge.security.biz;

import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.entity.FileServerPathEntity;
import com.github.hollykunge.security.mapper.FileServerPathMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * fast服务文件路径基础数据业务处理层
 * @author zhhongyu
 * @since 2019-08-01
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class FileServerPathBiz extends BaseBiz<FileServerPathMapper, FileServerPathEntity> {
    @Override
    protected String getPageName() {
        return null;
    }
}
