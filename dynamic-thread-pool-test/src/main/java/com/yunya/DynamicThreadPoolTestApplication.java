package com.yunya;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;
import java.util.concurrent.ExecutorService;

@SpringBootApplication
// @Configurable：用于标记普通 Java 类，使其能够接受 Spring 的依赖注入：即在该类中可以直接使用@Autowired注解引入Spring容器管理的其他bean。
@Configurable
public class DynamicThreadPoolTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicThreadPoolTestApplication.class, args);
    }

    /**
     * 创建线程池任务
     * ApplicationRunner 是 Spring Boot 中的一个接口，用于在 Spring Boot 应用程序启动后执行特定的任务或逻辑。
     * 它允许开发者在应用程序完全启动后执行一些初始化操作，例如加载数据、调用外部服务或执行其他业务逻辑。
     */
    @Bean
    public ApplicationRunner applicationRunner(ExecutorService threadPoolExecutor01) {
        System.out.println("创建线程池任务threadPoolExecutor01:");
        System.out.println(threadPoolExecutor01);
        return args -> {
            while (true) {
                // 创建一个随机时间生成器
                Random random = new Random();
                // 随机启动时间，用于模拟任务启动延迟
                int randomInitialTime = random.nextInt(10) + 1; // 随机生成1-10之间的整数
                // 随机任务时间，用于模拟任务执行时间
                int randomTaskTime = random.nextInt(10) + 1; // 随机生成1-10之间的整数
                // 提交任务到线程池
                threadPoolExecutor01.submit(() -> {
                    try {
                        // 模拟任务启动延迟
                        Thread.sleep(randomInitialTime * 1000);
                        System.out.println("线程池任务在 " + randomInitialTime + " 秒后启动。");
                        // 模拟任务执行时间
                        Thread.sleep(randomTaskTime * 1000);
                        System.out.println("线程池任务执行了 " + randomInitialTime + " 秒。");
                    } catch (Exception exception) {
                        // 中断当前线程
                        Thread.currentThread().interrupt();
                    }
                });
            }
        };
    }

}
