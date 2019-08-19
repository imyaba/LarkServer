package com.workhub.larktools.dao;


import com.workhub.larktools.entity.ToolScore;
import com.workhub.larktools.vo.ToolScoreVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 14:10
 **/
public interface ToolScoreDao extends Mapper<ToolScore> {
    int add(@Param("params") ToolScore param);
    List<ToolScoreVo> queryList(@Param("fileId") String fileId);
}
