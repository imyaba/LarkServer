package com.github.hollykunge.security.dao;

import com.github.hollykunge.security.entity.FileManageInf;
import com.github.hollykunge.security.vo.FileMonitoringVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FileManageDao  extends Mapper<FileManageInf> {
    FileManageInf queryById(@Param("fileId") String fileId);
    int insert(FileManageInf fileManageInf);
    int update(FileManageInf fileManageInf);
    int deleteById(String fileId);
    int fileUpdate(@Param("fileId") String fileId, @Param("level") String level, @Param("type") String type);
    //查询附件大小
    double queryFileSize(@Param("dateFmat") String dateFmat, @Param("date") String date, @Param("unit") long unit);
    //查询附件大小(日期范围)
    List<Map> queryFileSizeRange(@Param("dateFmat") String dateFmat, @Param("dateBegin") String dateBegin, @Param("dateEnd") String dateEnd, @Param("unit") long unit);
    List<FileMonitoringVO> fileMonitoring(@Param("params") Map<String, Object> param);
}

