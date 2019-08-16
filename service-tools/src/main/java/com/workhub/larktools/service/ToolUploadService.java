package com.workhub.larktools.service;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.workhub.larktools.entity.ToolFile;
import com.workhub.larktools.vo.ToolFileVo;

/**
 * author:zhuqz
 * description:
 * date:2019/8/15 16:29
 **/
public interface ToolUploadService {
    int add(ToolFile params) ;
    int updateTreeNode(ToolFile params) ;
    int delete(ToolFile params) ;
    ListRestResponse<ToolFileVo> queryNodeFile(String treeId) ;
    ListRestResponse<ToolFileVo> queryAllFile(String userId) ;
}
