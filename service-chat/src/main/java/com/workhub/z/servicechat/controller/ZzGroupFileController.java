package com.workhub.z.servicechat.controller;

import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.workhub.z.servicechat.VO.FileMonitoringVO;
import com.workhub.z.servicechat.VO.GroupFileVo;
import com.workhub.z.servicechat.config.RandomId;
import com.workhub.z.servicechat.config.common;
import com.workhub.z.servicechat.entity.ZzGroupFile;
import com.workhub.z.servicechat.service.ZzGroupFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 群文件(ZzGroupFile)表控制层
 *
 * @author 忠
 * @since 2019-05-13 10:59:08
 */
@RestController
@RequestMapping("/zzGroupFile")
public class ZzGroupFileController {

    /**
     * 服务对象
     */
    @Resource
    private ZzGroupFileService zzGroupFileService;
    private static Logger log = LoggerFactory.getLogger(ZzGroupFileController.class);
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/selectOne")
    public ObjectRestResponse selectOne(@RequestParam("id") String id) {
        ZzGroupFile entity = this.zzGroupFileService.queryById(id);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        objectRestResponse.data(entity);
        return objectRestResponse;
    }

    /**
     * 群文件查询
     * @param id
     * @return
     */
    @PostMapping("/groupfile")
    public TableResultResponse<GroupFileVo> groupFileList(@RequestParam("id")String id,
                                                          @RequestParam(value = "page",defaultValue = "1")Integer page,
                                                          @RequestParam(value = "size",defaultValue = "10")Integer size){
        TableResultResponse<GroupFileVo> pageInfo = null;
        Long total = 0L;
        try {
            pageInfo = this.zzGroupFileService.groupFileList(id, page, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        T data, int pageSize, int pageNo, int totalPage, int totalCount
        return pageInfo;
    }


    /**
     * 文件删除(删记录)
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public ObjectRestResponse delFileInfo(@RequestParam("id") String id){
        //boolean flag = this.zzGroupFileService.deleteById(id);
        //ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        //objectRestResponse.data(flag);
        this.zzGroupFileService.deleteById(id);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        objectRestResponse.data("成功");
        return objectRestResponse;
    }

    /**
     * 创建
     * @param zzGroupFile
     * @return
     */
    @PostMapping("/create")
    public ObjectRestResponse insert(@RequestBody ZzGroupFile zzGroupFile){
        zzGroupFile.setFileId(RandomId.getUUID());
        zzGroupFile.setCreator("登陆人id");//TODO
        zzGroupFile.setCreateTime(new Date());
        try{
            common.putEntityNullToEmptyString(zzGroupFile);
        }catch(Exception e){
            e.printStackTrace();
        }
//        Integer insert = this.zzGroupFileService.insert(zzGroupFile);
        this.zzGroupFileService.insert(zzGroupFile);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        objectRestResponse.data("成功");
//        if (insert == null){
//            objectRestResponse.data("失败");
//            return objectRestResponse;
//        }
//        objectRestResponse.data("成功");
        return objectRestResponse;
    }

    /**
     * 修改
     * @param zzGroupFile
     * @return
     */
    @PostMapping("/update")
    public ObjectRestResponse update(@RequestBody ZzGroupFile zzGroupFile){
        zzGroupFile.setUpdator("登陆人id");//TODO
        zzGroupFile.setUpdateTime(new Date());
        //zzGroupFile.setFileId("1");
        /*Integer update = this.zzGroupFileService.update(zzGroupFile);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        if (update == 0){
            objectRestResponse.data("失败");
            return objectRestResponse;
        }
        objectRestResponse.data("成功");*/
        try{
            common.putEntityNullToEmptyString(zzGroupFile);
        }catch(Exception e){
            e.printStackTrace();
        }
        this.zzGroupFileService.update(zzGroupFile);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        objectRestResponse.data("成功");
        return objectRestResponse;
    }
    /**
     * 上传文件监控
     *参数说明：page 页码 size 每页几条 userName上传用户名称 dateBegin、dateEnd上传时间开始结束 isGroup 是否群主1是0否
     * fileName文件名称 level密级
     * @return
     */
    @PostMapping("/fileMonitoring")
    public TableResultResponse<FileMonitoringVO> fileMonitoring(@RequestParam Map<String,Object> params){
        TableResultResponse<FileMonitoringVO> pageInfo = null;
        try {
            pageInfo = this.zzGroupFileService.fileMonitoring(params);
        } catch (Exception e) {
            log.error(common.getExceptionMessage(e));
        }
        return pageInfo;
    }
}