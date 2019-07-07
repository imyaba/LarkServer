package com.github.hollykunge.security.admin.mapper;

import com.github.hollykunge.security.admin.entity.Element;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 云雀
 */

public interface ElementMapper extends Mapper<Element> {

    /**
     * 根据角色id,菜单id获取ResourceMap中的element
     *
     * @param roleId 角色id
     * @param menuId 菜单id
     * @param type   资源类型
     * @return 元素列表
     */
    List<Element> getAuthorityMenuElement(@Param("roleId") String roleId, @Param("menuId") String menuId,
                                          @Param("type") String type);

    /**
     * 根据角色Id获取权限下的Element
     * @param roleId 角色Id
     * @return
     */
    List<Element> getElementByRoleId(@Param("roleId")String roleId);
}