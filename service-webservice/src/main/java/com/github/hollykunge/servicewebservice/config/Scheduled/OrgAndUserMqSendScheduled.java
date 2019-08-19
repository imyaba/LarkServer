package com.github.hollykunge.servicewebservice.config.Scheduled;

import com.github.hollykunge.security.common.vo.mq.AdminOrgVO;
import com.github.hollykunge.security.common.vo.mq.AdminUserVO;
import com.github.hollykunge.servicewebservice.config.mq.ProduceSenderConfig;
import com.github.hollykunge.servicewebservice.dao.EryuanOrgDao;
import com.github.hollykunge.servicewebservice.dao.EryuanUserDao;
import com.github.hollykunge.servicewebservice.model.EryuanOrg;
import com.github.hollykunge.servicewebservice.model.EryuanUser;
import com.github.hollykunge.servicewebservice.service.EryuanUserService;
import com.github.hollykunge.servicewebservice.serviceImpl.EryuanUserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: yzq
 * @Date: 创建于 2019/7/30 17:22
 */
@Configuration
@EnableScheduling
@Slf4j
public class OrgAndUserMqSendScheduled {

    @Autowired
    private EryuanOrgDao eryuanOrgDao;

    @Autowired
    private EryuanUserDao eryuanUserDao;

    @Autowired
    private EryuanUserServiceImpl eryuanUserServiceImpl;

    @Autowired
    private ProduceSenderConfig produceSenderConfig;

    @Scheduled(cron = "0 0 0,6,13,18,21 * * ?")
    public void refreshOrgMq(){
        Example example = new Example(EryuanOrg.class);
        example.createCriteria().andEqualTo("isSuccess","0");
        List<EryuanOrg> eryuanOrgs = eryuanOrgDao.selectByExample(example);
        //创建org队列list
        List<AdminOrgVO> orgVOList = new ArrayList<AdminOrgVO>();
        //创建队列id标识
        StringBuilder ids = new StringBuilder();
        if (eryuanOrgs.size() != 0) {
            for (EryuanOrg org : eryuanOrgs){
                eryuanUserServiceImpl.saveAdminOrg(org,orgVOList,ids);
            }
            if(ids.length()>0){
                ids.setLength(ids.length()-1);
            }
            //将orgList发送到队列
            produceSenderConfig.sendOrgList(ids.toString(),orgVOList);
            log.info("将未成功同步org数据重新发送至队列。");
        }
        log.info("没有未同步成功的org数据。");
    }
    @Scheduled(cron = "0 0 1,6,13,18,22 * * ?")
    public void refreshUserMq(){
        Example example = new Example(EryuanOrg.class);
        example.createCriteria().andEqualTo("isSuccess","0");
        List<EryuanUser> eryuanUsers = eryuanUserDao.selectByExample(example);
        //创建user队列list
        List<AdminUserVO> userVOList = new ArrayList<AdminUserVO>();
        //创建队列id标识
        StringBuilder ids = new StringBuilder();
        if (eryuanUsers.size() != 0) {
            for (EryuanUser user : eryuanUsers){
                eryuanUserServiceImpl.saveAdminUser(user,userVOList,ids);
            }
            if(ids.length()>0){
                ids.setLength(ids.length()-1);
            }
            //将orgList发送到队列
            produceSenderConfig.sendUserList(ids.toString(),userVOList);
            log.info("将未成功同步user数据重新发送至队列。");
        }
        log.info("没有未同步成功user数据。");
    }
}
