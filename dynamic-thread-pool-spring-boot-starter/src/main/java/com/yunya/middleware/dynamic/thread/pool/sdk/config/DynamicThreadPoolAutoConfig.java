package com.yunya.middleware.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson.JSON;
import com.yunya.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import com.yunya.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import com.yunya.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import com.yunya.middleware.dynamic.thread.pool.sdk.trigger.job.ThreadPoolDataReportJob;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(RedissonProperties.class)
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    @Bean("redissonClient")
    public RedissonClient redissonClient(RedissonProperties properties) {
        Config config = new Config();
        // 设定解码器
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress(properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setConnectionPoolSize(properties.getPoolSize())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectionTimeout())
                .setKeepAlive(properties.isKeepAlive());

        RedissonClient redissonClient = Redisson.create();

        logger.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    @Bean
    public IRegistry redisRegistry(RedissonClient redissonClient) {
        return new RedisRegistry(redissonClient);
    }

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

    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(IRegistry registry, DynamicThreadPoolService dynamicThreadPoolService) {
        return new ThreadPoolDataReportJob(registry, dynamicThreadPoolService);
    }


}
