package com.workhub.larktools.controller;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.workhub.larktools.service.ToolOrgService;
import com.workhub.larktools.tool.CommonUtil;
import com.workhub.larktools.vo.ToolTreeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Map;

/**
 * author:zhuqz
 * description:工具上传组织管理
 * date:2019/8/13 11:27
 **/
@RestController
@RequestMapping("/toolOrg")
public class ToolOrgController {
    private static Logger log = LoggerFactory.getLogger(ToolOrgController.class);
    @Resource
    private ToolOrgService toolOrgService;
    @Resource
    private HttpServletRequest httpServletRequest;
    /**
    * @MethodName: add
     * @Description: 增加组织节点
     * @Param: [params] 组织名称 ：name ，父节点pid,如果为空认为是根节点,order 排序
     * @Return: com.github.hollykunge.security.common.msg.ObjectRestResponse
     * @Author: admin
     * @Date: 2019/8/13
    **/
    @PostMapping("/add")
    public ObjectRestResponse add(@RequestParam Map params) throws Exception{
        ObjectRestResponse res = new ObjectRestResponse();
        res.msg("200");
        res.rel(true);
        String userId = CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        String userName = URLDecoder.decode(CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userName")),"UTF-8");
        params.put("userId",userId);
        params.put("id", UUIDUtils.generateShortUuid());
        int i = toolOrgService.add(params);
        return res;
    }

    /**
    * @MethodName: update
     * @Description: 更新节点的信息
     * @Param: [params]id节点主键，name新的名称，order排序
     * @Return: com.github.hollykunge.security.common.msg.ObjectRestResponse
     * @Author: zhuqz
     * @Date: 2019/8/14
    **/
    @PostMapping("/update")
    public ObjectRestResponse update(@RequestParam Map params) throws Exception{
        ObjectRestResponse res = new ObjectRestResponse();
        res.msg("200");
        res.rel(true);
        String userId = CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        String userName = URLDecoder.decode(CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userName")),"UTF-8");
        params.put("userId",userId);
        int i = toolOrgService.update(params);
        return res;
    }
    /**
     * @MethodName: updateTreeNodeParent
     * @Description: 更新节点所属树枝
     * @Param: [params]id节点主键，pid父节点，order排序
     * @Return: com.github.hollykunge.security.common.msg.ObjectRestResponse
     * @Author: zhuqz
     * @Date: 2019/8/14
     **/
    @PostMapping("/updateTreeNodeParent")
    public ObjectRestResponse updateTreeNodeParent(@RequestParam Map params) throws Exception{
        ObjectRestResponse res = new ObjectRestResponse();
        res.msg("200");
        res.rel(true);
        String userId = CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        String userName = URLDecoder.decode(CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userName")),"UTF-8");
        params.put("userId",userId);
        int i = toolOrgService.updateTreeNodeParent(params);
        return res;
    }
    /**
     * @MethodName: updateTreeNodeParent
     * @Description: 删除节点，连同删除子节点，如果节点是根节点，那么所属的组织结构对应关系也删除（全是逻辑删除）
     * @Param: [params]id当前节点id
     * @Return: com.github.hollykunge.security.common.msg.ObjectRestResponse
     * @Author: zhuqz
     * @Date: 2019/8/14
     **/
    @PostMapping("/delete")
    public ObjectRestResponse delete(@RequestParam Map params) throws Exception{
        ObjectRestResponse res = new ObjectRestResponse();
        res.msg("200");
        res.rel(true);
        String userId = CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        String userName = URLDecoder.decode(CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userName")),"UTF-8");
        params.put("userId",userId);
        int i = toolOrgService.delete(params);
        return res;
    }
    /**
    * @MethodName: queryCurrentUserTree
     * @Description: 获取当前登录用户端树结构
     * @Param: [params]
     * @Return: com.github.hollykunge.security.common.msg.ListRestResponse<com.workhub.larktools.vo.ToolTreeVo>
     * @Author: admin
     * @Date: 2019/8/15
    **/
    @GetMapping("/queryCurrentUserTree")
    public ListRestResponse<ToolTreeVo> queryCurrentUserTree() throws Exception{
        String userId = CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        return  this.toolOrgService.queryCurrentUserTree(userId);
    }
    /**
     * @MethodName: query
     * @Description: 查询整个树结构
     * @Param: [params]id树节点id
     * @Return: 整个树结构
     * @Author: zhuqz
     * @Date: 2019/8/14
     **/
    @PostMapping("/query")
    public ListRestResponse<ToolTreeVo> query(@RequestParam Map params) throws Exception{
        return  this.toolOrgService.query(params);
    }
}
