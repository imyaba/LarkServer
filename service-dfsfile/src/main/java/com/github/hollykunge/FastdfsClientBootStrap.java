package com.github.hollykunge;

import com.ace.cache.EnableAceCache;
import com.github.tobato.fastdfs.FdfsClientConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * FastdfsClient客户端
 * @author  zhhongyu
 */
@EnableHystrix
@EnableAceCache
@SpringBootApplication
@EnableDiscoveryClient
@Import(FdfsClientConfig.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@MapperScan({"com.github.hollykunge.dao","com.github.hollykunge.mapper"})
@EnableAspectJAutoProxy(exposeProxy=true)
public class FastdfsClientBootStrap {

    public static void main(String[] args) {
        SpringApplication.run(FastdfsClientBootStrap.class, args);
    }
}
