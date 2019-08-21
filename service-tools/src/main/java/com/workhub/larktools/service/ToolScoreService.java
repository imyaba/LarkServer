package com.workhub.larktools.service;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.workhub.larktools.entity.ToolScore;
import com.workhub.larktools.vo.ToolScoreVo;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 14:16
 **/
public interface ToolScoreService {
    int add(ToolScore param);
    ListRestResponse<ToolScoreVo> queryList(String fileId);
}
