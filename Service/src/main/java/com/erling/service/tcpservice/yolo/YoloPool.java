package com.erling.service.tcpservice.yolo;

import com.erling.service.opencv.dnn.YoloDnn;
import com.erling.utils.log.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

//@Component
public class YoloPool {

    private final int maxPoolSize=5;
    private final int minPoolSize=5;
    private final BlockingQueue<YoloDnn> availableInstances;
    private final Set<YoloDnn> allInstances;
    private final AtomicInteger createdCount = new AtomicInteger(0);
    public  YoloPool(){
        this.availableInstances = new LinkedBlockingQueue<>(maxPoolSize);
        this.allInstances = new HashSet<>(maxPoolSize);

        // 预先创建最小数量的实例
        for (int i = 0; i < minPoolSize; i++) {
            YoloDnn instance = createNewInstance();
            if (instance != null) {
                availableInstances.offer(instance);
            }
        }
        Logger.getLogger(YoloPool.class).info("YOLO实例池初始化完成，当前大小: {}/{}", availableInstances.size(), maxPoolSize);
    }

    private YoloDnn createNewInstance() {
        if (createdCount.get() >= maxPoolSize) {
            return null;
        }
        try {
            YoloDnn instance = new YoloDnn();
            allInstances.add(instance);
            createdCount.incrementAndGet();
            Logger.getLogger(YoloDnn.class).info("创建新的YOLO实例，当前总数: {}", allInstances.size());
            return instance;
        } catch (Exception e) {
            Logger.getLogger(YoloDnn.class).error("创建YOLO实例失败", e);
            return null;
        }
    }
}
