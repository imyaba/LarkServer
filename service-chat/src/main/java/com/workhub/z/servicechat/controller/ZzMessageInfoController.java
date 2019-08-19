package com.workhub.z.servicechat.controller;

import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.workhub.z.servicechat.entity.ZzMessageInfo;
import com.workhub.z.servicechat.service.ZzMessageInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 消息存储(ZzMessageInfo)表控制层
 *
 * @author makejava
 * @since 2019-06-23 13:50:41
 */
@RestController
@RequestMapping("zzMessageInfo")
public class ZzMessageInfoController {
    /**
     * 服务对象
     */
    @Resource
    private ZzMessageInfoService zzMessageInfoService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public ZzMessageInfo selectOne(String id) {
        return this.zzMessageInfoService.queryById(id);
    }

    /**
     * 消息监控 私聊(改为提供接口，使用admin服务调用)
     *
     * @param params  pageSize条数,pageNum页码,timeEnd开始时间,timeBegin结束时间,sender发送人,receiver接收人,levels消息密级
     * @return 列表
     */
    @PostMapping("queryAllMessagePrivate")
    public TableResultResponse queryAllMessagePrivate(@RequestBody Map params) throws Exception{
        return this.zzMessageInfoService.queryAllMessagePrivate(params);
    }
    /**
     * 消息监控 群聊(改为提供接口，使用admin服务调用)
     *
     * @param params  pageSize条数,pageNum页码,timeEnd开始时间,timeBegin结束时间,sender发送人,receiver接收人,levels消息密级
     * @return 列表
     */
    @PostMapping("queryAllMessageGroup")
    public TableResultResponse queryAllMessageGroup(@RequestBody Map params) throws Exception{
        return this.zzMessageInfoService.queryAllMessageGroup(params);
    }
}