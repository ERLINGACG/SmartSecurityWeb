package com.erling.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

@Configuration
public class WebMvcConfig  implements WebMvcConfigurer {
    /**
     * 配置自定义的异步任务执行器
     */
    @Bean
    public Executor asyncTaskExecutor() {
        // 使用ThreadPoolTaskExecutor作为异步执行器
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数
        executor.setCorePoolSize(5);

        // 最大线程数
        executor.setMaxPoolSize(10);

        // 队列容量
        executor.setQueueCapacity(25);

        // 线程名称前缀
        executor.setThreadNamePrefix("async-executor-");

        // 当线程池达到最大线程数时如何处理新任务
        // CALLER_RUNS：让提交任务的线程执行任务，减缓新任务的提交速度
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

        // 初始化执行器
        executor.initialize();

        return executor;
    }

    /**
     * 配置异步支持，指定使用我们自定义的执行器
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 设置异步执行器
        configurer.setTaskExecutor((AsyncTaskExecutor) asyncTaskExecutor());

        // 设置异步请求的超时时间（毫秒）
        configurer.setDefaultTimeout(30000);
    }
}
