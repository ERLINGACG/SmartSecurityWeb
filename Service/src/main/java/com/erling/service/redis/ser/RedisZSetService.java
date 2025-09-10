package com.erling.service.redis.ser;

import com.erling.utils.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisZSetService {
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisZSetService(
            @Qualifier("redisTemplate1") RedisTemplate<String, String> redisTemplate
    ){
        this.redisTemplate = redisTemplate;
    }
    /**
     * 向设备的有序集合中添加检测结果
     * @param deviceTopic 设备主题
     * @param jsonResult JSON格式的检测结果
     * @return 是否添加成功
     */
    public boolean addDetectionResult(String deviceTopic, String jsonResult) {
        try {
            long timestamp = System.currentTimeMillis();
            // 使用ZADD命令添加成员，分数为时间戳
            String key = deviceTopic+":"+timestamp;
            redisTemplate.opsForZSet().add(key, jsonResult, timestamp);
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
            return true;
        } catch (Exception e) {
            Logger.getLogger(RedisZSetService.class).error("添加检测结果到Redis失败", e);
            return false;
        }
    }
    public boolean addDetectionResult_0(String deviceTopic, String jsonResult) {
        try {
            long timestamp = System.currentTimeMillis();

            // 检查键是否存在且不是ZSet类型
            if (redisTemplate.hasKey(deviceTopic)) {
                String type = redisTemplate.type(deviceTopic).code();
                if (!"zset".equals(type)) {
                    // 删除非ZSet类型的键
                    redisTemplate.delete(deviceTopic);
                }
            }

            // 添加数据到有序集合
            redisTemplate.opsForZSet().add(deviceTopic, jsonResult, timestamp);

            // 设置过期时间（24小时）
            redisTemplate.expire(deviceTopic, 24, TimeUnit.HOURS);

            return true;
        } catch (Exception e) {
            // 简单的错误处理
            System.err.println("Redis操作失败: " + e.getMessage());
            return false;
        }
    }

}
