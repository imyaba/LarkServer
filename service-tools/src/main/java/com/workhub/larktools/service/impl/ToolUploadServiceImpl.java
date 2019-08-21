package com.workhub.larktools.service.impl;

import com.github.hollykunge.security.api.vo.user.UserInfo;
import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.workhub.larktools.dao.ToolOrgDao;
import com.workhub.larktools.dao.ToolUploadDao;
import com.workhub.larktools.entity.ToolFile;
import com.workhub.larktools.feign.IUserService;
import com.workhub.larktools.service.ToolUploadService;
import com.workhub.larktools.vo.ToolFileVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * author:zhuqz
 * description:
 * date:2019/8/15 16:30
 **/
@Service("toolUploadService")
public class ToolUploadServiceImpl implements ToolUploadService {
    @Resource
    private ToolUploadDao toolUploadDao;
    @Resource
    private ToolOrgDao toolOrgDao;
    @Resource
    private IUserService iUserService;

    public int add(ToolFile params) {
        return this.toolUploadDao.add(params);
    }
    public int updateTreeNode(ToolFile params) {
        return this.toolUploadDao.updateTreeNode(params);
    }
    public int delete(ToolFile params) {
        return this.toolUploadDao.delete(params);
    }
    public ListRestResponse<ToolFileVo> queryNodeFile(String treeId) {
        List<ToolFileVo> dataList = this.toolUploadDao.queryNodeFile(treeId);
        return   new ListRestResponse("200",dataList.size(),dataList);
    }
    public ListRestResponse<ToolFileVo> queryAllFile(String userId) {
        UserInfo userInfo = this.iUserService.info(userId);
        String orgCode = userInfo.getOrgCode();
        List<ToolFileVo> dataList = this.toolUploadDao.queryAllFile(orgCode);
        return   new ListRestResponse("200",dataList.size(),dataList);
    }
}
