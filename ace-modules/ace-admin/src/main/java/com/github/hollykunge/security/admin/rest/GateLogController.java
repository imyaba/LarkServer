package com.github.hollykunge.security.admin.rest;

import com.esotericsoftware.minlog.Log;
import com.github.hollykunge.security.common.util.ExportExcelUtils;
import com.github.hollykunge.security.common.util.Query;
import com.github.pagehelper.PageHelper;
import com.github.hollykunge.security.admin.biz.GateLogBiz;
import com.github.hollykunge.security.admin.entity.GateLog;
import com.github.hollykunge.security.admin.entity.User;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.common.rest.BaseController;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
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
        String userName = null;
        String pid = null;
//        try {
//            if (request.getHeader("userName").isEmpty()||request.getHeader("userName").equals("")){
//                userName = URLDecoder.decode(request.getHeader("userName"), "UTF-8");
//            }
//            if (request.getHeader("dnname").isEmpty()||request.getHeader("dnname").equals("")){
                pid = request.getHeader("dnname");
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        List<String> userlist = new ArrayList();
//        userlist.add(SYSTEM_USER);
//        userlist.add(LOG_USER);
        List<String> userPidlist = new ArrayList();
        userPidlist.add("2");
        userPidlist.add("3");
//        Log.debug(userName+SYSTEM_USER+LOG_USER+SECURITY_USER);
//        Log.debug("用户姓名" +request.getHeader("userName")+"用户身份证"+request.getHeader("dnname"));
        System.out.println("用户身份证"+pid);
//        if (userName.equals(LOG_USER))
//        {
//            params.put("crtName",userlist);
//            return baseBiz.selectByQueryM( new Query(params),"log");
//        }
//        else if(userName.equals(SECURITY_USER)){
//            params.put("crtName",userlist);
//            return baseBiz.selectByQueryM( new Query(params),"Security");
//        }
        if (pid.equals("3"))
        {
            params.put("pid",userPidlist);
            return baseBiz.selectByQueryM( new Query(params),"log");
        }
        else if(pid.equals("4")){
            params.put("pid",userPidlist);
            return baseBiz.selectByQueryM( new Query(params),"Security");
        }
//        switch (userName){
//            case LOG_USER:
//                params.put("crtName",userlist);
//                return baseBiz.selectByQueryM( new Query(params),"log");
//            case SECURITY_USER:
//                params.put("crtName",userlist);
//                return baseBiz.selectByQueryM( new Query(params),"Security");
//            default:
//                return baseBiz.selectByQuery( new Query(params));
//        }
        return null;

    }
    @GetMapping("/export")
    public void  download(HttpServletResponse response) throws Exception{
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("日志文件.xls", "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        List<GateLog> gateLogs = baseBiz.selectListAll();
        String columName = "菜单,功能,主机ip,是否成功,访问路径,访问时间,访问人员姓名,身份证号";
        String columCode = "menu,opt,crtHost,isSuccess,uri,crtTime,crtName,pid";
        String sheetName = "日志详情";
        byte[] export = ExportExcelUtils.export(gateLogs, columName, columCode, sheetName, outputStream);
        IOUtils.write(export, outputStream);
    }
}
