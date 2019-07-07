package com.github.hollykunge.security.gate.filter;

import com.alibaba.fastjson.JSON;
import com.github.hollykunge.security.api.vo.authority.FrontPermission;
import com.github.hollykunge.security.api.vo.authority.PermissionInfo;
import com.github.hollykunge.security.api.vo.log.LogInfo;
import com.github.hollykunge.security.auth.client.config.ServiceAuthConfig;
import com.github.hollykunge.security.auth.client.config.UserAuthConfig;
import com.github.hollykunge.security.auth.client.jwt.ServiceAuthUtil;
import com.github.hollykunge.security.auth.client.jwt.UserAuthUtil;
import com.github.hollykunge.security.auth.common.util.jwt.IJWTInfo;
import com.github.hollykunge.security.common.constant.CommonConstants;
import com.github.hollykunge.security.common.context.BaseContextHandler;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.msg.auth.TokenErrorResponse;
import com.github.hollykunge.security.common.msg.auth.TokenForbiddenResponse;
import com.github.hollykunge.security.common.util.ClientUtil;
import com.github.hollykunge.security.gate.feign.ILogService;
import com.github.hollykunge.security.gate.feign.IUserService;
import com.github.hollykunge.security.gate.utils.DBLog;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 网关核心权限拦截类
 *
 * @author 协同设计小组
 * @create 2017-06-23 8:25
 */
@Component
@Slf4j
public class AdminAccessFilter extends ZuulFilter {
    @Autowired
    @Lazy
    private IUserService userService;
    @Autowired
    @Lazy
    private ILogService logService;

    @Value("${gate.ignore.startWith}")
    private String startWith;

    @Value("${zuul.prefix}")
    private String zuulPrefix;
    @Autowired
    private UserAuthUtil userAuthUtil;

    @Autowired
    private ServiceAuthConfig serviceAuthConfig;

    @Autowired
    private UserAuthConfig userAuthConfig;

    @Autowired
    private ServiceAuthUtil serviceAuthUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        final String requestUri = request.getRequestURI().substring(zuulPrefix.length());
        BaseContextHandler.setToken(null);

        String dnname = request.getHeader(CommonConstants.PERSON_ID_ARG);
        String clientIp = request.getHeader(CommonConstants.CLIENT_IP);

        //走院网关时候需要走下面的逻辑，院网关很傻逼
        if(StringUtils.isNotEmpty(dnname)){
            try {
                dnname = new String (dnname.getBytes(CommonConstants.PERSON_CHAR_SET));
            } catch (UnsupportedEncodingException e) {
                throw new BaseException("身份信息编码转化错误...");
            }
            String[] userObjects = dnname.trim().split(",", 0);
            String pId = null;
            for (String val:
                    userObjects) {
                val = val.trim();
                if(val.indexOf("t=")>-1||val.indexOf("T=")>-1){
                    pId = val.substring(2,val.length());
                }
            }
            //将dnname设置为身份证信息
            ctx.addZuulRequestHeader(CommonConstants.PERSON_ID_ARG, pId);
            ctx.addZuulRequestHeader(CommonConstants.CLIENT_IP, clientIp);
        }

        // 不进行拦截的地址
        if (isStartWith(requestUri)) {
            return null;
        }
        IJWTInfo user = null;
        try {
            user = getJWTUser(request, ctx);
        } catch (Exception e) {
            setFailedRequest(JSON.toJSONString(new TokenErrorResponse(e.getMessage())), 200);
            return null;
        }

