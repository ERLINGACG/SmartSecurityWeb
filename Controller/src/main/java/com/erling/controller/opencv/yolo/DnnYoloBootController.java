package com.erling.controller.opencv.yolo;

import com.erling.service.opencv.model.yolo.YoloBoot;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/yolo")
public class DnnYoloBootController {

    YoloBoot yoloBoot;

    DnnYoloBootController() {
        this.yoloBoot = new YoloBoot();
    }


    @RequestMapping("/v5/detection")
    public ResponseEntity<byte[]> DnnYoloDetection(@RequestParam("image") MultipartFile image) throws IOException {
        Map<String, byte[]> res = yoloBoot.DnnYoloDetection((int) image.getSize(), image.getBytes());
        for (var entry : res.entrySet()) {
            System.out.println(entry.getKey());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(res.values().iterator().next());
    }

    @RequestMapping("/v5/detection/test")
    public void DnnYoloDetectionT(@RequestParam("image") MultipartFile image) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        for (int i = 0; i < 100; i++) {
            Map<String, byte[]> result = yoloBoot.DnnYoloDetection((int) image.getSize(), image.getBytes());
            // 不要立即GC，看看内存自然增长情况
            if (i % 10 == 0) {
                long currentMemory = runtime.totalMemory() - runtime.freeMemory();
                System.out.printf("Iteration %d: Memory diff = %d bytes%n",
                        i, currentMemory - initialMemory);
            }
        }

        // 最后强制GC，看是否回落
        System.gc();
        Thread.sleep(1000);
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Final memory diff = %d bytes%n", finalMemory - initialMemory);

    }
}
