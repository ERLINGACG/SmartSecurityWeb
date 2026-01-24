package com.erling.controller.redis;

import com.erling.service.redis.ser.RedisDeviceConfig;
import com.erling.utils.result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/redis/device")
public class RedisDeviceConfController {
    RedisDeviceConfig  redisDeviceConfig;


    public RedisDeviceConfController(RedisDeviceConfig redisDeviceConfig) {
        this.redisDeviceConfig = redisDeviceConfig;
    }

     @GetMapping("/follow")
     public ResponseEntity<Result<?>> setDeviceFollow(@RequestParam  String topic, @RequestParam String follows){
         System.out.printf(topic);
        return ResponseEntity.ok(new Result<>(200,"设置成功",

                redisDeviceConfig.setDeviceFollow(topic,follows)
        ));
     }

      @GetMapping("/follow/get")
      public ResponseEntity<Result<?>> getDeviceFollow(@RequestParam String topic){
        return ResponseEntity.ok(new Result<>(200,"获取成功",redisDeviceConfig.getDeviceFollow(topic)));
     }
}