        //根据用户id获取资源列表，包括菜单和菜单功能
        List<PermissionInfo> permissionInfos = userService.getPermissionByUserId(user.getId());
        if(permissionInfos.size()>0){
            checkUserPermission(requestUri, permissionInfos, ctx, user);
        }
        // 申请客户端密钥头，加到header里传递到下方服务
        ctx.addZuulRequestHeader(serviceAuthConfig.getTokenHeader(), serviceAuthUtil.getClientToken());
        return null;
    }

    /**
     * 获取目标权限资源
     * 请求资源和权限列表匹配，并且与资源方法相同
     * @param requestUri
     * @param method
     * @param serviceInfo
     * @return
     */
    private Stream<FrontPermission> getPermissionIfs(final String requestUri, final String method, List<FrontPermission> serviceInfo) {
        return serviceInfo.stream().filter(new Predicate<FrontPermission>() {
            @Override
            public boolean test(FrontPermission permissionInfo) {
                String uriTemp = permissionInfo.getUri();
                String uri = uriTemp.replaceAll("\\{\\*\\}", "[a-zA-Z\\\\d]+");
                String regEx = "^" + uri + "$";
                return Pattern.compile(regEx).matcher(requestUri).find() && method.equals(permissionInfo.getMethods());
            }
        });
    }

    /**
     * 在上下文中设置当前用户信息和操作日志
     */
    private void setCurrentUserInfoAndLog(RequestContext ctx, IJWTInfo user, PermissionInfo pm) {
        String host = ClientUtil.getClientIp(ctx.getRequest());
        String pid = ClientUtil.getPid(ctx.getRequest(), user.getUniqueName());
        ctx.addZuulRequestHeader("userId", pid);
        ctx.addZuulRequestHeader("userName", URLEncoder.encode(user.getName()));
        ctx.addZuulRequestHeader("userHost", host);
        LogInfo logInfo = new LogInfo(pm.getTitle(), ctx.getRequest().getMethod(), pm.getUri(), new Date(), pid, user.getName(), host);
        DBLog.getInstance().setLogService(logService).offerQueue(logInfo);
    }

    /**
     * 返回token中的用户信息
     *
     * @param request
     * @param ctx
     * @return
     */
    private IJWTInfo getJWTUser(HttpServletRequest request, RequestContext ctx) throws Exception {
        String authToken = request.getHeader(userAuthConfig.getTokenHeader());
        if (StringUtils.isBlank(authToken)) {
            authToken = request.getParameter("token");
        }
        ctx.addZuulRequestHeader(userAuthConfig.getTokenHeader(), authToken);
        BaseContextHandler.setToken(authToken);
        return userAuthUtil.getInfoFromToken(authToken);
    }

    /**
     * URI是否以什么打头
     *
     * @param requestUri
     * @return
     */
    private boolean isStartWith(String requestUri) {
        boolean flag = false;
        for (String s : startWith.split(",")) {
            if (requestUri.startsWith(s)) {
                return true;
            }
        }
        return flag;
    }

    /**
     * 网关抛异常
     *
     * @param body
     * @param code
     */
    private void setFailedRequest(String body, int code) {
        log.debug("Reporting error ({}): {}", code, body);
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody(body);
            ctx.getResponse().setContentType("text/json;charset=UTF-8");
            ctx.setSendZuulResponse(false);
        }
    }

    /**
     * 优化查询该请求资源是否在用户所拥有的权限中
     * @param ctx
     * @param user
     */
    private void checkUserPermission(String requestUri, List<PermissionInfo> permissionInfos, RequestContext ctx, IJWTInfo user) {
        if(StringUtils.isEmpty(requestUri)){
            throw new BaseException("requestUri 参数异常...");
        }
        permissionInfos =  permissionInfos.parallelStream()
                .filter(new Predicate<PermissionInfo>() {
                    @Override
                    public boolean test(PermissionInfo permissionInfo) {
                        if(StringUtils.isEmpty(permissionInfo.getUri())){
                            return false;
                        }
                        return requestUri.contains(permissionInfo.getUri());
                    }
                }).collect(Collectors.toList());

        if(permissionInfos.size()==0){
            setFailedRequest(JSON.toJSONString(new TokenForbiddenResponse("Token Forbidden!request url no permission...")), 200);
        }
//        boolean anyMatch =
//                permissionInfos.parallelStream()
//                .anyMatch(new Predicate<PermissionInfo>() {
//                    @Override
//                    public boolean test(PermissionInfo permissionInfo) {
//                        return permissionInfo.getActionEntitySetList().stream().anyMatch(actionEntitySet ->
//                                ctx.getRequest().getMethod().equals(actionEntitySet.getMethod()));
//                    }
//                });
            //该用户有访问路径权限
            setCurrentUserInfoAndLog(ctx, user, permissionInfos.get(0));

    }

}
