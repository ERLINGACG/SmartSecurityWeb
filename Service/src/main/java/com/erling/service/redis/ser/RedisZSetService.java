package com.erling.service.redis.ser;

import com.erling.utils.log.Logger;
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
    /**
     * 获取指定时间范围内的数据（降序）
     * @param key Redis键
     * @param startTimestamp 开始时间戳
     * @param endTimestamp 结束时间戳
     * @return 时间范围内的数据集合
     */
    public Set<String> getReverseRangeByTimestamp(String key, long startTimestamp, long endTimestamp) {
        try {
            String data= Objects.requireNonNull(redisTemplate.opsForZSet().reverseRangeByScore(key, startTimestamp, endTimestamp)).toString();
//            System.out.println(data);
//            return  redisTemplate.opsForZSet().reverseRangeByScore(key, startTimestamp, endTimestamp); //从大到小排序
            return  redisTemplate.opsForZSet().rangeByScore(key, startTimestamp, endTimestamp); //从小到大排序
        } catch (Exception e) {
            Logger.getLogger(RedisZSetService.class).error("获取Redis有序集合数据失败", e);
            return null;
        }
    }
    public Set<String> getDataByKey(String key) {
        try {
            return redisTemplate.opsForZSet().rangeWithScores(key, 0, -1)
                    .stream()
                    .map(typedTuple -> typedTuple.getValue() + "::" + typedTuple.getScore())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            Logger.getLogger(RedisZSetService.class).error("<UNK>Redis<UNK>", e);
            return null;
        }
    }


}
