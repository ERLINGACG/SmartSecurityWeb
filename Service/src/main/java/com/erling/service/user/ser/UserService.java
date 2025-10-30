package com.erling.service.user.ser;

import com.erling.dao.user.UserMapper;
import com.erling.entity.user.User;
import com.erling.utils.fileU.FileUtils;
import com.erling.utils.jwt.JwtUtils;
import com.erling.utils.log.Logger;
import com.erling.utils.passworld.PasswordUtils;
import com.erling.utils.result.Result;
import com.erling.utils.result.ResultEnum;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.erling.utils.jwt.JwtUtils.EXPIRATION_MS;

@Service
public class UserService {
    UserMapper  userMapper;

    HttpServletRequest request;

    String code;
    Producer  producer;

    Map<String,String>code_Ip=new HashMap<>();
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
    public ResponseEntity<Result<?>> Login(User user) {
        User u = userMapper.getUserByEmail(user.getEmail());
        if (u != null) {
            if (PasswordUtils.VerifyPassword(user.getPasswordHash(), u.getPasswordHash())) {
                String token = JwtUtils.generateToken(user.getEmail());
                System.out.println("登录生成的token为 " + token);
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
                return ResponseEntity.
                        status(HttpStatus.UNAUTHORIZED).
                        body(
                        new Result<>(
                                UserResultEnum.USER_LOGIN_FAILED,
                                null
                        )
                );
            }
        }
        return ResponseEntity
                .status(UserResultEnum.USER_NOT_FOUND.getCode())
                .body(
                        new Result<>(
                                UserResultEnum.USER_NOT_FOUND,
                                null
                        )
                );
    }
    public ResponseEntity<Result<?>> Register(@Valid User user) {
        User u = userMapper.getUserByEmail(user.getEmail());

        if (u != null) {
            return ResponseEntity
                    .status(UserResultEnum.USER_ALREADY_EXIST.getCode())
                    .body(
                        new Result<>(
                                UserResultEnum.USER_ALREADY_EXIST,
                                null
                        )
                    );
        }
        try{
            user.setCreatedAt(LocalDateTime.now());
            user.setPasswordHash(PasswordUtils.EncodePassword(user.getPasswordHash()));
            boolean b = userMapper.insertUser(user);

            return ResponseEntity.ok(
                    new Result<>(
                            ResultEnum.REGISTER_SUCCESS,
                            b
                    )
            );

        }catch (Exception e){
            Logger.getLogger(UserService.class).error(e.getMessage());
            return ResponseEntity.ok(
                    new Result<>(
                            ResultEnum.INTERNAL_SERVER_ERROR,
                            null
                    )
            );
        }
    }
    public ResponseEntity<Result<?>> getAll(){
        return ResponseEntity.ok(
                new Result<>(
                        200,
                        "success",
                        userMapper.getAllUsers()
                )
        );
    }
    public ResponseEntity<byte[]> getCodeImage(String ip) {
        try {
            String code = producer.createText();
            code_Ip.put(ip,code);
//            System.out.println(code);
            BufferedImage image = producer.createImage(code);

            ByteArrayOutputStream bass = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", bass);
            byte[] imageBytes = bass.toByteArray();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IOException e) {
            Logger.getLogger(UserService.class).error("验证码生成失败: {}", e.getMessage());
            return ResponseEntity.
                    status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ).
                    build();
        }

    }

    public ResponseEntity<byte[]> getCodeImage() {
        try {
            code = producer.createText();
            System.out.println(code);
            BufferedImage image = producer.createImage(code);

            ByteArrayOutputStream bass = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", bass);
            byte[] imageBytes = bass.toByteArray();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IOException e) {
            Logger.getLogger(UserService.class).error("验证码生成失败: {}", e.getMessage());
            return ResponseEntity.
                    status(
                    HttpStatus.INTERNAL_SERVER_ERROR
                    ).
                    build();
        }

    }
    public ResponseEntity<Result<?>> checkToken(String token) {
        if(JwtUtils.isTokenExpired(token)){
            return ResponseEntity.ok(
                    new Result<>(
                            200,
                            "token验证成功",
                            null
                    )
            );
        }
        else{
            return ResponseEntity.ok(
                    new Result<>(
                            400,
                            "token验证失败",
                            null
                    )
            );
        }
    }
    public ResponseEntity<Result<?>> checkToken(HttpServletRequest request) {
//        String token = request.getHeader("Authorization");
        String token = request.getCookies()[0].getValue();
        System.out.println(token);
        if(token == null){
            return ResponseEntity.ok(
                    new Result<>(
                            400,
                            "token为空",
                            null
                    )
            );
        }
        return checkToken(token);
    }
    public boolean checkCode(String inputCode) {
        return Objects.equals(inputCode, code);
    }
    public boolean checkCode(String inputCode,String ip) {
        for(Map.Entry<String,String> entry:code_Ip.entrySet()){
            Logger.getLogger(UserService.class).info("ip:{},验证码{}",entry.getKey(),entry.getValue());
            if(entry.getKey().equals(ip) && entry.getValue().equals(inputCode)){

                return true;
            }
        }
        return false;
    }

    public ResponseEntity<Result<?>> getUserDetail(String email){
        User u = userMapper.getUserByEmailNotPwd(email);
        return ResponseEntity.ok(
                new Result<>(
                        ResultEnum.SUCCESS,
                        u
                )
        );
    }
    public ResponseEntity<byte[]> getAvatar(int uid) {
        try {
            String avatarPath = userMapper.getUserAvatarByUid(uid);
            byte[] bytes = FileUtils.readFile(avatarPath);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(bytes);
        } catch (IOException e) {
            Logger.getLogger(UserService.class).error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    public ResponseEntity<Result<?>> addAvatar(int uid, byte[] avatar) {
        String savePath="E:\\SmartSecurity\\user";
        String fileName=uid+".jpg";
        try {
            FileUtils.saveFile(avatar,savePath,fileName);
        } catch (IOException e) {
            Logger.getLogger(UserService.class).error(e.getMessage());
            return ResponseEntity.ok(
                    new Result<>(
                            ResultEnum.INTERNAL_SERVER_ERROR,
                            null
                    )
            );
        }
        User user = new User();
        user.setUid(uid);
        user.setAvatarPath(savePath+"\\"+fileName);
        boolean b = userMapper.addAvatar(user);
        return ResponseEntity.ok(
                new Result<>(
                        ResultEnum.SUCCESS,
                        b
                )
        );
    }
    public ResponseEntity<Result<?>> UpdateAvatar(int uid, byte[] avatar) {

        String oldAvatarPath = userMapper.getUserAvatarByUid(uid);
        String savePath="E:\\SmartSecurity\\user";
        String fileName=uid+".jpg";
        try {
            FileUtils.deleteFile(oldAvatarPath);
            FileUtils.saveFile(avatar,savePath,fileName);
            return ResponseEntity.ok(
                    new Result<>(
                            ServiceResultEnum.SUCCESS,
                            true
                    )
            );
        } catch (IOException e) {
            Logger.getLogger(UserService.class).error(e.getMessage());
            return ResponseEntity.ok(
                    new Result<>(
                            ResultEnum.INTERNAL_SERVER_ERROR,e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<Result<?>> UpdateNickname(String email, String nickname) {
        boolean b = userMapper.updateNickname(nickname, email);
        if(!b){
                return ResponseEntity.ok(
                        new Result<>(
                                ServiceResultEnum.FAILURE,
                                false
                        )
                );
        }
        return ResponseEntity.ok(
                new Result<>(
                        ServiceResultEnum.SUCCESS,
                        true
                )
        );
    }

}
