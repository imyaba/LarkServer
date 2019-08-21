package com.workhub.z.servicechat.controller;

import com.alibaba.fastjson.JSONArray;
import com.github.hollykunge.security.api.vo.user.UserInfo;
import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.common.vo.rpcvo.ContactVO;
import com.github.pagehelper.PageInfo;
import com.workhub.z.servicechat.VO.GroupInfoVO;
import com.workhub.z.servicechat.VO.GroupUserListVo;
import com.workhub.z.servicechat.VO.GroupVO;
import com.workhub.z.servicechat.VO.UserInfoVO;
import com.workhub.z.servicechat.config.RandomId;
import com.workhub.z.servicechat.config.common;
import com.workhub.z.servicechat.entity.ZzGroup;
import com.workhub.z.servicechat.feign.IUserService;
import com.workhub.z.servicechat.service.ZzGroupMsgService;
import com.workhub.z.servicechat.service.ZzGroupService;
import com.workhub.z.servicechat.service.ZzMessageInfoService;
import com.workhub.z.servicechat.service.ZzUserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tio.core.ChannelContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.workhub.z.servicechat.config.VoToEntity.ZzGroupToGroupInfo;

/**
 * 群组表(ZzGroup)表控制层
 *
 * @author 忠
 * @since 2019-05-10 14:29:33
 */
@RestController
@RequestMapping("/zzGroup")
public class ZzGroupController  {
    private static Logger log = LoggerFactory.getLogger(ZzGroupController.class);
    /**
     * 服务对象
     */
    @Resource
    private ZzGroupService zzGroupService;

    @Resource
    private ZzUserGroupService userGroupService;

    @Resource
    private ZzGroupMsgService groupMsgService;

    @Resource
    private ZzMessageInfoService messageInfoService;

    @Autowired
    private IUserService iUserService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 成功
     */
    private static final String SUCCESS = "1";
    /**
     * 失败
     */
    private static final String FAIL = "-1";
    /**
     * 通过主键查询单条数据
     *
     * @param groupId 主键
     * @return 单条数据
     */
    @GetMapping("/getGroupInfo")
    public ObjectRestResponse selectOne(@RequestParam("groupId")String groupId) throws Exception {
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");

        GroupInfoVO groupInfo = new GroupInfoVO();
        groupInfo = (GroupInfoVO)ZzGroupToGroupInfo(this.zzGroupService.queryById(groupId));
        groupInfo.setMemberNum(Math.toIntExact(this.zzGroupService.groupUserListTotal(groupId)));
        common.putVoNullStringToEmptyString(groupInfo);
        objectRestResponse.data(groupInfo);
        return objectRestResponse;
    }

    @PostMapping("/create")
    public ObjectRestResponse insert(@RequestBody ZzGroup zzGroup,@RequestParam("token")String token) throws Exception{
        zzGroup.setGroupId(RandomId.getUUID());
        zzGroup.setCreateTime(new Date());
        String userId=common.nulToEmptyString(request.getHeader("userId"));
        String userName = URLDecoder.decode(common.nulToEmptyString(request.getHeader("userName")),"UTF-8");
        try {
            common.putEntityNullToEmptyString(zzGroup);
            if(zzGroup!=null && zzGroup.getIscross().equals("")){
                zzGroup.setIscross("0");
            }
            zzGroup.setCreator(userId);
            zzGroup.setCreatorName(userName);

            zzGroup.setGroupOwnerId(userId);
            zzGroup.setGroupOwnerName(userName);
        }catch (Exception e){
            log.error(common.getExceptionMessage(e));
        }
        this.zzGroupService.insert(zzGroup);
//        Integer insert = this.zzGroupService.insert(zzGroup);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
//        if (insert == 0){
//            objectRestResponse.data("失败");
//            return objectRestResponse;
//        }
        objectRestResponse.rel(true);
        objectRestResponse.msg("200");
        objectRestResponse.data("成功");
        return objectRestResponse;
    }

