package com.github.hollykunge.security.admin.rpc.service;

import com.github.hollykunge.security.admin.biz.ElementBiz;
import com.github.hollykunge.security.admin.biz.MenuBiz;
import com.github.hollykunge.security.admin.biz.RoleBiz;
import com.github.hollykunge.security.admin.biz.UserBiz;
import com.github.hollykunge.security.admin.config.mq.ProduceSenderConfig;
import com.github.hollykunge.security.admin.entity.Element;
import com.github.hollykunge.security.admin.entity.Menu;
import com.github.hollykunge.security.admin.entity.Role;
import com.github.hollykunge.security.admin.entity.User;
import com.github.hollykunge.security.admin.vo.*;
import com.github.hollykunge.security.api.vo.authority.ActionEntitySet;
import com.github.hollykunge.security.api.vo.authority.FrontPermission;
import com.github.hollykunge.security.api.vo.authority.PermissionInfo;
import com.github.hollykunge.security.api.vo.user.UserInfo;
import com.github.hollykunge.security.auth.client.jwt.UserAuthUtil;

import com.github.hollykunge.security.common.constant.UserConstant;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.util.StringHelper;

import com.github.hollykunge.security.common.util.UUIDUtils;
import com.github.hollykunge.security.common.vo.mq.HotMapVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 初始化用户权限服务
 * @author 协同设计小组
 * @date 2017/9/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionService {
    @Autowired
    private RoleBiz roleBiz;
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private MenuBiz menuBiz;
    @Autowired
    private ElementBiz elementBiz;
    @Autowired
    private UserAuthUtil userAuthUtil;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(UserConstant.PW_ENCORDER_SALT);

    @Autowired
    private ProduceSenderConfig produceSenderConfig;

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    public UserInfo getUserByUserId(String userId) {
        UserInfo info = new UserInfo();
        User user = userBiz.getUserByUserId(userId);
        BeanUtils.copyProperties(user, info);
        info.setId(user.getId());
        return info;
    }

    /**
     * 验证用户
     * @param userPid
     * @param password
     * @return
     */
    public UserInfo validate(String userPid,String password){
        UserInfo info = new UserInfo();
        User user = userBiz.getUserByUserPid(userPid);
        if(user==null){
            throw new BaseException("没有该用户...");
        }
        if (!encoder.matches(password, user.getPassword())) {
            throw new BaseException("密码错误...");
        }
        BeanUtils.copyProperties(user, info);
        info.setId(user.getId());
        return info;
    }

    /**
     * 获取所有的资源权限，包括菜单和按钮
     * @return
     */
    public List<FrontPermission> getAllPermission() {
        List<Menu> menus = menuBiz.selectListAll();
        List<FrontPermission> result = new ArrayList<FrontPermission>();
        menu2permission(menus, result);

        List<Element> elements = elementBiz.selectListAll();
        element2permission(result, elements);
        return result;
    }

    /**
     * 菜单权限
     * @param menus
     * @param result
     */
    private void menu2permission(List<Menu> menus, List<FrontPermission> result) {
        FrontPermission info;
        for (Menu menu : menus) {
            info = new FrontPermission();
            info.setMenuId(menu.getId());
            info.setTitle(menu.getTitle());
            info.setUri(menu.getUri());
            info.setPermissionId(menu.getPermissionId());
            result.add(info);
        }
    }

    /**
     * 根据userId获取角色所属菜单和功能,提供给前端获取userinfo使用
     * @param userId
     * @return
     */
    private List<FrontPermission> getPermissionByUserId(String userId) {
        List<Role> roleByUserId = roleBiz.getRoleByUserId(userId);
        if(roleByUserId.size()==0){
            throw new BaseException("该人员没有访问权限...");
        }
        List<FrontPermission> authorityMenu = roleBiz.frontAuthorityMenu(roleByUserId.get(0).getId());
        return authorityMenu;
    }

    /**
     * 根据userId获取角色所属菜单，鉴权使用
     * @param userId
     * @return
     */
    public List<PermissionInfo> getPermissionMenuByUserId(String userId) {
        List<Role> roleByUserId = roleBiz.getRoleByUserId(userId);
        if(roleByUserId.size()==0){
            throw new BaseException("该人员没有访问权限...");
        }
        List<PermissionInfo> authorityMenu = roleBiz.getMenusByRoleId(roleByUserId.get(0).getId());
        return authorityMenu;
    }

    /**
     * 元素权限
     * @param result
     * @param elements
     */
    private void element2permission(List<FrontPermission> result, List<Element> elements) {

        for(FrontPermission frontPermission : result){

            List<Element> tempElement = elements.stream()
                    .filter((Element e) -> frontPermission.getMenuId().contains(e.getMenuId()))
                    .collect(Collectors.toList());
            ActionEntitySet info;
            List<ActionEntitySet> actionEntitySets = new ArrayList<>();
            for (Element element : tempElement) {


                info = new ActionEntitySet();

                info.setDefaultCheck(true);
                info.setDescription(element.getDescription());
                info.setMethod(element.getMethod());

                actionEntitySets.add(info);
            }
            frontPermission.setActionEntitySetList(actionEntitySets);
            frontPermission.setMethods(StringHelper.getObjectValue(actionEntitySets));
        }

    }

    /**
     * 获取前端用户信息
     * @param token
     * @return
     * @throws Exception
     */
    public FrontUser getUserInfo(String token) throws Exception {
        String userId = userAuthUtil.getInfoFromToken(token).getId();
        if (userId == null) {
            return null;
        }
        User user = userBiz.getUserByUserId(userId);
//        UserInfo user = this.getUserByUserId(userId);
        FrontUser frontUser = new FrontUser();
        BeanUtils.copyProperties(user, frontUser);
        frontUser.setId(user.getId());
        UserRole userRole = this.getUserRoleByUserId(userId);
        frontUser.setUserRole(userRole);
        //发送消息到mq
        HotMapVO hotMapVO = new HotMapVO();
        hotMapVO.setUserId(userId);
        ZoneId zoneId = ZoneId.systemDefault();
        ChronoZonedDateTime<LocalDate> zonedDateTime = LocalDate.now().atStartOfDay(zoneId);
        Date nowDate = Date.from(zonedDateTime.toInstant());
        hotMapVO.setMapDate(nowDate);
        produceSenderConfig.sendAndNoConfirm(UUIDUtils.generateShortUuid(),hotMapVO);
        return frontUser;
    }

    /**
     * 获取用户角色信息
     * @param userId
     * @return
     */
    public UserRole getUserRoleByUserId(String userId) {
        List<Role> roleList = roleBiz.getRoleByUserId(userId);
        UserRole userRole = new UserRole();
        //使用list可能有点不太对劲
        BeanUtils.copyProperties(roleList.get(0), userRole);
        List<FrontPermission> frontPermissionList = this.getPermissionByUserId(userId);
        userRole.setFrontPermissionList(frontPermissionList);
        return userRole;
    }
}