package com.erling.service.redis.ser;

import com.erling.dao.device.DeviceMapper;
import com.erling.entity.device.Device;
import com.erling.utils.result.Result;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisDeviceInfoService {

    private final RedisTemplate<String, String> redisTemplate;


    private final DeviceMapper deviceMapper;

    RedisDeviceInfoService(
            @Qualifier("redisTemplate2") RedisTemplate<String, String> redisTemplate, DeviceMapper deviceMapper) {
        this.redisTemplate = redisTemplate;
        this.deviceMapper = deviceMapper;
    }


    public boolean setDeviceStatus(String deviceTopic, boolean isOnline) {
        String key = deviceTopic + ":status";
        redisTemplate.opsForValue().set(key, isOnline ? "ONLINE" : "OFFLINE");
        return true;
    }
    public boolean setDeviceUrgent(String deviceTopic, boolean isUrgent) {
        String key = deviceTopic + ":urgent";
        redisTemplate.opsForValue().set(key, isUrgent ? "URGENT" : "NORMAL", 10, TimeUnit.SECONDS);
        return true;
    }

     public boolean getDeviceStatus(String deviceTopic) {
        String key = deviceTopic + ":status";
        String status = redisTemplate.opsForValue().get(key);
        return "ONLINE".equals(status);
    }

    public boolean getDeviceUrgent(String deviceTopic) {
        String key = deviceTopic + ":urgent";
        String status = redisTemplate.opsForValue().get(key);
        return "URGENT".equals(status);
    }


    public ResponseEntity<?> getDeviceStatusUser(String email) {
        List<Device> devices = deviceMapper.getDevicesByEmail(email);
        int onlineCount = 0;
        for(Device device : devices){
            String key = device.getDeviceTopic();
            boolean isOnline = this.getDeviceStatus(key);
            if(isOnline){
                onlineCount++;
            }
        }
        return ResponseEntity
                .ok(new Result<>(
                        200,
                        "查询成功",
                        onlineCount
            )
        );
    }

    public ResponseEntity<?> getDeviceUrgentUser(String email) {
        List<Device> devices = deviceMapper.getDevicesByEmail(email);
        int urgentCount = 0;

//        Map<Integer, List<String>> result = new HashMap<>();
        List<String> urgentDevices =new ArrayList<>();

        for(Device device : devices){
            String key = device.getDeviceTopic();
            boolean isUrgent = this.getDeviceUrgent(key);
            if(isUrgent){
                urgentCount++;
                urgentDevices.add(device.getDeviceTopic());
            }
        }

//        result.put(urgentCount, urgentDevices);
        return ResponseEntity
                .ok(new Result<>(
                        200,
                        "查询成功",
                        urgentDevices
                )
        );
    }


}
