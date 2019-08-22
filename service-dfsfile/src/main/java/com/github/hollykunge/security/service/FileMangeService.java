package com.github.hollykunge.security.service;

import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.entity.FileManageInf;
import com.github.hollykunge.security.vo.FileMonitoringVO;

import java.util.List;
import java.util.Map;

public interface FileMangeService {
    FileManageInf queryById(String fileId);
    int insert(FileManageInf fileManageInf);
    int update(FileManageInf fileManageInf);
    int deleteById(String fileId);
    int fileUpdate(String fileId, String level, String type);
    String getFileSizeByDB(String queryType, String queryDate, String returnUnit);
    List<Map<String,String>> getFileSizeRangeByDB(String queryType, String queryDateBegin, String queryDateEnd, String returnUnit);
    //文件监控查询
    public TableResultResponse<FileMonitoringVO> fileMonitoring(Map<String, Object> params) throws Exception;
}
