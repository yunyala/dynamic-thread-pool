## 动态线程池

### 项目介绍

我们在借助线程池来实现多线程任务提升系统的吞吐率和响应性的时候，经常遇到问题：线程池
的参数配置难以合理评估。而不合理的线程池参数，可能导致服务器负载升高，服务不可用，内存溢出等严
重问题；一旦遇到参数不合理的问题，还需要重新上线修改，并且存在反复修改的情况。为了解决以上线程
池的痛点，开发了动态线程池项目，帮助开发人员可以提前预知线程池的问题。

### 功能描述

1. 实现自定义通用 jar 包 dynamic-thread-pool-spring-boot-starter 的开发。
2. 将动态线程池项目部署成公司通用的组件 SDK，帮助公司其他两个项目直接复用动态线程池技术。
3. 完成项目架构设计：负责管理线程池的模块+提供管理线程池的对外接口的模块+引入 SDK 测试模块。
4. 使用 Redis 实现注册中心功能，通过 Redisson 客户端将被监控服务的线程池配置上报到注册中心进行
统一管理，让各个接入 SDK 的微服务的线程池配置信息都能被快速查看。
5. 掌握 Redis 的发布订阅能力，被监控服务通过推送指定的主题，让 SDK 可以监听和修改线程池配置。
6. 使用定时任务 Spring Task 将被监控服务的线程池配置信息定时发送到注册中心。
7. 提供线程池管理端界面，可以直观查看和修改线程池核心配置参数。
8. 使用监控技术给项目负责人发出线程池达到阈值的告警信息，调整之后满足更大队列任务的执行。

### 架构介绍

1. 负责管理线程池的模块：dynamic-thread-pool-spring-boot-starter
2. 提供管理线程池的对外接口的模块：dynamic-thread-pool-admin
3. 引入 SDK 测试模块：dynamic-thread-pool-test
4. 前端管理线程池页面：front/index.html

### 写在最后但同等重要

如果你喜欢这个项目，请点个 star 吧！你的点赞是我继续更新的动力。