    @PostMapping("/update")
    public ObjectRestResponse update(@RequestBody ZzGroup zzGroup){
        String userId = common.nulToEmptyString(request.getHeader("userId"));
        try {
            common.putEntityNullToEmptyString(zzGroup);
            zzGroup.setCreateTime(null);
            zzGroup.setUpdator(userId);
        }catch (Exception e){
            e.getStackTrace();
        }
        Integer update = this.zzGroupService.update(zzGroup);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.rel(true);
        objectRestResponse.msg("200");
        if (update == null){
            objectRestResponse.rel(false);
            objectRestResponse.data("失败");
            return objectRestResponse;
        }
        objectRestResponse.data("成功");
        return objectRestResponse;
    }

    /**
     * 删除研讨组
     * @Author: dd
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    public ObjectRestResponse delete(@RequestParam("id")String id){
        boolean flag = this.zzGroupService.deleteById(id);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.data(flag);
        return objectRestResponse;
    }
    /**
    *@Description:
    *@Param:
    *@return:
    *@Author: 忠
    *@date: 2019/6/11
    */
    @PostMapping("/querygroupuser")
    public TableResultResponse queryGroupUserList(@RequestParam("id")String id,
                                                  @RequestParam(value = "page",defaultValue = "1")Integer page,
                                                  @RequestParam(value = "size",defaultValue = "10")Integer size){
        PageInfo<GroupUserListVo> groupUserListVoPageInfo = null;
        try {
            groupUserListVoPageInfo = this.zzGroupService.groupUserList(id, page, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //int pageSize, int pageNo, int totalPage, long totalCount, List<T> rows
        return new TableResultResponse(groupUserListVoPageInfo.getPageSize(),
                groupUserListVoPageInfo.getPageNum(),
                groupUserListVoPageInfo.getPages(),
                groupUserListVoPageInfo.getTotal(),
                groupUserListVoPageInfo.getList());
    }

    /**
    *@Description: 根据用户id查询用户所在群组信息
    *@Param: 用户id
    *@return: 群组列表
    *@Author: 忠
    *@date: 2019/6/11
    */
    @GetMapping("/queryGroupListByUserId")
    public ListRestResponse queryGroupListByUserId(@RequestParam("userId")String userId) throws Exception {
        List<ZzGroup> groups = this.zzGroupService.queryGroupListByUserId(userId);
        common.putVoNullStringToEmptyString(groups);
        return new ListRestResponse("200",groups.size(),groups);
    }

    @GetMapping("/queryContactListById")
    public ListRestResponse queryContactListById(@RequestParam("userId")String userId) throws Exception {
        List<ContactVO> contactVOS = userGroupService.getContactVOList(userId);
        common.putVoNullStringToEmptyString(contactVOS);
//        List<ZzGroup> groups = this.zzGroupService.queryGroupListByUserId(userId);
        return new ListRestResponse("200", contactVOS.size(), contactVOS);
    }

    @GetMapping("/queryHistoryMessageById")
    public ListRestResponse queryHistoryMessageById(@RequestParam("userId")String userId) throws Exception {
//        List<HistoryMessageVO> query = this.groupMsgService.queryHistoryMessageById(userId);
        String res =  messageInfoService.queryContactsMessage(userId);
        JSONArray myJsonArray = JSONArray.parseArray(res);
        return new ListRestResponse("200", 0,myJsonArray);
    }

    /**
     * 逻辑删除群
     * @param groupId 群id;delFlg：删除标记位，1删除，0 不删
     * @return  1成功；-1失败；
     * @author zhuqz
     * @since 2019-06-11
     */
    @PostMapping("/deleteGroupLogic")
    public ObjectRestResponse deleteGroupLogic(@RequestParam("groupId")String groupId,@RequestParam("delFlg")String delFlg) {
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        String oppRes = "1";
        try {
            this.zzGroupService.deleteGroupLogic(groupId,delFlg);
        }catch (Exception e){
            e.printStackTrace();
            oppRes = FAIL;
        }
        if(SUCCESS.equals(oppRes)){
            objectRestResponse.data("成功");
            objectRestResponse.rel(true);
            objectRestResponse.msg("200");
        }else{
            objectRestResponse.data("失败");
            objectRestResponse.rel(false);
            objectRestResponse.msg("200");
        }
        return objectRestResponse;
    }

    /**
     * 当前登录人查询具体某个人或者群的聊天记录,contactId表示个人或者群id
     * query 聊天内容、接收人
     * @param userId
     * @param contactId
     * @param isGroup
     * @param query
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    @GetMapping("/queryHistoryMessageForSingle")
    public TableResultResponse queryHistoryMessageForSingle(@RequestParam("userId")String userId,@RequestParam("contactId")String contactId,@RequestParam("isGroup")String isGroup,@RequestParam("query")String query,@RequestParam("page")String page,@RequestParam("size")String size) throws Exception {
        TableResultResponse resultResponse = messageInfoService.queryHistoryMessageForSingle(userId,contactId,isGroup,query,page,size);
        return resultResponse;
    }
    /**
     * 查询群成员列表信息
     *
     * @param groupId 主键
     * @return 单条数据
     */
    @GetMapping("/getGroupUserList")
    public ListRestResponse getGroupUserList(@RequestParam("groupId")String groupId) throws Exception {

        String userIds= this.zzGroupService.getGroupUserList(groupId);
        List<UserInfo> list  = iUserService.userList(userIds);
        List<UserInfoVO> dataList = new ArrayList<>();
        for(UserInfo userTemp:list){
            UserInfoVO vo = new UserInfoVO();
            vo.setId(userTemp.getId());
            vo.setName(userTemp.getName());
            vo.setSecretLevel(userTemp.getSecretLevel());
            vo.setOrgId(userTemp.getOrgCode());
            vo.setOrgName(userTemp.getOrgName());
            vo.setAvatar(userTemp.getAvatar());
            //ToDo 群成员是否在线需要完善
            vo.setOnline("1");
            dataList.add(vo);
        }
        ListRestResponse res=new ListRestResponse("200",dataList.size(),dataList);
        return res;
    }
    /**
     * 群列表监控
     //param:page 页码 size 每页几条;group_name群组名称；creator创建人姓名；level密级；
     // dateBegin创建时间开始；dateEnd创建时间结束；pname项目名称；isclose是否关闭;groupOwnerName群主名称
     * @return
     */
    @GetMapping("/groupListMonitoring")
    public TableResultResponse<GroupVO> groupListMonitoring(@RequestParam Map<String,String> params){
        TableResultResponse<GroupVO> pageInfo = null;
        try {
            pageInfo = this.zzGroupService.groupListMonitoring(params);
        } catch (Exception e) {
            log.error(common.getExceptionMessage(e));
        }
        return pageInfo;
    }

    /**
     * 解散本研讨组
     */
    @GetMapping("dissolve")
    public ObjectRestResponse dissolve(@RequestParam("groupId") String groupId,ChannelContext channelContext) {
        zzGroupService.dissolveGroup(groupId);
        return new ObjectRestResponse().rel(true).msg("研讨组已解散...");
    }

    /**
     * 移除研讨组成员
     */
    @GetMapping("removeMember")
    public ObjectRestResponse removeMember(@RequestParam("groupId") String groupId, @RequestParam("userId") String userId) {
        zzGroupService.removeMember(groupId, userId);
        return new ObjectRestResponse().rel(true).msg("成功移除组成员");
    }

    /**
     * 添加研讨组成员
     */
    @GetMapping("addMember")
    public ObjectRestResponse addMember(@RequestParam("groupId") String groupId, @RequestParam("userIds") String userIds,ChannelContext channelContext) {
        zzGroupService.addMember(groupId, userIds);
        return new ObjectRestResponse().rel(true).msg("成功添加组成员");
    }
}