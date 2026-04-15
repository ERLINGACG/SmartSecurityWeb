package com.erling.controller.opencv.edge;

import com.erling.service.opencv.edge.EdgeServiceTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//@RestController
//@RequestMapping("/edge/test")
public class EdgeControllerTest {
    EdgeServiceTest edgeService;
//    @Autowired
    public void setEdgeService(EdgeServiceTest edgeService) {
        this.edgeService = edgeService;
    }
    @PostMapping("/SodelDetection")
    public ResponseEntity<byte[]> SodelDetection(@RequestBody MultipartFile file) throws IOException {
        return ResponseEntity.
                status(200).
                contentType(MediaType.IMAGE_JPEG).
                body(edgeService.SodelDetection(file.getBytes()));
    }
}
