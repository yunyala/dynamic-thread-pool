package com.yunya.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
// @RunWith(SpringRunner.class) 是 JUnit4 中用于指定运行器（Runner）的注解，
// 它的作用是告诉 JUnit 使用 Spring 的测试支持，以便在测试开始时自动初始化 Spring 应用上下文。
// 这两个注解在编写基于 Spring 框架的测试时非常常见，@RunWith(SpringRunner.class) 是必须的，而 @SpringBootTest 则是在 Spring Boot 应用的集成测试中常用的重要注解。
@RunWith(SpringRunner.class)
@SpringBootTest
class ApiTest {



}
