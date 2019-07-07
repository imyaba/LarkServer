package com.github.hollykunge.security.gate.fallback;

import com.github.hollykunge.security.api.vo.authority.FrontPermission;
import com.github.hollykunge.security.api.vo.authority.PermissionInfo;
import com.github.hollykunge.security.gate.feign.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author dd
 * @create 2018/3/7.
 */
@Service
@Slf4j
public class UserServiceFallback implements IUserService{
    @Override
    public List<PermissionInfo> getPermissionByUserId(@PathVariable("userId") String userId) {
        log.error("调用{}异常{}","getPermissionByUserId",userId);
        return null;
    }

    @Override
    public List<FrontPermission> getAllPermissionInfo() {
        log.error("调用{}异常","getPermissionByUsername");
        return null;
    }
}
