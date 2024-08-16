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
     *
     * ExecutorService threadPoolExecutor01 参数讲解：
     * ExecutorService 是 Java 中一个用于管理和调度线程池的接口，它提供了一组方法来管理和调度并发任务的执行。
     * threadPoolExecutor01 是一个实例化的 ExecutorService 对象，通常通过工厂方法或者构造函数来创建并初始化。
     * 在实际应用中，ExecutorService threadPoolExecutor01 的作用可以总结如下：
     * 任务提交：可以将实现了 Runnable 或 Callable 接口的任务提交给 ExecutorService，它将负责在一个线程中执行任务。
     * 任务管理：通过 Future 接口，可以管理提交任务的执行状态。您可以取消任务、获取任务的结果或检查任务是否已经完成。
     * 线程管理：ExecutorService 管理线程的创建、调度和销毁。通过使用线程池，减少了线程创建和销毁所带来的开销，从而提高系统性能。
     * 并发控制：通过限制线程池的最大线程数，ExecutorService 可以控制同时执行的任务数量，避免线程过多导致的系统资源耗尽。
     * 任务调度：一些实现如 ScheduledExecutorService 提供了任务调度功能，可以在指定的时间延迟或周期性地执行任务。
     *
     * ExecutorService 提供了一系列方法用于管理任务和线程池：
     * 1、execute(Runnable task)：执行一个没有返回值的任务。
     * 2、submit(Runnable task)：提交一个任务，并返回一个 Future 对象，用于获取任务的执行状态或结果。
     * 3、submit(Callable<T> task)：提交一个任务，并返回一个 Future 对象，任务可以返回一个结果。
     * 4、shutdown()：有序地关闭线程池，在这个方法调用后，不再接受新任务。
     * 5、shutdownNow()：立即关闭线程池，尝试中断正在执行的任务并清除等待队列中的任务。
     * 6、isShutdown()：判断线程池是否已经关闭。
     * 7、isTerminated()：判断线程池是否已经完成关闭并不再有任何任务在执行。
     *
     * ExecutorService 通常通过以下几种方式创建：
     * 1、使用 Executors 工厂类：ExecutorService threadPoolExecutor01 = Executors.newFixedThreadPool(10);
     * 2、自定义线程池：
     * ExecutorService threadPoolExecutor01 = new ThreadPoolExecutor(
     *     5, // corePoolSize
     *     10, // maximumPoolSize
     *     60L, // keepAliveTime
     *     TimeUnit.SECONDS, // keepAliveTime's time unit
     *     new LinkedBlockingQueue<Runnable>() // workQueue
     * );
     *
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
