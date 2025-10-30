package com.erling.entity.group;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Group {
    int gid;                      //群组ID
    @NotBlank(message = "群组名称不能为空")
    String groupName;             //群组名称
    @Email
    @NotBlank(message = "群组邮箱不能为空")
    String groupEmail;            //群组邮箱
    String groupTopic;            //群组主题
    String groupDescription;      //群组描述
    String groupVisibility;       //群组可见性
}
