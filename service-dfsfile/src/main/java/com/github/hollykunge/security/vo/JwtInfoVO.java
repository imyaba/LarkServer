package com.github.hollykunge.security.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: zhhongyu
 * @description: token生成jwt实体类接收
 * @since: Create in 15:04 2019/8/21
 */
@Data
public class JwtInfoVO {
    private Date crtTime = new Date();

    private String crtUser;

    private String crtName;

    private String crtHost;

    private Date updTime = new Date();

    private String updUser;

    private String updName;

    private String updHost;
}
