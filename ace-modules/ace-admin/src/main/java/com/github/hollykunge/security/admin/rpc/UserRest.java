package com.github.hollykunge.security.admin.rpc;

import com.ace.cache.annotation.Cache;
import com.alibaba.fastjson.JSON;
import com.github.hollykunge.security.admin.biz.OrgBiz;
import com.github.hollykunge.security.admin.biz.UserBiz;
import com.github.hollykunge.security.admin.constant.AdminCommonConstant;
import com.github.hollykunge.security.admin.entity.User;
import com.github.hollykunge.security.admin.rpc.service.PermissionService;
import com.github.hollykunge.security.api.vo.authority.FrontPermission;
import com.github.hollykunge.security.api.vo.authority.PermissionInfo;
import com.github.hollykunge.security.api.vo.user.UserInfo;
import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.vo.OrgUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * ${DESCRIPTION}
 *
 * @author 协同设计小组
 * @create 2017-06-21 8:15
 */
@RestController
@RequestMapping("api")
public class UserRest {


    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserBiz userBiz;

    @Autowired
    private OrgBiz orgBiz;

//    @Cache(key="permission")
    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    public @ResponseBody
    List<FrontPermission> getAllPermission(){
        return permissionService.getAllPermission();
    }

    @RequestMapping(value = "/user/un/{userId}/permissions", method = RequestMethod.GET)
    public @ResponseBody List<PermissionInfo> getPermissionByUserId(@PathVariable("userId") String userId){
        return permissionService.getPermissionMenuByUserId(userId);
    }

    @RequestMapping(value = "/user/validate", method = RequestMethod.POST)
    public @ResponseBody UserInfo validate(String pid,String password){
        return permissionService.validate(pid,password);
    }

    @RequestMapping(value = "/user/info", method = RequestMethod.POST)
    public @ResponseBody UserInfo info(String userId){
        User user = userBiz.getUserByUserId(userId);
        UserInfo info = new UserInfo();

        BeanUtils.copyProperties(user, info);
        info.setId(user.getId());
        return info;
    }

    @RequestMapping(value = "/user/userlist", method = RequestMethod.POST)
    public @ResponseBody List<UserInfo> userList(String userIdSet){
        List<UserInfo> userInfos = new ArrayList<UserInfo>();
        if (!StringUtils.isEmpty(userIdSet)) {
            String[] ids = userIdSet.split(",");
            for (String m : ids) {
                User user = userBiz.getUserByUserId(m);
                UserInfo info = new UserInfo();
                BeanUtils.copyProperties(user, info);
                info.setId(user.getId());
                userInfos.add(info);
            }
        }
        return userInfos;
    }

    @RequestMapping(value = "/user/all", method = RequestMethod.POST)
    public @ResponseBody List<User> all(){
        List<User> users = userBiz.getUsers();
//        List<User> infos = new ArrayList<User>();
//        BeanUtils.copyProperties(users, infos);
        return users;
    }

    /**
     * 组织用户树枝包含用户接口
     * @param parentTreeId 默认root
     * @return
     */
    @RequestMapping(value = "/orgUsers", method = RequestMethod.GET)
    public @ResponseBody String orgUsers(String parentTreeId) {
        if(StringUtils.isEmpty(parentTreeId)){
            parentTreeId = AdminCommonConstant.ROOT;
        }
        List<OrgUser> tree = orgBiz.getOrg(parentTreeId);
        return JSON.toJSONString(tree);
    }
}
