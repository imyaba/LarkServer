package com.github.hollykunge.security.api.vo.authority;

import lombok.Data;

import java.io.Serializable;

/**
 * 用于权限验证
 *
 * @author 协同设计小组
 * @create 2017-06-22 15:19
 */
@Data
public class PermissionInfo implements Serializable{
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 菜单id
     * permissionId->menuId
     */
    private String menuId;

    private String code;
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
}