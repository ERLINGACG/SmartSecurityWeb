package com.erling.entity.device;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("device")
public class Device {

    int pid;
    String deviceName;
    @NotBlank(message = "设备主题不能为空")
    String deviceTopic;
    @NotBlank(message = "设备类型不能为空")
    String deviceType;

    String deviceDescribe;
    @NotBlank(message = "用户邮箱不能为空")
    @Email
    String userEmail;
    LocalDateTime date;
}
