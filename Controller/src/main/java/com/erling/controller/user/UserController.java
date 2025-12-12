package com.erling.controller.user;

import com.erling.entity.user.User;
import com.erling.service.user.ser.UserService;
import com.erling.utils.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user/api")
public class UserController {
    UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 登录
     * @param user 包含用户邮箱和密码的登录请求体
     * @param result 参数校验结果绑定对象
     * @return 包含认证令牌的响应实体
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid User user,
            BindingResult result
    ){
//        if(result.hasErrors()){
//            return ResponseEntity.ok(
//                    new Result<>(UserResultEnum.USER_NOT_FOUND,null
//                    )
//            );
//        }
        return userService.Login(user,result);
    }

    /**
     * 注册
     * @param user 包含邮箱、密码等信息的注册请求体
     * @param result 参数校验结果绑定对象
     * @return 注册操作结果响应
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid User user,
            @RequestParam String code,
            HttpServletRequest request,
            BindingResult result
    ){
        if(userService.checkCode(code,request.getRemoteAddr())){
            return userService.Register(user,result);
        }
        else{
            return ResponseEntity.ok(
                    new Result<>(400,
                            "验证码错误",
                            null
                    )
            );
        }

    }
    @GetMapping("/getCodeImage")
    public ResponseEntity<byte[]> getCode(HttpServletRequest request){
        return userService.getCodeImage(request);
    }

    @GetMapping("/getCodeImage/C/{code}")
    public boolean getCode(@PathVariable String code, HttpServletRequest request){
        return userService.checkCode(code,request.getRemoteAddr());
    }



    @GetMapping("/getUserDetail/{email}")
    public ResponseEntity<Result<?>> getUserDetail(@PathVariable String email){
        return userService.getUserDetail(email);
    }


    @GetMapping("/getAvatar/{uid}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable int uid){
        return userService.getAvatar(uid);
    }

    @PostMapping("/addAvatar/{uid}")
    public ResponseEntity<Result<?>> addAvatar(
            @PathVariable int uid,
            @RequestBody MultipartFile file
    ) throws IOException {
        return userService.addAvatar(uid,file.getBytes());
    }

    @PutMapping("/updateAvatar/{uid}")
    public ResponseEntity<Result<?>> updateAvatar(
            @PathVariable int uid,
            @RequestBody MultipartFile file
    ) throws IOException {
        return userService.UpdateAvatar(uid,file.getBytes());
    }

    @PutMapping("/updateNickname/{email}/{nickname}")
    public ResponseEntity<Result<?>> updateNickname(
            @PathVariable String email,
            @PathVariable String nickname
    ){
        return userService.UpdateNickname(email,nickname);
    }
}
