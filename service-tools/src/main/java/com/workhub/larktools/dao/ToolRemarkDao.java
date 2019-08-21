package com.workhub.larktools.dao;

import com.workhub.larktools.entity.ToolRemark;
import com.workhub.larktools.vo.ToolRemarkVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 10:22
 **/
public interface ToolRemarkDao  extends Mapper<ToolRemark> {
    int add(@Param("params") ToolRemark param);
    int delete(@Param("params") ToolRemark param);
    List<ToolRemarkVo> queryList(@Param("fileId") String fileId);
}
