package com.github.hollykunge.security.admin.constant;

/**
 * ${DESCRIPTION}
 *
 * @author 协同设计小组
 * @create 2017-06-17 14:41
 */
public class AdminCommonConstant {
    public final static String ROOT = "root";
    public final static int DEFAULT_GROUP_TYPE = 0;
    /**
     * 权限关联类型
     */
    public final static String AUTHORITY_TYPE_GROUP = "group";
    /**
     * 权限资源类型
     */
    public final static String RESOURCE_TYPE_MENU = "menu";
    public final static String RESOURCE_TYPE_BTN = "button";

    public final static String RESOURCE_REQUEST_METHOD_GET = "GET";
    public final static String RESOURCE_REQUEST_METHOD_PUT = "PUT";
    public final static String RESOURCE_REQUEST_METHOD_DELETE = "DELETE";
    public final static String RESOURCE_REQUEST_METHOD_POST = "POST";

    public final static String RESOURCE_ACTION_VISIT = "访问";

    public final static String BOOLEAN_NUMBER_FALSE = "0";

    public final static String BOOLEAN_NUMBER_TRUE = "1";
    /**
     * 通知死信队列交换机名称
     */
    public final static String NOTICE_DEAD_EXCHANGENAME = "notice_dead_exchange";
    /**
     * 通知死信队列路由键
     */
    public final static String NOTICE_DEAD_ROUTING_KEY = "notice_dead_routing_key";


    /**
     * 死信队列 交换机标识符
     */
    public static final String DEAD_LETTER_QUEUE_KEY = "x-dead-letter-exchange";
    /**
     * 死信队列交换机绑定键标识符
     */
    public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    /**
     * 创建角色时默认给权限的三个菜单
     */
    public static final String DEFAULT_MENU_PERMISSION_CODE_LIST = "talk,dashboard,login,self";

    public static final String NO_DATA_ORG_CODE = "0010";


    /**
     *     三元角色
     */
//    安全管理员
    public static final String SECURITY_USER = "安全管理员";
//    日志审计员
    public static final String LOG_USER = "日志审计员";
//    系统管理员
    public static final String SYSTEM_USER = "系统管理员";

}
