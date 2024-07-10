package com.yunya.middleware.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson.JSON;
import com.yunya.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态配置入口
 */
@Configuration
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            logger.warn("动态线程池，启动提示。SpringBoot 应用未配置 spring.application.name 无法获取到应用名称！");
        }

        logger.info("线程池信息：{}", JSON.toJSONString(threadPoolExecutorMap.keySet()));

        Set<String> threadPoolKeySet = threadPoolExecutorMap.keySet();
        for (String threadPoolKey : threadPoolKeySet) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolKey);
            int corePoolSize = threadPoolExecutor.getCorePoolSize();
            int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
            String simpleName = queue.getClass().getSimpleName();
            System.out.println("线程池名称：" + threadPoolKey + "，核心线程数：" + corePoolSize + "，最大线程数：" + maximumPoolSize + "，队列类型：" + simpleName);
        }

        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }


}
