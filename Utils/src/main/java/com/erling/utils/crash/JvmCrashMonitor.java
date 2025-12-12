package com.erling.utils.crash;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class JvmCrashMonitor {
    @PostConstruct
    public void init() {
        // 注册JVM关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("JVM正在关闭！记录最后的状态信息...");

            // 记录线程堆栈
            logThreadStacks();

            // 记录内存状态
            logMemoryStatus();

            // 记录资源释放状态
            logResourceStatus();

            log.info("JVM关闭钩子执行完成");
        }));
    }

    private void logResourceStatus() {

    }

    private void logMemoryStatus() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            log.info("=== 内存状态 ===");
            log.info("最大内存: {} MB", maxMemory / (1024 * 1024));
            log.info("已分配内存: {} MB", totalMemory / (1024 * 1024));
            log.info("已使用内存: {} MB", usedMemory / (1024 * 1024));
            log.info("可用内存: {} MB", freeMemory / (1024 * 1024));
        } catch (Exception e) {
            log.error("记录内存状态失败: {}", e.getMessage());
        }
    }

    private void logThreadStacks() {
        try {
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            log.error("=== 当前活动线程堆栈 ===");
            for (Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
                Thread thread = entry.getKey();
                if (thread.isAlive()) {
                    log.info("线程: {} [状态: {}, 守护: {}]",
                            thread.getName(), thread.getState(), thread.isDaemon());
                    for (StackTraceElement element : entry.getValue()) {
                        log.info("    at {}", element);
                    }
                    log.info("---");
                }
            }
        } catch (Exception e) {
            log.error("记录线程堆栈失败: {}", e.getMessage());
        }
    }

}
