package com.github.hollykunge.security.admin.rest;

import com.github.hollykunge.security.common.util.Query;
import com.github.pagehelper.PageHelper;
import com.github.hollykunge.security.admin.biz.GateLogBiz;
import com.github.hollykunge.security.admin.entity.GateLog;
import com.github.hollykunge.security.admin.entity.User;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.common.rest.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.github.hollykunge.security.admin.constant.AdminCommonConstant.*;

/**
 * ${DESCRIPTION}
 *
 * @author 协同设计小组
 * @create 2017-07-01 20:32
 */
@Controller
@RequestMapping("gateLog")
public class GateLogController extends BaseController<GateLogBiz,GateLog> {
    @RequestMapping(value = "/page",method = RequestMethod.GET)
    @ResponseBody
    @Override
    public TableResultResponse<GateLog> page(@RequestParam Map<String, Object> params){
        Example example = new Example(GateLog.class);
        String userId = null;
        try {
            userId = URLDecoder.decode(request.getHeader("userName"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<String> userlist = new ArrayList();
        userlist.add(SYSTEM_USER);
        userlist.add(LOG_USER);
        switch (userId){
            case LOG_USER:
                params.put("crtName",userlist);
                return baseBiz.selectByQueryM( new Query(params),"log");
            case SECURITY_USER:
                params.put("crtName",userlist);
                return baseBiz.selectByQueryM( new Query(params),"Security");
            default:
                return baseBiz.selectByQuery( new Query(params));
        }

    }
}
