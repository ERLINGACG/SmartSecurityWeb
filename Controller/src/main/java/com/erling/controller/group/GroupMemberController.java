package com.erling.controller.group;

import com.erling.entity.group.GroupMember;
import com.erling.service.group.GroupMemberService;
import com.erling.utils.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;


@RestController
@RequestMapping("/group/member")
public class GroupMemberController {

    byte[] image;
    GroupMemberService groupMemberService;

    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    @PostMapping("/add")
    public ResponseEntity<Result<?>> addGroupMember(
            @Valid @RequestPart GroupMember groupMember,
            @RequestPart MultipartFile file
    ) throws IOException {

            return groupMemberService.addGroupMember(groupMember, file.getBytes());
    }

//    @Data
//    class RequestData{
//
//    }

    @PostMapping("/addB64")
    public ResponseEntity<Result<?>> simplestTest(@RequestBody RequestData requestData) throws IOException {
//        System.out.println("收到原始数据: " + requestData);
//        System.out.println("收到原始数据: " + requestData.getGroupMember());
        System.out.println("收到原始数据: " + requestData.getBase64());
        byte[] imageBytes = Base64.getDecoder().decode(requestData.getBase64());
//        Files.write(Path.of("./test.jpg"), imageBytes);
        return groupMemberService.addGroupMember(requestData.getGroupMember(), imageBytes);
    }

    @GetMapping("/get/{gid}")
    public ResponseEntity<Result<?>> getGroupMembers(
            @PathVariable int gid
    ) {

        return groupMemberService.getGroupMembers(gid);
    }

    @GetMapping("/getAll")
    public ResponseEntity<Result<?>> getAllGroupMembers(@RequestParam String email) {

        return groupMemberService.getGroupMembersEmail(email);
    }



    @PutMapping("/updateNoFeatures")
    public ResponseEntity<Result<?>> updateGroupMemberNoFeatures(
            @RequestBody GroupMember groupMember
    ) {

        return groupMemberService.updateGroupMemberNoFeatures(groupMember);
    }

    @PutMapping("/update")
    public ResponseEntity<Result<?>> updateGroupMember(
            @RequestPart GroupMember groupMember,
            @RequestPart MultipartFile file
    ) throws IOException {
        return groupMemberService.updateGroupMember(groupMember, file);
    }

    @DeleteMapping("/delete/{gid}/{mid}")
    public ResponseEntity<Result<?>> deleteGroupMember(@PathVariable int gid,@PathVariable int mid) {
        return groupMemberService.deleteGroupMember(gid,mid);
    }


    @Deprecated
    @PostMapping("/verify/{id}")
    public ResponseEntity<Result<?>> verifyGroupMember(
            @PathVariable int id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return groupMemberService.verifyGroupMemberMysql(id, file.getBytes());
    }


    @PostMapping("/verifyDnnSSDcaffem/")
    public ResponseEntity<Result<?>> verifyGroupMemberDnnSSDcaffem(
            @RequestParam String topic,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return groupMemberService.verifyGroupMemberDnnSSDcaffem(topic, file.getBytes());
//        return groupMemberService.verifyGroupMemberMysql(id, file.getBytes());
    }

    @PostMapping("/verifyDnnSSDcaffem/path")
    public ResponseEntity<Result<?>> verifyGroupMemberDnnSSDcaffem_(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        String topic = request.getHeader("topic");
        System.out.println(topic);
        System.out.println(file.getBytes().length);
//        return ResponseEntity.ok().body(new Result<>(200,"验证成功",null));
//        System.out.println(topic);
        image = file.getBytes();
        return groupMemberService.verifyGroupMemberDnnSSDcaffem(topic, file.getBytes());
//        return groupMemberService.verifyGroupMemberMysql(id, file.getBytes());
    }

    @GetMapping("/image")
    public ResponseEntity<?> getImage() {
        return ResponseEntity.status(HttpStatus.OK).
                contentType(MediaType.IMAGE_JPEG).
                body(this.image);
    }
    Logger logger= LoggerFactory.getLogger(GroupMemberController.class);

    @RequestMapping("/face/maker/test")
    public void MakeTest(@RequestParam("file") MultipartFile file) throws IOException {
        for(int i = 0; i < 40; i++){
            long start = System.currentTimeMillis();
            groupMemberService.MakeTest(file);
            long end = System.currentTimeMillis();
            logger.info("MakeTest: " + (end - start) + "ms");
        }
    }

}

