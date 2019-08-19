package com.github.hollykunge.security.admin.rest;

import com.github.hollykunge.security.admin.feign.ZzMessageInfoFeign;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: zhhongyu
 * @description:
 * @since: Create in 15:12 2019/8/16
 */
@Controller
@RequestMapping("zzMessageInfo")
public class ChatMessageInfoController {
    @Autowired
    private ZzMessageInfoFeign zzMessageInfoFeign;
    /**
     * 消息监控 私聊
     *
     * @param params  pageSize条数,pageNum页码,timeEnd开始时间,timeBegin结束时间,sender发送人,receiver接收人,levels消息密级
     * @return 列表
     */
    @GetMapping("/queryAllMessagePrivate")
    @ResponseBody
    public TableResultResponse queryAllMessagePrivate(@RequestParam Map params) throws Exception{
        TableResultResponse tableResultResponse = zzMessageInfoFeign.queryAllMessagePrivate(params);
        return tableResultResponse;
    }
    /**
     * 消息监控 群聊
     *
     * @param params  pageSize条数,pageNum页码,timeEnd开始时间,timeBegin结束时间,sender发送人,receiver接收人,levels消息密级
     * @return 列表
     */
    @GetMapping("/queryAllMessageGroup")
    @ResponseBody
    public TableResultResponse queryAllMessageGroup(@RequestParam Map params) throws Exception{
        return zzMessageInfoFeign.queryAllMessageGroup(params);
    }
}
