package com.workhub.z.servicechat.controller;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.pagehelper.PageInfo;
import com.workhub.z.servicechat.VO.GroupListVo;
import com.workhub.z.servicechat.VO.UserNewMsgVo;
import com.workhub.z.servicechat.config.RandomId;
import com.workhub.z.servicechat.entity.ZzUserGroup;
import com.workhub.z.servicechat.service.ZzUserGroupService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 用户群组映射表(ZzUserGroup)表控制层
 *
 * @author 忠
 * @since 2019-05-10 14:22:54
 */
@RestController
@RequestMapping("/zzUserGroup")
public class ZzUserGroupController{
    /**
     * 服务对象
     */
    @Resource
    private ZzUserGroupService zzUserGroupService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/selectOne")
    public ObjectRestResponse selectOne(String id) {
        ZzUserGroup entity = this.zzUserGroupService.queryById(id);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        objectRestResponse.data(entity);
        return  objectRestResponse;
    }

    @PostMapping("/create")
    public ObjectRestResponse insert(@RequestBody ZzUserGroup zzUserGroup){
        zzUserGroup.setId(RandomId.getUUID());
        zzUserGroup.setCreatetime(new Date());
//        Integer insert = this.zzUserGroupService.insert(zzUserGroup);
        this.zzUserGroupService.insert(zzUserGroup);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
//        if (insert == null){
//            objectRestResponse.data("失败");
//            return objectRestResponse;
//        }
        objectRestResponse.data("成功");
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        return objectRestResponse;
    }

    @PostMapping("/update")
    public ObjectRestResponse update(@RequestBody ZzUserGroup zzUserGroup){
        Integer update = this.zzUserGroupService.update(zzUserGroup);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        if (update == null){
            objectRestResponse.data("失败");
            objectRestResponse.rel(false);
            return objectRestResponse;
        }
        objectRestResponse.data("成功");
        objectRestResponse.rel(true);
        objectRestResponse.msg("200");
        return objectRestResponse;
    }

    @DeleteMapping("/delete")
    public ObjectRestResponse delete(@RequestParam("id")String id){
        boolean flag = this.zzUserGroupService.deleteById(id);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.rel(flag);
        objectRestResponse.data("成功");
        objectRestResponse.msg("200");
        return objectRestResponse;
    }

    @PostMapping("/querygroup")
    //id参数是userid
    public TableResultResponse queryGroupList(@RequestParam("id")String id,
                                                  @RequestParam(value = "page",defaultValue = "1")Integer page,
                                                  @RequestParam(value = "size",defaultValue = "10")Integer size){
        PageInfo<GroupListVo> groupUserListVoPageInfo = null;
        try {
            groupUserListVoPageInfo = this.zzUserGroupService.groupUserList(id, page, size);
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

    @PostMapping("/usernewmsglist")
    public ListRestResponse getUserNewMsgList(@RequestParam("id") String id){
        List<UserNewMsgVo> userNewMsgList = this.zzUserGroupService.getUserNewMsgList(id);
        return new ListRestResponse("200",userNewMsgList==null?0:userNewMsgList.size(),userNewMsgList);
    }
    /**
     * 修改用户群个性化信息--是否置顶
     * @param userId 用户id；groupId 群id；topFlg 1置顶，0不置顶
     * @return  1成功；0用户不在组内或者组已经不存在；-1错误
     * @author zhuqz
     * @since 2019-06-11
     */
    @PostMapping("/setUserGroupTop")
    public ObjectRestResponse setUserGroupTop(@RequestParam("userId") String userId, @RequestParam("groupId") String groupId, @RequestParam("topFlg") String topFlg){
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();

        String oppRes = "1";
        try{
            oppRes = this.zzUserGroupService.setUserGroupTop(userId,groupId,topFlg);
        }catch(Exception e){
            e.printStackTrace();
            oppRes="-1";
        }
        if("1".equals(oppRes)){
            objectRestResponse.msg("200");//成功
            objectRestResponse.data("成功");
            objectRestResponse.rel(true);
        }else if("-1".equals(oppRes)){
            objectRestResponse.msg("失败");//失败
            objectRestResponse.rel(false);
        }else{
            objectRestResponse.data("0");//失败
            objectRestResponse.msg("用户群不存在，或者用户已经不在群中");
        }
        return  objectRestResponse;
    }
    /**
     * 修改用户群个性化信息--是否免打扰
     * @param userId 用户id；groupId 群id；muteFlg 1免打扰，0否
     * @return  1成功；0用户不在组内或者组已经不存在；-1错误
     * @author zhuqz
     * @since 2019-06-11
     */
    @PostMapping("/setUserGroupMute")
    public ObjectRestResponse setUserGroupMute(@RequestParam("userId") String userId, @RequestParam("groupId") String groupId, @RequestParam("muteFlg") String muteFlg){
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();

        String oppRes = "1";
        try{
            oppRes = this.zzUserGroupService.setUserGroupMute(userId,groupId,muteFlg);
        }catch(Exception e){
            e.printStackTrace();
            oppRes="-1";
        }
        if("1".equals(oppRes)){
            objectRestResponse.msg("200");//成功
            objectRestResponse.data("成功");
            objectRestResponse.rel(true);
        }else if("-1".equals(oppRes)){
            objectRestResponse.rel(false);
            objectRestResponse.data("失败");
        }else{
            objectRestResponse.data("用户群不存在，或者用户已经不在群中");//失败
            objectRestResponse.msg("200");
            objectRestResponse.rel(false);
        }
        return  objectRestResponse;
    }
    //测试一下获取最近联系的接口
    @PostMapping("/testGetContactVOList")
    //id参数是userid
    public void testGetContactVOList(@RequestParam("userId")String userId){
        this.zzUserGroupService.getContactVOList(userId);
    }
}