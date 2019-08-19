package com.workhub.larktools.service.impl;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.workhub.larktools.dao.ToolRemarkDao;
import com.workhub.larktools.entity.ToolRemark;
import com.workhub.larktools.service.ToolRemarkService;
import com.workhub.larktools.vo.ToolRemarkVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 10:58
 **/
@Service("toolRemarkService")
public class ToolRemarkServiceImpl implements ToolRemarkService {
    @Resource
    private ToolRemarkDao toolRemarkDao;
    public int add(ToolRemark param){
        return toolRemarkDao.add(param);
    }
    public int delete(ToolRemark param){
        return toolRemarkDao.delete(param);
    }
    public  ListRestResponse<ToolRemarkVo> queryList(String fileId){
        List<ToolRemarkVo> dataList = this.toolRemarkDao.queryList(fileId);
        return   new ListRestResponse("200",dataList.size(),dataList);
    }


}
