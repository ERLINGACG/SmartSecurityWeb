package com.erling.controller.hal;

import com.erling.service.group.GroupMemberService;
import com.erling.service.opencv.dnn.CVDnnFaceService;
import com.erling.utils.result.Result;
import com.erling.utils.result.ResultEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/HAL/esp32")
public class Esp32RequestController {

    GroupMemberService groupMemberService;
    CVDnnFaceService cvdnnFaceService;
    byte[] bytes;


    public Esp32RequestController(GroupMemberService groupMemberService, CVDnnFaceService cvdnnFaceService) {
        this.groupMemberService = groupMemberService;
        this.cvdnnFaceService = cvdnnFaceService;
    }

    @PostMapping("/test")
    public ResponseEntity<Result<?>> test() {
        return ResponseEntity.ok(new Result<>(ResultEnum.SUCCESS,"测试成功"));
    }

    @PostMapping("/facial-recognition/{gid}")
    public double facialRecognition(
            @PathVariable int gid,
            @RequestBody MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return 0xffffff;
        }
        this.bytes =cvdnnFaceService.getImageForByte(file.getBytes());
        return cvdnnFaceService.getDistance(gid,file.getBytes());
    }

    @GetMapping("/looking-image")
    public ResponseEntity<byte[]> lookingImage() throws IOException {

        return ResponseEntity.status(HttpStatus.OK).
                contentType(MediaType.IMAGE_JPEG).
                body(this.bytes);
    }

}
