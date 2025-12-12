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

    DnnYoloBootController(){
        this.yoloBoot = new YoloBoot();
    }


    @RequestMapping("/v5/detection")
    public ResponseEntity<byte[]> DnnYoloDetection(@RequestParam("image") MultipartFile image) throws IOException {
        Map<String,byte[]> res= yoloBoot.DnnYoloDetection((int) image.getSize(), image.getBytes());
        for(var entry:res.entrySet()){
            System.out.println(entry.getKey());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(res.values().iterator().next());
    }
}
