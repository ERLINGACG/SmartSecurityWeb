package com.erling.controller.opencv.yolo;

import com.erling.service.opencv.dnn.YoloDnn;
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

    private final YoloDnn yoloDnn;

    public DnnDetectorControllerTest() {
        this.yoloDnn = new YoloDnn();
    }

    @PostMapping("/detect")
    public Callable<ResponseEntity<byte[]>> detect(@RequestParam("image") MultipartFile image) {
        // 注意：这里的代码仍在 Servlet 容器线程中执行，用于接收参数和构建 Callable 对象
        return () -> { // 这个 lambda 表达式内部的代码将在异步任务线程中执行
            Map<String, byte[]> result = yoloDnn.TEST_D3(image.getBytes());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(result.values().iterator().next());
        };
    }
}
