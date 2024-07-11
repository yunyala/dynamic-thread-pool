package com.yunya.middleware.dynamic.thread.pool.sdk.trigger.job;

import com.alibaba.fastjson.JSON;
import com.yunya.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import com.yunya.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.yunya.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * 线程池数据上报任务
 */
public class ThreadPoolDataReportJob {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);

    private final IRegistry registry;

    private final DynamicThreadPoolService dynamicThreadPoolService;

    public ThreadPoolDataReportJob(IRegistry registry, DynamicThreadPoolService dynamicThreadPoolService) {
        this.registry = registry;
        this.dynamicThreadPoolService = dynamicThreadPoolService;
    }

    /**
     * 定时任务：上报线程池到注册中心
     */
    @Scheduled(cron = "0/20 * * * * ?")
    public void execReportThreadPoolList() {
        // 获取当前被监控的微服务的线程池列表
        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPoolList(threadPoolConfigEntityList);
        logger.info("动态线程池，上报线程池信息：{}", threadPoolConfigEntityList);
        logger.info("动态线程池，上报线程池信息：{}", JSON.toJSONString(threadPoolConfigEntityList));

        // 循环上传单个线程池到注册中心
        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntityList) {
            registry.reportThreadPool(threadPoolConfigEntity);
            logger.info("动态线程池，上报单个线程池信息：{}", JSON.toJSONString(threadPoolConfigEntity));
        }
    }

}
