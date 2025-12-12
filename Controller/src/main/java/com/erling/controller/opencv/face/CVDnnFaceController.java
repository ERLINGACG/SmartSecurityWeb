package com.erling.controller.opencv.face;

import com.erling.service.group.GroupMemberService;
import com.erling.service.opencv.dnn.CVDnnFaceService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//@RestController
//@RequestMapping("/opencv/face")
public class CVDnnFaceController {
    CVDnnFaceService cvDnnFaceService;
    GroupMemberService  groupMemberService;

    public CVDnnFaceController(CVDnnFaceService cvDnnFaceService) {
        this.cvDnnFaceService = cvDnnFaceService;
    }


    @PostMapping("/verifySQL/{gid}")
    public double verifyFaceSQL(
            @RequestParam("file") MultipartFile image,
            @PathVariable("gid") int gid) throws IOException {
        return cvDnnFaceService.getDistance(gid,image.getBytes());
    }
}
