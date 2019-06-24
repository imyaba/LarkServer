package com.workhub.z.servicechat.service.impl;

import com.github.hollykunge.security.common.biz.BaseBiz;
import com.workhub.z.servicechat.VO.NoReadVo;
import com.workhub.z.servicechat.entity.ZzMsgReadRelation;
import com.workhub.z.servicechat.dao.ZzMsgReadRelationDao;
import com.workhub.z.servicechat.service.ZzMsgReadRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息阅读状态关系表(ZzMsgReadRelation)表服务实现类
 *
 * @author makejava
 * @since 2019-05-23 13:27:22
 */
@Service("zzMsgReadRelationService")
public class ZzMsgReadRelationServiceImpl implements ZzMsgReadRelationService {
    @Resource
    private ZzMsgReadRelationDao zzMsgReadRelationDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ZzMsgReadRelation queryById(String id) {
        return this.zzMsgReadRelationDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<ZzMsgReadRelation> queryAllByLimit(int offset, int limit) {
        return this.zzMsgReadRelationDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param zzMsgReadRelation 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public void insert(ZzMsgReadRelation zzMsgReadRelation) {
        zzMsgReadRelationDao.insert(zzMsgReadRelation);
    }

    /**
     * 修改数据
     *
     * @param zzMsgReadRelation 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public ZzMsgReadRelation update(ZzMsgReadRelation zzMsgReadRelation) {
        this.zzMsgReadRelationDao.update(zzMsgReadRelation);
        return this.queryById(zzMsgReadRelation.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deleteById(String id) {
        return this.zzMsgReadRelationDao.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByConsumerAndSender(String sender, String consumer) {
        return this.zzMsgReadRelationDao.deleteByConsumerAndSender(sender,consumer);
    }

    @Override
    public Long queryNoReadCount(String consumer) {
        return this.zzMsgReadRelationDao.queryNoReadCount(consumer);
    }

    @Override
    public List<NoReadVo> queryNoReadCountList(String consumer) {
        return this.zzMsgReadRelationDao.queryNoReadCountList(consumer);
    }

    /**
    *@Description:
    *@Param: receiver 当前登录人，sender 消息发送人
    *@return: 未读消息条数
    *@Author: 忠
    *@date: 2019/6/23
    */
    @Override
    public int queryNoReadMsgBySenderAndReceiver(String sender, String receiver) {
        return Math.toIntExact(this.zzMsgReadRelationDao.queryNoReadMsgBySenderAndReceiver(sender, receiver));
    }
}