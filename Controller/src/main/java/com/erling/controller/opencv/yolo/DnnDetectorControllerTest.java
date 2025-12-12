package com.erling.controller.opencv.yolo;

import com.erling.service.opencv.model.yolo.YoloV5;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/yolo/test")
public class DnnDetectorControllerTest {

    private final YoloV5 yoloV5;

    public DnnDetectorControllerTest() {
        this.yoloV5 = new YoloV5();
    }

    @PostMapping("/detect/v5")
    public Callable<ResponseEntity<byte[]>> detect(@RequestParam("image") MultipartFile image) {

        return () -> { // 这个 lambda 表达式内部的代码将在异步任务线程中执行
            Map<String, byte[]> result = yoloV5.DnnYoloV5Detection(image.getBytes().length, image.getBytes());
            System.out.println(result.keySet().iterator().next());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(result.values().iterator().next());
        };
    }
}
