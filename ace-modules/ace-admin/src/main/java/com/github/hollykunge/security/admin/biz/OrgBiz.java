package com.github.hollykunge.security.admin.biz;

import com.ace.cache.annotation.Cache;
import com.ace.cache.annotation.CacheClear;
import com.alibaba.fastjson.JSON;
import com.github.hollykunge.security.admin.constant.AdminCommonConstant;
import com.github.hollykunge.security.admin.entity.Org;
import com.github.hollykunge.security.admin.entity.OrgUserMap;
import com.github.hollykunge.security.admin.entity.User;
import com.github.hollykunge.security.admin.mapper.OrgMapper;
import com.github.hollykunge.security.admin.mapper.OrgUserMapMapper;
import com.github.hollykunge.security.admin.mapper.UserMapper;
import com.github.hollykunge.security.admin.vo.AdminUser;
import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.util.EntityUtils;
import com.github.hollykunge.security.common.vo.OrgUser;
import com.github.hollykunge.security.common.vo.TreeNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 云雀小组
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class OrgBiz extends BaseBiz<OrgMapper, Org> {

    @Resource
    private OrgUserMapMapper orgUserMapMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserBiz userBiz;

    public List<AdminUser> getOrgUsers(String orgCode,String secretLevels,String pid) {
        //如果orgCode为航天二院，则直接返回空数据
        if(AdminCommonConstant.NO_DATA_ORG_CODE.equals(orgCode)){
            return new ArrayList<AdminUser>();
        }
        User userParams = new User();
        userParams.setOrgCode(orgCode);
        Example userExample = new Example(User.class);
        Example.Criteria criteria = userExample.createCriteria();
        //TODO:处理删除人员。deleted为2或者null
        criteria.andLike("orgCode","%"+orgCode+"%").andNotEqualTo("deleted","2").andNotEqualTo("deleted",null);
        if(!StringUtils.isEmpty(secretLevels)){
            String[] secretLevelArray = secretLevels.split(",");
            List<String> secretList = Arrays.asList(secretLevelArray);
            for (String secretLevel:secretLevelArray ) {
                criteria.andIn("secretLevel",secretList);
            }
        }
        if(!StringUtils.isEmpty(pid)){
            criteria.andNotEqualTo("pId",pid);
        }
        List<User> users = userMapper.selectByExample(userExample);
        Collections.sort(users, Comparator.comparing(User::getOrderId));
        List<AdminUser> userList;
        userList = JSON.parseArray(JSON.toJSONString(users),AdminUser.class);
        return userList;
    }

//    @CacheClear(pre = "permission")
    public void modifyOrgUsers(String orgId, String users) {
        OrgUserMap orgUserMap = new OrgUserMap();
        orgUserMap.setOrgId(orgId);
        orgUserMapMapper.delete(orgUserMap);
        if (!StringUtils.isEmpty(users)) {
            String[] mem = users.split(",");
            for (String m : mem) {
                orgUserMap.setUserId(m);
                EntityUtils.setCreatAndUpdatInfo(orgUserMap);
                orgUserMapMapper.insertSelective(orgUserMap);
            }
        }
    }

    @Override
    protected String getPageName() {
        return null;
    }
//    @Cache(key = "orgUsers{2}") TODO:有点问题 造成前端第二次刷新显示不正确
    public List<OrgUser> getOrg(String parentTreeId) {
        List<Org> orgs = this.selectListAll();
        Collections.sort(orgs, Comparator.comparing(Org::getOrderId));
        List<OrgUser> orgUserList = this.buildByRecursive(orgs, parentTreeId);
        return orgUserList;
    }
    private List<OrgUser> buildByRecursive(List<Org> orgs,Object root) {
        List<OrgUser> result = new ArrayList<>();
        List<OrgUser> treeNodes;
        treeNodes = JSON.parseArray(JSON.toJSONString(orgs),OrgUser.class);
        for (int i = 0; i < treeNodes.size(); i++) {
            OrgUser treeNode = treeNodes.get(i);
            treeNode.setTitle(orgs.get(i).getOrgName());
            if (root.equals(treeNode.getParentId())) {
                OrgUser children = findChildren(treeNode, treeNodes);
                result.add(children);
            }
        }
        return result;
    }
    private OrgUser findChildren(OrgUser treeNode, List<OrgUser> treeNodes) {
        for (OrgUser it : treeNodes) {
            if (treeNode.getId().equals(it.getParentId())) {
                if (treeNode.getChildren()==null) {
                    treeNode.setChildren(new ArrayList<TreeNode>());
                }
                OrgUser children = findChildren(it, treeNodes);
                //如果children中的children为空则为底层子节点，赋值用户信息
                if(children.getChildren().size()==0){
                    User params = new User();
                    params.setOrgCode(super.selectById(children.getId()).getOrgCode());
                    List<User> users = userBiz.selectList(params);
                    Collections.sort(users, Comparator.comparing(User::getOrderId));
                    //TODO:处理删除人员。deleted为2或者null
                    users.stream().filter(user -> !StringUtils.isEmpty(user.getDeleted()) && !user.getDeleted().equals("2")).forEach(user ->{
                        OrgUser orgUser = new OrgUser();
                        orgUser.setIcon(user.getAvatar());
                        orgUser.setKey(user.getId());
                        orgUser.setScopedSlotsTitle("userNode");
                        orgUser.setTitle(user.getName());
                        orgUser.setOnline(true);
                        children.add(orgUser);
                    });
                }
                children.setScopedSlotsTitle("orgNode");
                children.setOnline(null);
                treeNode.add(children);
            }
        }
        return treeNode;
    }
}