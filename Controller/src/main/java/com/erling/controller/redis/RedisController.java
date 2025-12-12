package com.erling.controller.redis;

import com.erling.service.json.JsonService;
import com.erling.service.redis.ser.RedisZSetService;
import com.erling.utils.result.Result;
import com.erling.utils.result.ResultEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/redis")
public class RedisController {
    private final RedisZSetService redisZSetService;
    private final JsonService jsonService;


    public RedisController(RedisZSetService redisZSetService, JsonService jsonService) {
        this.redisZSetService = redisZSetService;
        this.jsonService = jsonService;
    }

    @GetMapping("/pdata/")
    public ResponseEntity<Result<?>> getPdata(@RequestParam String topic){
        System.out.println(topic);
        long endTime_ = System.currentTimeMillis();
        long startTime_ = endTime_-3600000*2;
        Set<String> strings = redisZSetService.getReverseRangeByTimestamp(topic,
//                1765201146470L, 1765202805854L);
                startTime_, endTime_);
//        System.out.println(strings);D
        String json = jsonService.ToJsonOutput(strings.toString());
//        System.out.println(json);

        return ResponseEntity.ok(
                new Result<>(
                        ResultEnum.SUCCESS,json
                )
        );

    }
}
