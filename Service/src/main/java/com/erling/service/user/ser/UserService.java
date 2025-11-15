package com.erling.service.user.ser;

import com.erling.dao.user.UserMapper;
import com.erling.entity.user.User;
import com.erling.service.exception.exc.UserBusinessException;
import com.erling.service.obj.ServiceObject;
import com.erling.utils.fileU.FileUtils;
import com.erling.utils.jwt.JwtUtils;
import com.erling.utils.log.Logger;
import com.erling.utils.passworld.PasswordUtils;
import com.erling.utils.result.Result;
import com.erling.utils.result.ren.ServiceResultEnum;
import com.erling.utils.result.ren.UserResultEnum;
import com.google.code.kaptcha.Producer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.erling.utils.jwt.JwtUtils.EXPIRATION_MS;

@Service
public class UserService extends ServiceObject {
    UserMapper  userMapper;

    HttpServletRequest request;

    Map<String,String> ip_code=new HashMap<>();
    Producer  producer;

    @Autowired
    public UserService(
            UserMapper userMapper,
            HttpServletRequest request,
            Producer producer
    ) {
        this.userMapper = userMapper;
        this.request = request;
        this.producer = producer;

    }
    public ResponseEntity<Result<?>> Login(User user, BindingResult result) {
        validate(result);
        User u = userMapper.getUserByEmail(user.getEmail());
        if (u != null) {
            if (PasswordUtils.VerifyPassword(user.getPasswordHash(), u.getPasswordHash())) {
                String token = JwtUtils.generateToken(user.getEmail());
                log.info("登录生成的token为 {}", token);
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE,
                                String.format("jwt_token=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=Strict",
                                        token,
                                        EXPIRATION_MS/1000))
                        .body(new Result<>(
                                UserResultEnum.USER_LOGIN_SUCCESS,
                                token
                            )
                        );
            } else {
                throw new UserBusinessException(UserResultEnum.USER_LOGIN_FAILED);
            }
        }
        throw new UserBusinessException(UserResultEnum.USER_NOT_FOUND);
    }
    public ResponseEntity<Result<?>> Register(@Valid User user, BindingResult result) {
        validate(result);
        User u = userMapper.getUserByEmail(user.getEmail());
        if (u != null) {
            throw new UserBusinessException(UserResultEnum.USER_ALREADY_EXIST);
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setPasswordHash(PasswordUtils.EncodePassword(user.getPasswordHash()));
        boolean b = userMapper.insertUser(user);
        return ResponseEntity.ok(
                    new Result<>(
                            UserResultEnum.USER_REGISTER_SUCCESS,
                            b
                    )
            );
    }



    public ResponseEntity<byte[]> getCodeImage(HttpServletRequest request) {
        try {
            String c = producer.createText();
            log.info("验证码为 {},ip为 {}", c,request.getRemoteAddr());
            BufferedImage image = producer.createImage(c);
            ip_code.put(request.getRemoteAddr(),c);

            ByteArrayOutputStream bass = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", bass);
            byte[] imageBytes = bass.toByteArray();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IOException e) {
            throw new UserBusinessException(ServiceResultEnum.FAILURE);
        }
    }


    public boolean checkCode(String inputCode,String ip) {
        for(var entry:ip_code.entrySet()){
            Logger.getLogger(UserService.class).info("ip:{},验证码{}",entry.getKey(),entry.getValue());
            if(entry.getKey().equals(ip) && entry.getValue().equals(inputCode)){
                ip_code.remove(ip);
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<Result<?>> getUserDetail(String email){
        User u = userMapper.getUserByEmailNotPwd(email);
        return ResponseEntity.ok(
                new Result<>(
                        UserResultEnum.USER_DETAIL_SUCCESS,
                        u
                )
        );
    }
    public ResponseEntity<byte[]> getAvatar(int uid) {
        try {
            String avatarPath = userMapper.getUserAvatarByUid(uid);
            byte[] bytes;
            bytes = FileUtils.readFile(Objects.requireNonNullElse(avatarPath, "E:\\SmartSecurity\\user\\default.png"));
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(bytes);
        } catch (IOException e) {
            throw new UserBusinessException(UserResultEnum.USER_GET_AVATAR_FAILED);
        }
    }
    public ResponseEntity<Result<?>> addAvatar(int uid, byte[] avatar) {
        String savePath="E:\\SmartSecurity\\user";
        String fileName= MessageFormat.format("{0}.jpg", uid);
        try {
            FileUtils.saveFile(avatar,savePath,fileName);
        } catch (IOException e) {
           throw new UserBusinessException(ServiceResultEnum.FAILURE);
        }
        User user = new User();
        user.setUid(uid);
        user.setAvatarPath(MessageFormat.format("{0}\\{1}", savePath, fileName));
        boolean b = userMapper.addAvatar(user);
        return ResponseEntity.ok(
                new Result<>(
                        ServiceResultEnum.SUCCESS,
                        b
                )
        );
    }
    public ResponseEntity<Result<?>> UpdateAvatar(int uid, byte[] avatar) {

        String oldAvatarPath = userMapper.getUserAvatarByUid(uid);
        String savePath="E:\\SmartSecurity\\user";
        String fileName= MessageFormat.format("{0}.jpg", uid);
        try {
            if (FileUtils.isImage(oldAvatarPath)) {
                FileUtils.deleteFile(oldAvatarPath);
            }
            User newUser = new User();
            newUser.setUid(uid);
            newUser.setAvatarPath(MessageFormat.format("{0}\\{1}", savePath, fileName));
            boolean b = userMapper.addAvatar(newUser);
            FileUtils.saveFile(avatar,savePath,fileName);
            log.info("更新用户{}的头像为{},是否成功:{}",uid, MessageFormat.format("{0}\\{1}", savePath, fileName),b);
            return ResponseEntity.ok(
                    new Result<>(
                            ServiceResultEnum.SUCCESS,
                            b
                    )
            );
        } catch (IOException e) {
            throw new UserBusinessException(ServiceResultEnum.FAILURE);
        }
    }

    public ResponseEntity<Result<?>> UpdateNickname(String email, String nickname) {
        boolean b = userMapper.updateNickname(nickname, email);
        if(!b){
            throw new UserBusinessException(UserResultEnum.USER_NOT_FOUND);
        }
        return ResponseEntity.ok(
                new Result<>(
                        ServiceResultEnum.SUCCESS,
                        true
                )
        );
    }

}
