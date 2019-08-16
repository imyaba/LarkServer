package com.workhub.larktools.dao;

import com.workhub.larktools.entity.ToolFile;
import com.workhub.larktools.vo.ToolFileVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ToolUploadDao extends Mapper<ToolFile> {
    int add(@Param("params") ToolFile param);
    int updateTreeNode(@Param("params") ToolFile param);
    int delete(@Param("params") ToolFile param);
    List<ToolFileVo> queryNodeFile(@Param("treeId") String treeId);
    List<ToolFileVo> queryAllFile(@Param("orgCode") String orgCode);
}
