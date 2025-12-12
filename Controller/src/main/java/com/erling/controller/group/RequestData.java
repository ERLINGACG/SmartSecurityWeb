package com.erling.controller.group;

import com.erling.entity.group.GroupMember;
import lombok.Data;

@Data
public class RequestData {
    private GroupMember groupMember;  // 假设GroupMember是你已有的实体类
    private String base64;
}
