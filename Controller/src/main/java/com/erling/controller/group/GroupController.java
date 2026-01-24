package com.erling.controller.group;

import com.erling.entity.group.Group;
import com.erling.service.group.GroupService;
import com.erling.utils.result.Result;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
public class GroupController {
        GroupService groupService;

        public GroupController(GroupService groupService) {
        this.groupService = groupService;
     }

        @PostMapping("/add")
        public ResponseEntity<Result<?>> addGroup(@Valid @RequestBody Group group, BindingResult result) {
             return groupService.addGroup(group, result);
         }

        @DeleteMapping("/delete/{gid}")
        public ResponseEntity<Result<?>> deleteGroup(@PathVariable int gid, @RequestHeader("Group-Email") String email) {
            return groupService.deleteGroup(gid, email);
        }

        @PutMapping("/update")
        public ResponseEntity<Result<?>> updateGroup(@Valid @RequestBody Group group, BindingResult result) {
             return groupService.updateGroup(group, result);
        }

        @GetMapping("/get")
        public ResponseEntity<Result<?>> getGroup(@RequestParam String groupName, @RequestHeader("Group-Email") String email) {
             return groupService.getGroup(groupName, email);
        }

        @GetMapping("/getAll")
        public ResponseEntity<Result<?>> getAllGroups(@RequestHeader("Group-Email") String email) {
             System.out.println(email);
             return groupService.getGroups(email);
        }

}
