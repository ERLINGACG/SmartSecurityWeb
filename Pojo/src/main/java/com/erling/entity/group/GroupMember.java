package com.erling.entity.group;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMember {
    int mid;                     //成员id
    int groupId;                 //群组id
    @NotBlank(message = "成员名称不能为空")
    String memberName;           //成员名称
    @Email
    @NotBlank(message = "成员邮箱不能为空")
    String memberEmail;          //成员邮箱

    @NotBlank(message = "成员身份不能为空")
    String memberIdentity;       //成员身份

    @NotBlank(message = "成员性别不能为空")
    String memberGender;         //成员性别
    String memberDescription;    //成员描述
    byte[] memberFeature;        //成员特征
    LocalDateTime updateTime;    //更新时间
}
