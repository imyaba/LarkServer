package com.github.hollykunge.security.service.impl;

import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.dao.FileManageDao;
import com.github.hollykunge.security.entity.FileManageInf;
import com.github.hollykunge.security.service.FileMangeService;
import com.github.hollykunge.security.util.CommonUtil;
import com.github.hollykunge.security.vo.FileMonitoringVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("fileManageService")
public class FileManageServiceImpl implements FileMangeService {
    @Resource
    FileManageDao fileManageDao;

    public FileManageInf queryById(String fileId){
       return  this.fileManageDao.queryById(fileId);
    }
    public int insert(FileManageInf fileManageInf){
        return this.fileManageDao.insert(fileManageInf);
    }
    public int update(FileManageInf fileManageInf){
        return this.fileManageDao.update(fileManageInf);
    }
    public int deleteById(String fileId){
        return this.fileManageDao.deleteById(fileId);
    }
    public int fileUpdate(String fileId,String level,String type){
        return this.fileManageDao.fileUpdate(fileId,level,type);
    }
    /**
     * 获取上传附件大小（数据库统计）
     *
     * @param queryType 查询类型0天（默认），1月，2年
     * @param queryDate 查询时间
     * @param returnUnit 返回结果单位  0 M（默认），1 G，2 T
     * @return 文件大小：单位兆
     */
    public String getFileSizeByDB(String queryType, String queryDate, String returnUnit){
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
        double sizes = this.fileManageDao.queryFileSize(dateFmt,queryDate,divide);
        res=String.valueOf(CommonUtil.formatDouble2(sizes));
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
    public List<Map<String,String>> getFileSizeRangeByDB(String queryType, String queryDateBegin, String queryDateEnd, String returnUnit)  {
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
        List<Map> data = this.fileManageDao.queryFileSizeRange(dateFmt,queryDateBegin,queryDateEnd,divide);
        for(Map<String,Object> temp : data){
            String date = (String)temp.get("DATES");
            String size = String.valueOf(CommonUtil.formatDouble2(((BigDecimal)temp.get("SIZES")).doubleValue()));
            Map<String,String> map=new HashMap<>();
            map.put("date",date);
            map.put("size",size);
            res.add(map);
        }
        return res;
    }
    //文件监控查询
    //参数说明：page 页码 size 每页几条 userName上传用户名称 dateBegin、dateEnd上传时间开始结束 isGroup 是否群主1是0否
    //fileName文件名称 level密级
    public TableResultResponse<FileMonitoringVO> fileMonitoring(Map<String,Object> params) throws Exception{
        int page=Integer.valueOf(CommonUtil.nulToEmptyString(params.get("page")));
        int size=Integer.valueOf(CommonUtil.nulToEmptyString(params.get("size")));
        PageHelper.startPage(page, size);
        List<FileMonitoringVO> dataList =this.fileManageDao.fileMonitoring(params);
        //null的String类型属性转换空字符串
        CommonUtil.putVoNullStringToEmptyString(dataList);
        PageInfo<FileMonitoringVO> pageInfo = new PageInfo<>(dataList);
        TableResultResponse<FileMonitoringVO> res = new TableResultResponse<FileMonitoringVO>(
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages(),
                pageInfo.getTotal(),
                pageInfo.getList()
        );
        return res;
    }
}
