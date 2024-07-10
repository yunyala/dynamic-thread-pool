package com.yunya;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// @Configurable：用于标记普通 Java 类，使其能够接受 Spring 的依赖注入：即在该类中可以直接使用@Autowired注解引入Spring容器管理的其他bean。
@Configurable
public class DynamicThreadPoolTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicThreadPoolTestApplication.class, args);
    }

}
