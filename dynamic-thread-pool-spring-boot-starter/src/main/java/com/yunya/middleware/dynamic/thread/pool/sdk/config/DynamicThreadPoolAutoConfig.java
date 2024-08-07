package com.yunya.middleware.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson.JSON;
import com.yunya.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import com.yunya.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import com.yunya.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.yunya.middleware.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import com.yunya.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import com.yunya.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import com.yunya.middleware.dynamic.thread.pool.sdk.trigger.job.ThreadPoolDataReportJob;
import com.yunya.middleware.dynamic.thread.pool.sdk.trigger.listener.ThreadPoolConfigAdjustListener;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态配置入口
 */
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
@EnableScheduling
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    private String applicationName;

    @Bean("redissonClient")
    public RedissonClient redissonClient(RedissonProperties properties) {
        Config config = new Config();
        // 设定解码器
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setConnectionPoolSize(properties.getPoolSize())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectionTimeout())
                .setKeepAlive(properties.isKeepAlive());

        // 之前这里没有传参config，导致存、取数据序列化都失败
        RedissonClient redissonClient = Redisson.create(config);

        logger.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    @Bean
    public IRegistry redisRegistry(RedissonClient redissonClient) {
        return new RedisRegistry(redissonClient);
    }

    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient) {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            logger.warn("动态线程池，启动提示。SpringBoot 应用未配置 spring.application.name 无法获取到应用名称！");
        }

        logger.info("线程池信息：{}", JSON.toJSONString(threadPoolExecutorMap.keySet()));

        // 旧版本，直接从配置文件中获取线程池配置，这种方案会导致每次重启项目都会丢失之前调优设置好的线程池配置参数。
//        Set<String> threadPoolKeySet = threadPoolExecutorMap.keySet();
//        for (String threadPoolKey : threadPoolKeySet) {
//            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolKey);
//            int corePoolSize = threadPoolExecutor.getCorePoolSize();
//            int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
//            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
//            String simpleName = queue.getClass().getSimpleName();
//            System.out.println("线程池名称：" + threadPoolKey + "，核心线程数：" + corePoolSize + "，最大线程数：" + maximumPoolSize + "，队列类型：" + simpleName);
//        }

        // 获取缓存数据，设置本地线程池配置
        Set<String> threadPoolKeySet = threadPoolExecutorMap.keySet();
        for (String threadPoolKey : threadPoolKeySet) {
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + threadPoolKey).get();
            if (threadPoolConfigEntity == null) continue;
            // 如果不为空，则使用缓存数据更新本地线程池配置。
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolKey);
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        }

        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }

    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService) {
        return new ThreadPoolDataReportJob(registry, dynamicThreadPoolService);
    }

    @Bean
    public ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        return new ThreadPoolConfigAdjustListener(dynamicThreadPoolService, registry);
    }

    @Bean(name = "dynamicThreadPoolAdjustRedisTopic")
    public RTopic threadPoolConfigAdjustListener(RedissonClient redissonClient, ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener) {
        RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + applicationName);
        topic.addListener(ThreadPoolConfigEntity.class, threadPoolConfigAdjustListener);
        return topic;
    }

}
