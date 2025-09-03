package com.erling.controller.opencv.yolo;

import com.erling.service.opencv.dnn.DnnDetectorServiceTest;
import com.erling.service.opencv.dnn.YoloDnnTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/yolo/test")
public class DnnDetectorControllerTest {
    DnnDetectorServiceTest dnnDetectorServiceTest;
    @Autowired
    public DnnDetectorControllerTest(DnnDetectorServiceTest dnnDetectorServiceTest) {
        this.dnnDetectorServiceTest = dnnDetectorServiceTest;
    }

//    @PostMapping("/detect")
//    public ResponseEntity<byte[]> detect(@RequestParam("image") MultipartFile image) throws IOException {
//        byte[] bytes = dnnDetectorServiceTest.detectTest(image.getBytes());
//        return ResponseEntity.
//                status(HttpStatus.OK).
//                contentType(MediaType.IMAGE_JPEG).
//                body(bytes);
//    }
    @PostMapping("/detect")
    @Async // 显式声明此方法为异步执行
    public Callable<ResponseEntity<byte[]>> detect(@RequestParam("image") MultipartFile image) throws IOException {
        // 注意：这里的代码仍在 Servlet 容器线程中执行，用于接收参数和构建 Callable 对象
        return () -> { // 这个 lambda 表达式内部的代码将在异步任务线程中执行
            byte[] bytes = YoloDnnTest.TEST_D(image.getBytes(),image.getBytes().length);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        };
    }
}
