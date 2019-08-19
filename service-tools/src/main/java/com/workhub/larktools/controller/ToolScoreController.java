package com.workhub.larktools.controller;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.workhub.larktools.entity.ToolScore;
import com.workhub.larktools.service.ToolScoreService;
import com.workhub.larktools.tool.CommonUtil;
import com.workhub.larktools.vo.ToolScoreVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Map;

/**
 * author:zhuqz
 * description:
 * date:2019/8/19 14:21
 **/
@RestController
@RequestMapping("/score")
public class ToolScoreController {
    @Resource
    ToolScoreService toolScoreService;
    @Resource
    private HttpServletRequest httpServletRequest;
    /**
    * @MethodName: add
     * @Description:
     * @Param: [map]score 得分;fileId下载附件对应的id
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
        ToolScore toolScore = new ToolScore();
        toolScore.setId(UUIDUtils.generateShortUuid());
        toolScore.setCreator(userId);
        toolScore.setCreatorName(userName);
        toolScore.setToolId(CommonUtil.nulToEmptyString(map.get("fileId")));
        String scoreStr = CommonUtil.nulToEmptyString(map.get("score"));
        Double score = Double.valueOf(scoreStr.equals("")?"0":scoreStr);
        toolScore.setScore(score);
        int i = toolScoreService.add(toolScore);
        return  res;
    }
    //查询得分
    @GetMapping("/queryList")
    public ListRestResponse<ToolScoreVo> queryList(@RequestParam String fileId) throws Exception{
        return this.toolScoreService.queryList(fileId);
    }
}
