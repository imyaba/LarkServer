package com.github.hollykunge.security.admin.rest;

import com.github.hollykunge.security.common.util.Query;
import com.github.pagehelper.PageHelper;
import com.github.hollykunge.security.admin.biz.GateLogBiz;
import com.github.hollykunge.security.admin.entity.GateLog;
import com.github.hollykunge.security.admin.entity.User;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.common.rest.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author 协同设计小组
 * @create 2017-07-01 20:32
 */
@Controller
@RequestMapping("gateLog")
public class GateLogController extends BaseController<GateLogBiz,GateLog> {

    private final static String ANQUAN = "2";
    private final static String RIZHI = "3";

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    @Override
    public TableResultResponse<GateLog> page(@RequestParam Map<String, Object> params) {
        String pId = request.getHeader("userId");

        if (pId == ANQUAN){
            params.put("crtUser", pId);
            Query query = new Query(params);
            return baseBiz.selectByQueryEq(query);
        }else if (pId == RIZHI){
            params.put("crtUser", pId);
            Query query = new Query(params);
            return baseBiz.selectByQueryNotEq(query);
        }else {
            Query query = new Query(params);
            return baseBiz.selectByQuery(query);
        }

    }
}
