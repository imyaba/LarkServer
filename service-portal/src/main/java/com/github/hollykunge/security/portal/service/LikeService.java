package com.github.hollykunge.security.portal.service;

import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.entity.Like;
import com.github.hollykunge.security.mapper.LikeMapper;

/**
 * @Author: yzq
 * @Date: 创建于 2019/7/2 10:28
 */
public class LikeService extends BaseBiz<LikeMapper,Like> {
    @Override
    protected String getPageName() {
        return null;
    }
}
