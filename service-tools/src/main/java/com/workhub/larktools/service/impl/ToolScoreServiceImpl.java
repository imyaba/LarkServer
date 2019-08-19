package com.workhub.larktools.service.impl;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.workhub.larktools.dao.ToolScoreDao;
import com.workhub.larktools.entity.ToolScore;
import com.workhub.larktools.service.ToolScoreService;
import com.workhub.larktools.vo.ToolScoreVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 14:17
 **/
@Service("toolScoreService")
public class ToolScoreServiceImpl implements ToolScoreService {
    @Resource
    private ToolScoreDao toolScoreDao;
    public int add(ToolScore param){
        return toolScoreDao.add(param);
    }
    public  ListRestResponse<ToolScoreVo> queryList(String fileId){
        List<ToolScoreVo> dataList = this.toolScoreDao.queryList(fileId);
        return   new ListRestResponse("200",dataList.size(),dataList);
    }
}
