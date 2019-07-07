package com.github.hollykunge.security.auth.common.util.jwt;

/**
 *
 * @author 协同设计小组
 * @date 2017/9/10
 */
public interface IJWTInfo {
    /**
     * 获取用户名
     * @return
     */
    String getUniqueName();

    /**
     * 获取用户ID
     * @return
     */
    String getId();

    /**
     * 获取名称
     * @return
     */
    String getName();
}
