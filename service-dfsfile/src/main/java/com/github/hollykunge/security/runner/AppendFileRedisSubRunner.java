package com.github.hollykunge.security.runner;

import com.github.hollykunge.security.redis.subscribe.AppendFileSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * @author: zhhongyu
 * @description: 服务启动一次性生成redis定义规则key失效订阅者
 * @since: Create in 10:23 2019/8/19
 */
@Configuration
@Slf4j
public class AppendFileRedisSubRunner implements CommandLineRunner {
    @Autowired
    private AppendFileSubscriber appendFileSubscriber;

    @Override
    public void run(String... args) throws Exception {
        appendFileSubscriber.generatorSubscriber();
    }
}
