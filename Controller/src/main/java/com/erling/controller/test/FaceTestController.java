package com.erling.controller.test;

import com.erling.service.opencv.dnn.FaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

//@RestController
//@RequestMapping("/test/face")
public class FaceTestController {

    FaceService  faceService;

     public FaceTestController(FaceService faceService) {
        this.faceService = faceService;
    }

     @RequestMapping("/getImage")
     public ResponseEntity<byte[]> test() throws IOException {
        return faceService.test();
     }
}
