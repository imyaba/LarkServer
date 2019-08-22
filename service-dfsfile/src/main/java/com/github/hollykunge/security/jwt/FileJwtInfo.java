package com.github.hollykunge.security.jwt;

import com.github.hollykunge.security.auth.client.config.UserAuthConfig;
import com.github.hollykunge.security.auth.client.jwt.UserAuthUtil;
import com.github.hollykunge.security.auth.common.util.jwt.IJWTInfo;
import com.github.hollykunge.security.common.util.ClientUtil;
import com.github.hollykunge.security.vo.JwtInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhhongyu
 * @since 2019-08-22
 * @deprecation token解析生成文件服务实体组件
 */
@Component
public class FileJwtInfo {

    @Autowired
    private UserAuthConfig userAuthConfig;
    @Autowired
    private UserAuthUtil userAuthUtil;

    public JwtInfoVO getJwtInfoVO(HttpServletRequest request) throws Exception{
        JwtInfoVO jwtInfoVO = null;
        IJWTInfo infoFromToken = getJwtInfo(request);
        if (infoFromToken != null) {
            jwtInfoVO = new JwtInfoVO();
            String host = StringUtils.isEmpty(request.getHeader("clientIp")) ?
                    request.getHeader("clientIp") : ClientUtil.getClientIp(request);
            jwtInfoVO.setCrtHost(host);
            jwtInfoVO.setUpdHost(host);
            jwtInfoVO.setCrtName(infoFromToken.getName());
            jwtInfoVO.setCrtUser(infoFromToken.getUniqueName());
            jwtInfoVO.setUpdName(infoFromToken.getName());
            jwtInfoVO.setUpdUser(infoFromToken.getUniqueName());
        }
        return jwtInfoVO;
    }

    private IJWTInfo getJwtInfo(HttpServletRequest request) throws Exception{
        IJWTInfo infoFromToken = null;
        try {
            String authToken = request.getHeader(userAuthConfig.getTokenHeader());
            if (StringUtils.isEmpty(authToken)) {
                infoFromToken = null;
            } else {
                infoFromToken = userAuthUtil.getInfoFromToken(authToken);
            }
        } catch (Exception e) {
            infoFromToken = null;
            throw e;
        }
        return infoFromToken;
    }

}
