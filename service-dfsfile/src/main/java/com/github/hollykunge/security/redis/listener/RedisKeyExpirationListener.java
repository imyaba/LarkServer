package com.github.hollykunge.security.redis.listener;

import com.ace.cache.api.impl.CacheRedis;
import com.alibaba.fastjson.JSONObject;
import com.github.hollykunge.security.biz.FileServerPathBiz;
import com.github.hollykunge.security.common.util.EntityUtils;
import com.github.hollykunge.security.comtants.FileComtants;
import com.github.hollykunge.security.entity.FileServerPathEntity;
import com.github.hollykunge.security.util.FastDFSClientWrapper;
import com.github.hollykunge.security.vo.FileAppendInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.utils.date.DateUtils;
import org.tio.utils.hutool.StrUtil;
import redis.clients.jedis.JedisPubSub;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author: zhhongyu
 * @description: jedis发布/订阅监听类
 * @since: Create in 9:36 2019/8/19
 */
@Component
@Slf4j
public class RedisKeyExpirationListener extends JedisPubSub {
    @Autowired
    private CacheRedis cacheRedis;
    @Autowired
    private FastDFSClientWrapper fastDFSClientWrapper;
    @Autowired
    private FileServerPathBiz fileServerPathBiz;

    private String pre = "i_";
    
    /**
     * 初始化按表达式的方式订阅时候的处理
     * @param pattern
     * @param subscribedChannels
     */
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        this.startLog(pattern,subscribedChannels);
    }

    /**
     * 取得按表达式的方式订阅的消息后的处理
     * @param pattern
     * @param channel
     * @param message
     */
    @Override
    public void onPMessage(String pattern, String channel, String message) {
        String serverPaht = "";
        try{
            if(message.contains(FileComtants.REDIS_KEY_APPEND_FILE)&&message.contains(pre)){
                String[] split = message.split(FileComtants.REDIS_KEY_APPEND_FILE);
                String fileKey = split[split.length-1];
                String jsonStr = cacheRedis.get(FileComtants.REDIS_KEY_PRE+fileKey);
                FileAppendInfoVO fileAppendInfoVO = JSONObject.parseObject(jsonStr, FileAppendInfoVO.class);
                serverPaht = fileAppendInfoVO.getFilePath();
                fastDFSClientWrapper.deleteFile(serverPaht);
                log.info("片段文件所对应的服务器路径为{},该文件由于用户没有再进行上传操作，已被删除",serverPaht);
            }
        }catch (Exception e){
            //记录日志采集，进行人工删除fastdfs没有用的文件，或保存在数据库中进行定时任务删除
            log.info("片段文件所对应的服务器路径为{}," +
                    "该文件由于用户没有再进行上传操作，需要手动进行删除",serverPaht);
            //进行数据库保存，执行定时任务定时删除没有被清除掉的垃圾文件
            FileServerPathEntity fileServerPathEntity = new FileServerPathEntity();
            EntityUtils.setCreatAndUpdatInfo(fileServerPathEntity);
            fileServerPathEntity.setFileEncrype(FileComtants.SENSITIVE_CIPHER_TYPE);
            fileServerPathEntity.setPath(serverPaht);
            fileServerPathEntity.setStatus(FileComtants.INVALID_FILE);
            fileServerPathBiz.insertSelective(fileServerPathEntity);
        }
    }

    private void startLog(String partten,int subscribedChannels){
        long start = System.currentTimeMillis();
        String baseStr = "|----------------------------------------------------------------------------------------|";
        int baseLen = baseStr.length();
        StackTraceElement[] ses = Thread.currentThread().getStackTrace();
        StackTraceElement se = ses[ses.length - 1];
        int xxLen = 18;
        int aaLen = baseLen - 3;
        List<String> infoList = new ArrayList();
        infoList.add(StrUtil.fillAfter("subscrber init", ' ', xxLen) + "| " + "success...");
        infoList.add(StrUtil.fillAfter("pattern", ' ', xxLen) + "| " + partten+".../");
        infoList.add(StrUtil.fillAfter("subscribedChannels", ' ', xxLen) + "| " + String.valueOf(subscribedChannels));
        infoList.add(StrUtil.fillAfter("-", '-', aaLen));
        infoList.add(StrUtil.fillAfter("Started at", ' ', xxLen) + "| " + DateUtils.formatDateTime(new Date()));

        String string;
        try {
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            String runtimeName = runtimeMxBean.getName();
            string = runtimeName.split("@")[0];
            long startTime = runtimeMxBean.getStartTime();
            long startCost = System.currentTimeMillis() - startTime;
            infoList.add(StrUtil.fillAfter("Jvm start time", ' ', xxLen) + "| " + startCost + " ms");
            infoList.add(StrUtil.fillAfter("Sub init time", ' ', xxLen) + "| " + (System.currentTimeMillis() - start) + " ms");
            infoList.add(StrUtil.fillAfter("Pid", ' ', xxLen) + "| " + string);
        } catch (Exception var21) {
            ;
        }

        String printStr = "\r\n" + baseStr + "\r\n";

        for(Iterator var23 = infoList.iterator(); var23.hasNext(); printStr = printStr + "| " + StrUtil.fillAfter(string, ' ', aaLen) + "|\r\n") {
            string = (String)var23.next();
        }

        printStr = printStr + baseStr + "\r\n";
        if (log.isInfoEnabled()) {
            log.info(printStr);
            log.info("server on start ... ");
        } else {
            System.out.println(printStr);
            System.out.println("server on start ... ");
        }
    }
}
