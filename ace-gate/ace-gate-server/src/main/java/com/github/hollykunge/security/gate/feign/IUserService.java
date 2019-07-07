package com.github.hollykunge.security.gate.feign;

import com.github.hollykunge.security.api.vo.authority.FrontPermission;
import com.github.hollykunge.security.api.vo.authority.PermissionInfo;
import com.github.hollykunge.security.gate.fallback.UserServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


/**
 * ${DESCRIPTION}
 *
 * @author 协同设计小组
 * @create 2017-06-21 8:11
 */
@FeignClient(value = "ace-admin",fallback = UserServiceFallback.class)
public interface IUserService {
  @RequestMapping(value="/api/user/un/{userId}/permissions",method = RequestMethod.GET)
  List<PermissionInfo> getPermissionByUserId(@PathVariable("userId") String userId);
  @RequestMapping(value="/api/permissions",method = RequestMethod.GET)
  List<FrontPermission> getAllPermissionInfo();
}
