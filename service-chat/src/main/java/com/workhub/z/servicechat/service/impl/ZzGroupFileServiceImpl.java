package com.workhub.z.servicechat.service.impl;

import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.workhub.z.servicechat.VO.GroupInfoVO;
import com.workhub.z.servicechat.config.common;
import com.workhub.z.servicechat.dao.ZzGroupFileDao;
import com.workhub.z.servicechat.entity.ZzGroupFile;
import com.workhub.z.servicechat.service.ZzGroupFileService;
import jodd.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群文件(ZzGroupFile)表服务实现类
 *
 * @author 忠
 * @since 2019-05-13 10:59:08
 */
@Service("zzGroupFileService")
public class ZzGroupFileServiceImpl extends BaseBiz<ZzGroupFileDao,ZzGroupFile > implements ZzGroupFileService {
    @Resource
    private ZzGroupFileDao zzGroupFileDao;

    /**
     * 通过ID查询单条数据
     *
     * @param fileId 主键
     * @return 实例对象
     */
    @Override
    public ZzGroupFile queryById(String fileId) {

        //return this.zzGroupFileDao.queryById(fileId);
        ZzGroupFile entity = new ZzGroupFile();
        entity.setFileId(fileId);
        return super.selectOne(entity);

    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<ZzGroupFile> queryAllByLimit(int offset, int limit) {
        return this.zzGroupFileDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param zzGroupFile 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public void insert(ZzGroupFile zzGroupFile) {
        int insert = this.zzGroupFileDao.insert(zzGroupFile);
//        return insert;
        //super.insert(zzGroupFile);
    }

    @Override
    protected String getPageName() {
        return null;
    }

    /**
     * 修改数据
     *
     * @param zzGroupFile 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public void update(ZzGroupFile zzGroupFile) {
        /*int update = this.zzGroupFileDao.update(zzGroupFile);
        return update;*/
        //super.updateById(zzGroupFile);
        this.zzGroupFileDao.update(zzGroupFile);
    }

    /**
     * 通过主键删除数据
     *
     * @param fileId 主键
     * @return 是否成功
     */
    @Override
    @Transactional
    public void deleteById(String fileId) {

        //return this.zzGroupFileDao.deleteById(fileId) > 0;
        ZzGroupFile entity = new ZzGroupFile();
        entity.setFileId(fileId);
        super.delete(entity);
    }

    /**
     * 查询群内文件信息
     * @param id
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    @Override
    public TableResultResponse<GroupInfoVO> groupFileList(String id, int page, int size) throws Exception {
        if (StringUtil.isEmpty(id)) throw new NullPointerException("id is null");
        PageHelper.startPage(page, size);
        List<GroupInfoVO> dataList =this.zzGroupFileDao.groupFileList(id);
        PageInfo<GroupInfoVO> pageInfo = new PageInfo<>(dataList);
        TableResultResponse<GroupInfoVO> res = new TableResultResponse<GroupInfoVO>(
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages(),
                pageInfo.getTotal(),
                pageInfo.getList()
        );
        return res;
    }

    @Override
    public Long groupFileListTotal(String id) throws Exception {
        return this.zzGroupFileDao.groupFileListTotal(id);
    }
    /**
     * 获取上传附件大小（数据库统计）
     *
     * @param queryType 查询类型0天（默认），1月，2年
     * @param queryDate 查询时间
     * @param returnUnit 返回结果单位  0 M（默认），1 G，2 T
     * @return 文件大小：单位兆
     */
    public String getGroupChatFileSizeByDB(String queryType, String queryDate, String returnUnit){
        String res="";
        String dateFmt="";
        long divide=1L;
        if(queryType.equals("0")){
            dateFmt="yyyy-mm-dd";
        }else if(queryType.equals("1")){
            dateFmt="yyyy-mm";
        }else{
            dateFmt="yyyy";
        }
        if(returnUnit.equals("0")){
            divide=1024*1024L;
        }else if(returnUnit.equals("1")){
            divide=1024*1024*1024L;
        }else{
            divide=1024*1024*1024*1024L;
        }
        double sizes = this.zzGroupFileDao.queryFileSize(dateFmt,queryDate,divide);
        res=String.valueOf(common.formatDouble2(sizes));
        return res;
    }
    /**
     * 获取上传附件区间段情况（数据库统计）
     *
     * @param queryType 查询类型0天（默认），1月，2年
     * @param queryDateBegin 查询时间开始
     * @param queryDateEnd 查询时间结束
     * @param returnUnit 返回结果单位  0 M（默认），1 G，2 T
     * @return 文件去区间段大小
     */
    public List<Map<String,String>> getGroupChatFileSizeRangeByDB(String queryType, String queryDateBegin, String queryDateEnd, String returnUnit) throws Exception{
        List<Map<String,String>> res=new ArrayList<>();
        String dateFmt="";
        long divide=1L;
        if(queryType.equals("0")){
            dateFmt="yyyy-mm-dd";
        }else if(queryType.equals("1")){
            dateFmt="yyyy-mm";
        }else{
            dateFmt="yyyy";
        }
        if(returnUnit.equals("0")){
            divide=1024*1024L;
        }else if(returnUnit.equals("1")){
            divide=1024*1024*1024L;
        }else{
            divide=1024*1024*1024*1024L;
        }
        List<Map> data = this.zzGroupFileDao.queryFileSizeRange(dateFmt,queryDateBegin,queryDateEnd,divide);
        for(Map<String,Object> temp : data){
            String date = (String)temp.get("DATES");
            String size = String.valueOf(common.formatDouble2(((BigDecimal)temp.get("SIZES")).doubleValue()));
            Map<String,String> map=new HashMap<>();
            map.put("date",date);
            map.put("size",size);
            res.add(map);
        }
        return res;
    }
}