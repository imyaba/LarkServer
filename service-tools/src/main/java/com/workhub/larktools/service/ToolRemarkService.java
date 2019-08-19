package com.workhub.larktools.service;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.workhub.larktools.entity.ToolRemark;
import com.workhub.larktools.vo.ToolRemarkVo;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 10:58
 **/
public interface ToolRemarkService {
    int add(ToolRemark param);
    int delete(ToolRemark param);
    ListRestResponse<ToolRemarkVo> queryList(String fileId);
}
