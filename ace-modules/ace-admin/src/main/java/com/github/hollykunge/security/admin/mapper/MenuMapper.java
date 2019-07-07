package com.github.hollykunge.security.admin.mapper;

import com.github.hollykunge.security.admin.entity.Menu;
import com.github.hollykunge.security.admin.entity.ResourceRoleMap;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MenuMapper extends Mapper<Menu> {
    /**
     * 根据角色Id获取权限下的Element
     * @param roleId 角色Id
     * @return
     */
    List<Menu> selectMenuByRoleId(@Param("roleId")String roleId);
}