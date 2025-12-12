package com.erling.controller.detect;

import com.erling.service.detectHistroy.DetectionHistoryService;
import com.erling.utils.result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DetectHistoryController {

    private final DetectionHistoryService detectionHistoryService;

    public DetectHistoryController(DetectionHistoryService detectionHistoryService) {
        this.detectionHistoryService = detectionHistoryService;
    }


    @GetMapping("/detect/history/all")
    public ResponseEntity<Result<?>> getAll(@RequestParam String topic){
        return detectionHistoryService.getDetectionHistoryByTopic(topic);
    }

    @GetMapping("/detect/history/image")
    public ResponseEntity<byte[]> getImage(@RequestParam("topic") String topic,
                                           @RequestParam("path") String path
    ){
        return detectionHistoryService.getDetectionHistoryByPath(topic, path);
    }

    @GetMapping("/detect/history/between")
    public ResponseEntity<Result<?>> getBetween(@RequestParam String topic,
                                                @RequestParam String start,
                                                @RequestParam String end
    ){
        return detectionHistoryService.getDetectionHistoryByDateBetween(topic,start,end);
    }
}
