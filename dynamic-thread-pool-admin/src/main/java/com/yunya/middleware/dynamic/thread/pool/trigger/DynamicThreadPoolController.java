package com.yunya.middleware.dynamic.thread.pool.trigger;

import com.alibaba.fastjson.JSON;
import com.yunya.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.yunya.middleware.dynamic.thread.pool.types.Response;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 提供接口给外部调用，查看线程池配置和修改线程池配置
 * @Author yunyala
 * @Date 2024/7/14 17:38
 */
@RequestMapping("/api/v1/dynamic/thread/pool/")
@CrossOrigin("*")
@RestController
@Slf4j
public class DynamicThreadPoolController {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 查询线程池配置列表
     * curl --request GET
     * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_list'
     * @return
     */
    @RequestMapping(value = "query_thread_pool_list", method = RequestMethod.GET)
    public Response<List<ThreadPoolConfigEntity>> queryThreadPoolList() {
        try {
            RList<ThreadPoolConfigEntity> cacheList = redissonClient.getList("THREAD_POOL_CONFIG_LIST_KEY");
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .data(cacheList.readAll())
                    .build();
        } catch (Exception e) {
            log.error("查询线程池配置异常", e);
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(Response.Code.UN_ERROR.getCode())
                    .info(Response.Code.UN_ERROR.getInfo())
                    .build();
        }

    }

    /**
     * 查询线程池配置
     * curl --request GET
     *      * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_by_name?appName=dynamic-thread-pool-test-app&threadPoolName=threadPoolExecutor01'
     * @param appName
     * @param threadPoolName
     * @return
     */
    @RequestMapping(value = "query_thread_pool_by_name", method = RequestMethod.GET)
    public Response<ThreadPoolConfigEntity> queryThreadPoolByName(@RequestParam String appName, @RequestParam String threadPoolName) {
        String cacheKey = "THREAD_POOL_CONFIG_PARAMETER_LIST_KEY" + "_" + appName + "_" + threadPoolName;
        ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
        return Response.<ThreadPoolConfigEntity>builder()
                .code(Response.Code.SUCCESS.getCode())
                .info(Response.Code.SUCCESS.getInfo())
                .data(threadPoolConfigEntity)
                .build();
    }

    /**
     * 修改线程池
     * curl --request POST \
     * --url http://localhost:8089/api/v1/dynamic/thread/pool/update_thread_pool \
     * --header 'content-type: application/json' \
     * --data '{
     * "appName":"dynamic-thread-pool-test-app",
     * "threadPoolName": "threadPoolExecutor",
     * "corePoolSize": 1,
     * "maximumPoolSize": 10
     * }'
     * @param threadPoolConfigEntity
     * @return
     */
    @RequestMapping(value = "update_thread_pool", method = RequestMethod.POST)
    public Response<Boolean> updateThreadPool(@RequestBody ThreadPoolConfigEntity threadPoolConfigEntity) {
        log.info("修改线程池开始，应用名称：{}，线程池名称：{}，请求参数：{}", threadPoolConfigEntity.getAppName(), threadPoolConfigEntity.getThreadPoolName(), JSON.toJSONString(threadPoolConfigEntity));
        RTopic topic = redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + threadPoolConfigEntity.getAppName());
        topic.publish(threadPoolConfigEntity);
        log.info("修改线程池完成");
        return Response.<Boolean>builder()
                .code(Response.Code.SUCCESS.getCode())
                .info(Response.Code.SUCCESS.getInfo())
                .data(true)
                .build();
    }

}
