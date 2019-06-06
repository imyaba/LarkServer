package com.github.hollykunge.security.admin.entity;

import com.github.hollykunge.security.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "ADMIN_NOTICE")
public class Notice extends BaseEntity {
    /**
     * 标题
     */
    @Column(name = "TITLE")
    private String title;
    /**
     * 类型
     */
    @Column(name = "TYPE")
    private String type;
    /**
     * 内容
     */
    @Column(name = "CONTENT")
    private String content;
    /**
     * 附件
     */
    @Column(name = "ATTACHMENT")
    private String attachment;
    /**
     * 是否置顶
     */
    @Column(name = "IS_TOP")
    private String isTop;
    /**
     * 组织ID
     */
    @Column(name = "ORG_ID")
    private String orgId;
    /**
     * 组织名称
     */
    @Column(name = "ORG_NAME")
    private String orgName;
}