package com.github.hollykunge.security.api.vo.authority;

import lombok.Data;

import java.util.List;

/**
 * @description: 用于用户菜单生成
 * @author: dd
 * @since: 2019-06-01
 */
@Data
public class FrontPermission {
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 菜单id
     * permissionId->menuId
     */
    private String menuId;
    /**
     * 权限标识
     */
    private String permissionId;
    /**
     * 菜单名称
     * permissionName->title
     */
    private String title;
    /**
     * 方法列表
     * actions->methods
     */
    private String methods;
    /**
     * 权限资源路径
     */
    private String uri;

    private List<ActionEntitySet> actionEntitySetList;

}
