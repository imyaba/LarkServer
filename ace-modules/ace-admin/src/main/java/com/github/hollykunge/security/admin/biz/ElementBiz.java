package com.github.hollykunge.security.admin.biz;

import com.ace.cache.annotation.Cache;
import com.ace.cache.annotation.CacheClear;
import com.alibaba.fastjson.JSON;
import com.github.hollykunge.security.admin.entity.Element;
import com.github.hollykunge.security.admin.entity.Role;
import com.github.hollykunge.security.admin.mapper.ElementMapper;
import com.github.hollykunge.security.admin.vo.AdminElement;
import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.common.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author 协同设计小组
 * @create 2017-06-23 20:27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ElementBiz extends BaseBiz<ElementMapper,Element> {
    @Autowired
    private RoleBiz roleBiz;
//    @Cache(key="permission:ele:u{1}")
//    public List<Element> getAuthorityElementByUserId(String userId){
//       return mapper.selectAuthorityElementByUserId(userId);
//    }
//    public List<Element> getAuthorityElementByUserId(String userId,String menuId){
//        return mapper.selectAuthorityMenuElementByUserId(userId,menuId);
//    }
//
//    @Cache(key="permission:ele")
//    public List<Element> getAllElementPermissions(){
//        return mapper.selectAllElementPermissions();
//    }

    @Override
//    @CacheClear(keys={"permission:ele","permission"})
    public void insertSelective(Element entity) {
        super.insertSelective(entity);
    }

    @Override
//    @CacheClear(keys={"permission:ele","permission"})
    public void updateSelectiveById(Element entity) {
        super.updateSelectiveById(entity);
    }

    @Override
//    @CacheClear(keys={"permission:ele","permission"})
    public void deleteById(String id) {
        super.deleteById(id);
    }

    @Override
    protected String getPageName() {
        return null;
    }

    /**
     * 通过userId获取权限下的Element
     * @param userId
     * @return
     */
    public List<Element> getElementByUserId(String userId){
        List<Element> result = new ArrayList<>();
        List<Role> roleList = roleBiz.getRoleByUserId(userId);
        roleList.stream().forEach(roleEntity ->{
            List<Element> roleElemet = mapper.getElementByRoleId(roleEntity.getId());
            result.addAll(roleElemet);
        });
        return result;
    }

    /**
     * 通过menuid获取menu下的所有的菜单
     * @param menuId menuId
     * @return Element信息集
     */
    public List<AdminElement> listMenuElement(String menuId){
        Example params = new Example(Element.class);
        params.createCriteria().andEqualTo("menuId",menuId);
        List<Element> elements = mapper.selectByExample(params);
        return JSON.parseArray(JSON.toJSONString(elements),AdminElement.class);
    }

    /**
     * 保存或修改menu下的element
     * @param menuId 菜单id
     * @param elementList 绑定的element
     */
    public void modifyMenuElement(String menuId,List<AdminElement> elementList){
        Element elementDO = new Element();
        elementDO.setMenuId(menuId);
        mapper.delete(elementDO);
        if(elementList.size()>0){
            elementList.stream().forEach(elementVO->{
                BeanUtils.copyProperties(elementVO,elementDO);
                EntityUtils.setCreatAndUpdatInfo(elementDO);
                mapper.insertSelective(elementDO);
            });
        }
    }
}
