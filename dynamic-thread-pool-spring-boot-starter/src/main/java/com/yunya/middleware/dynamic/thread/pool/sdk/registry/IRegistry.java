package com.yunya.middleware.dynamic.thread.pool.sdk.registry;

import com.yunya.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 注册中心接口
 */
public interface IRegistry {

    /**
     * 上报线程池列表到注册中心
     */
    void reportThreadPoolList(List<ThreadPoolConfigEntity> threadPoolConfigEntityList);

    /**
     * 上报单个线程池到注册中心
     */
    void reportThreadPool(ThreadPoolConfigEntity threadPoolConfigEntity);

}
