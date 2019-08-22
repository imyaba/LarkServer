package com.github.hollykunge.security;

import com.ace.cache.EnableAceCache;
import com.github.hollykunge.security.auth.client.EnableAceAuthClient;
import com.github.tobato.fastdfs.FdfsClientConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * FastdfsClient客户端
 * @author  zhhongyu
 */
@EnableAceAuthClient
@EnableHystrix
@EnableAceCache
@SpringBootApplication
@EnableDiscoveryClient
@Import(FdfsClientConfig.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@MapperScan({"com.github.hollykunge.security.dao","com.github.hollykunge.security.mapper"})
@EnableAspectJAutoProxy(exposeProxy=true)
@EnableFeignClients({"com.github.hollykunge.security.auth.client.feign"})
public class FastdfsClientBootStrap {

    public static void main(String[] args) {
        SpringApplication.run(FastdfsClientBootStrap.class, args);
    }
}
