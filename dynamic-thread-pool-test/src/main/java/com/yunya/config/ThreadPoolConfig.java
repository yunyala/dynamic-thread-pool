package com.yunya.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@Slf4j
@EnableAsync
@Configuration
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
public class ThreadPoolConfig {

    @Bean("threadPoolExecutor01")
    public ThreadPoolExecutor threadPoolExecutor01(ThreadPoolConfigProperties threadPoolConfigProperties) {
        // 实例化策略
        RejectedExecutionHandler handler;
        switch (threadPoolConfigProperties.getPolicy()) {
            case "AbortPolicy":
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
            case "CallerRunsPolicy":
                handler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            case "DiscardOldestPolicy":
                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            case "DiscardPolicy":
                handler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            default:
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }
        // 创建线程池
        return new ThreadPoolExecutor(threadPoolConfigProperties.getCorePoolSize(),
                threadPoolConfigProperties.getMaxPoolSize(),
                threadPoolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(threadPoolConfigProperties.getBlockQueueSize()),
                Executors.defaultThreadFactory(),
                handler);
    }

    @Bean("threadPoolExecutor02")
    public ThreadPoolExecutor threadPoolExecutor02(ThreadPoolConfigProperties threadPoolConfigProperties) {
        // 实例化策略
        RejectedExecutionHandler handler;
        switch (threadPoolConfigProperties.getPolicy()) {
            case "AbortPolicy":
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
            case "CallerRunsPolicy":
                handler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            case "DiscardOldestPolicy":
                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            case "DiscardPolicy":
                handler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            default:
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }
        // 创建线程池
        return new ThreadPoolExecutor(threadPoolConfigProperties.getCorePoolSize(),
                threadPoolConfigProperties.getMaxPoolSize(),
                threadPoolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(threadPoolConfigProperties.getBlockQueueSize()),
                Executors.defaultThreadFactory(),
                handler);
    }

}
