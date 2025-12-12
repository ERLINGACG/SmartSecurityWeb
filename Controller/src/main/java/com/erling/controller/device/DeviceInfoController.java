package com.erling.controller.device;

import com.erling.service.redis.ser.RedisDeviceInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device/info")
public class DeviceInfoController {

    private final RedisDeviceInfoService redisDeviceInfoService;

    public DeviceInfoController(RedisDeviceInfoService redisDeviceInfoService) {
        this.redisDeviceInfoService = redisDeviceInfoService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getDeviceStatus(@RequestParam String email) {
        System.out.println(email);
       return redisDeviceInfoService.getDeviceStatusUser(email);
    }

    @GetMapping("/urgent")
    public ResponseEntity<?> getDeviceUrgent(@RequestParam String email) {
       return redisDeviceInfoService.getDeviceUrgentUser(email);
    }

}
