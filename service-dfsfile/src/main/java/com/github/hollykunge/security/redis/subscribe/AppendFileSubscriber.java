package com.github.hollykunge.security.redis.subscribe;

import com.ace.cache.api.impl.CacheRedis;
import com.github.hollykunge.security.redis.listener.RedisKeyExpirationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author: zhhongyu
 * @description: 文件分块redis键失效订阅者
 * @since: Create in 10:14 2019/8/19
 */
@Component
@Slf4j
public class AppendFileSubscriber{
    @Autowired
    private JedisPool pool;
    @Autowired
    private RedisKeyExpirationListener redisKeyExpirationListener;
    @Autowired
    private CacheRedis cacheRedis;
    @Value("${redis.database}")
    private String database;

    /**
     * 由于订阅模式使用的是阻塞式方法，该处使用多线程
     */
    public void generatorSubscriber(){
        SubscriberThread subscriberThread = new SubscriberThread();
        subscriberThread.start();
    }
    class SubscriberThread extends Thread{
        @Override
        public void run() {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                //生成订阅失效key规则，也就是频道
//                String keyRule = CacheConstants.PRE+cacheRedis.addSys("*");
                String keyRule = "__keyevent@4__:expired";
                jedis.psubscribe(redisKeyExpirationListener, keyRule);
            } catch (Exception e) {
                log.error(String.format("subsrcibe channel error, %s", e));
                throw e;
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

}
