package com.erling.controller.group;

import com.erling.entity.group.Group;
import com.erling.service.group.GroupService;
import com.erling.utils.result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
public class GroupController {
        GroupService groupService;

        public GroupController(GroupService groupService) {
        this.groupService = groupService;
     }

        @PostMapping("/add")
        public ResponseEntity<Result<?>> addGroup(@RequestBody Group group) {
             return groupService.addGroup(group);
         }

        @DeleteMapping("/delete/{gid}")
        public ResponseEntity<Result<?>> deleteGroup(@PathVariable int gid, @RequestHeader("Group-Email") String email) {
            return groupService.deleteGroup(gid, email);
        }

        @PutMapping("/update")
        public ResponseEntity<Result<?>> updateGroup(@RequestBody Group group) {
             return groupService.updateGroup(group);
        }

        @GetMapping("/get")
        public ResponseEntity<Result<?>> getGroup(@RequestParam String groupName, @RequestHeader("Group-Email") String email) {
             return groupService.getGroup(groupName, email);
        }

        @GetMapping("/getAll")
        public ResponseEntity<Result<?>> getAllGroups(@RequestHeader("Group-Email") String email) {
             return groupService.getGroups(email);
        }
}
