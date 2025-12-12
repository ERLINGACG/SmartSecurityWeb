package com.erling.service.opencv.dnn;

import com.erling.service.obj.ServiceObject;
import com.erling.service.opencv.model.ssd.SSDcaffem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

//@Service
public class FaceService extends ServiceObject {
    final SSDcaffem ssdcaffem;

    FaceService() {

        this.ssdcaffem = new SSDcaffem();

    }

    public ResponseEntity<byte[]> test() throws IOException {
        byte[] bytes1 = Files.readAllBytes(Paths.get("E:\\SmartSecurity\\javaLib\\Jopencv\\example\\test5.jpg"));
        Map<String,byte[]> map = ssdcaffem.DnnSSDcaffemDetection(bytes1.length,bytes1);
       return ResponseEntity.
               status(HttpStatus.OK).
               contentType(MediaType.IMAGE_JPEG).
               body(map.values().iterator().next());
    }
}
