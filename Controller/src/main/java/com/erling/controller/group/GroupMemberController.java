package com.erling.controller.group;

import com.erling.entity.group.GroupMember;
import com.erling.service.group.GroupMemberService;
import com.erling.utils.result.Result;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/group/member")
public class GroupMemberController {
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

    @GetMapping("/get/{gid}")
    public ResponseEntity<Result<?>> getGroupMembers(
            @PathVariable int gid
    ) {

        return groupMemberService.getGroupMembers(gid);
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

}

