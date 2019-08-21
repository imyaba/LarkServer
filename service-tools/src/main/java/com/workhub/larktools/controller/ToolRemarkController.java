package com.workhub.larktools.controller;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.workhub.larktools.entity.ToolRemark;
import com.workhub.larktools.service.ToolRemarkService;
import com.workhub.larktools.tool.CommonUtil;
import com.workhub.larktools.vo.ToolRemarkVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Map;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 11:05
 **/
@RestController
@RequestMapping("/remark")
public class ToolRemarkController {
    private static Logger log = LoggerFactory.getLogger(ToolRemarkController.class);
    @Resource
    private ToolRemarkService toolRemarkService;
    @Resource
    private HttpServletRequest httpServletRequest;

    /**
    * @MethodName: add
     * @Description:
     * @Param: [map] remark 评论内容，fileId 评论的附件的id
     * @Return: com.github.hollykunge.security.common.msg.ObjectRestResponse
     * @Author: zhuqz
     * @Date: 2019/8/19
    **/
    @PostMapping("/add")
    public ObjectRestResponse add(@RequestParam Map map) throws  Exception{
        ObjectRestResponse res = new ObjectRestResponse();
        res.rel(true);
        res.msg("200");
        String userId = CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        String userName = URLDecoder.decode(CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userName")),"UTF-8");
        ToolRemark remark = new ToolRemark();
        remark.setId(UUIDUtils.generateShortUuid());
        remark.setCreator(userId);
        remark.setCreatorName(userName);
        remark.setToolId(CommonUtil.nulToEmptyString(map.get("fileId")));
        remark.setRemark(CommonUtil.nulToEmptyString(map.get("remark")));
        int i = this.toolRemarkService.add(remark);
        return  res;
    }

    /**
    * @MethodName: delete
     * @Description:
     * @Param: [id] 主键id
     * @Return: com.github.hollykunge.security.common.msg.ObjectRestResponse
     * @Author: zhuqz
     * @Date: 2019/8/19
    **/
    @PostMapping("/delete")
    public ObjectRestResponse delete(@RequestParam String id) throws Exception{
        ObjectRestResponse res = new ObjectRestResponse();
        res.rel(true);
        res.msg("200");
        String userId = CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        String userName = URLDecoder.decode(CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userName")),"UTF-8");
        ToolRemark remark = new ToolRemark();
        remark.setId(id);
        remark.setUpdator(userId);
        remark.setUpdatorName(userName);
        int i = this.toolRemarkService.delete(remark);
        return  res;
    }
    //查询评论
    @GetMapping("/queryList")
    public ListRestResponse<ToolRemarkVo> queryList(@RequestParam String fileId) throws Exception{
        return this.toolRemarkService.queryList(fileId);
    }
}
