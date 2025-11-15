package com.erling.service.redis.ser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @SuppressWarnings("all")
    public boolean addDetectionResult(String deviceTopic, String jsonResult) {
            long timestamp = System.currentTimeMillis();
            String key = deviceTopic + ":" + timestamp;
            redisTemplate.opsForZSet().add(key, jsonResult, timestamp);
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
            return true;
    }

    public boolean addDetectionResult_0(String deviceTopic, String jsonResult) {

        long timestamp = System.currentTimeMillis();
            // 检查键是否存在且不是ZSet类型
        if (redisTemplate.hasKey(deviceTopic)) {
            String type = redisTemplate.type(deviceTopic).code();
            if (!"zset".equals(type)) {
                    redisTemplate.delete(deviceTopic);
            }
        }
        redisTemplate.opsForZSet().add(deviceTopic, jsonResult, timestamp);  // 添加数据到有序集
        redisTemplate.expire(deviceTopic, 24, TimeUnit.HOURS);   // 设置过期时间（24小时）
        return true;
    }
    /**
     * @param key Redis键
     * @param startTimestamp 开始时间戳
     * @param endTimestamp 结束时间戳
     * @return 时间范围内的数据集合
     */
    public Set<String> getReverseRangeByTimestamp(String key, long startTimestamp, long endTimestamp) {
            return  redisTemplate.opsForZSet().rangeByScore(key, startTimestamp, endTimestamp); //从小到大排序
    }

    public Set<String> getDataByKey(String key) {
            return Objects.requireNonNull(redisTemplate.opsForZSet().rangeWithScores(key, 0, -1))
                    .stream()
                    .map(typedTuple -> "%s::%s".formatted(typedTuple.getValue(), typedTuple.getScore()))
                    .collect(Collectors.toSet());
    }


}
