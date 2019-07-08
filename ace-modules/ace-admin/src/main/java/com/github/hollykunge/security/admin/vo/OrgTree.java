package com.github.hollykunge.security.admin.vo;

import com.github.hollykunge.security.common.vo.TreeNode;
import lombok.Data;

/**
 * @author holly
 */
@Data
public class OrgTree extends TreeNode {

    private String label;
    private String name;
    private Long order;
}
