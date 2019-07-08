package com.workhub.z.servicechat.service;

import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.workhub.z.servicechat.VO.FileMonitoringVO;
import com.workhub.z.servicechat.VO.GroupFileVo;
import com.workhub.z.servicechat.entity.ZzGroupFile;

import java.util.List;
import java.util.Map;

/**
 * 群文件(ZzGroupFile)表服务接口
 *
 * @author 忠
 * @since 2019-05-13 10:59:08
 */
public interface ZzGroupFileService {

    /**
     * 通过ID查询单条数据
     *
     * @param fileId 主键
     * @return 实例对象
     */
    ZzGroupFile queryById(String fileId);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<ZzGroupFile> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param zzGroupFile 实例对象
     * @return 实例对象
     */
    void insert(ZzGroupFile zzGroupFile);

    /**
     * 修改数据
     *
     * @param zzGroupFile 实例对象
     * @return 实例对象
     */
    void update(ZzGroupFile zzGroupFile);

    /**
     * 通过主键删除数据
     *
     * @param fileId 主键
     * @return 是否成功
     */
    void deleteById(String fileId);

    /**
     * 查询群组的文件信息
     * @param id
     * @return
     * @throws Exception
     */
    TableResultResponse<GroupFileVo> groupFileList(String id, int page, int size) throws Exception;

    /**
     * 查询群组的文件信息记录数
     * @param id
     * @return
     * @throws Exception
     */
    Long groupFileListTotal(String id) throws Exception;
    /**
     * 获取上传附件大小（数据库统计）
     *
     * @param queryType 查询类型0天（默认），1月，2年
     * @param queryDate 查询时间
     * @param returnUnit 返回结果单位  0 M（默认），1 G，2 T
     * @return 文件大小
     */
    public String getGroupChatFileSizeByDB(String queryType, String queryDate, String returnUnit) throws Exception;
    /**
     * 获取上传附件区间段情况(数据库统计)
     *
     * @param queryType 查询类型0天（默认），1月，2年
     * @param queryDateBegin 查询时间开始
     * @param queryDateEnd 查询时间结束
     * @param returnUnit 返回结果单位  0 M（默认），1 G，2 T
     * @return 文件去区间段大小
     */
    public List<Map<String,String>>  getGroupChatFileSizeRangeByDB(String queryType, String queryDateBegin, String queryDateEnd, String returnUnit) throws Exception;

    //文件数据库信息补全
    public int fileUpdate(String fileId,String receiverId,String level,String sendId,String sendName,String receiverName) throws Exception;
    //文件监控查询
    public TableResultResponse<FileMonitoringVO> fileMonitoring(Map<String,Object> params) throws Exception;
}