package com.erling.service.redis.ser;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class RedisDeviceConfig {

    private final RedisTemplate<String, String> redisTemplate;

    RedisDeviceConfig(@Qualifier("redisTemplate3") RedisTemplate<String, String> redisTemplate
    ){
        this.redisTemplate = redisTemplate;
    }

     public boolean setDeviceFollow(String deviceId, String followIds){
        if(!Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(deviceId, followIds))){
            redisTemplate.opsForValue().set(deviceId, followIds);
        }
        return  true;
     }

    public List<String> getDeviceFollow(String deviceId){
        // 检查 deviceId 是否存在
        if (!redisTemplate.hasKey(deviceId)) {
            return List.of(); // 如果不存在，返回空列表
        }
        return Arrays.asList(Objects.requireNonNull(redisTemplate.opsForValue().get(deviceId)).split(","));
    }
}
