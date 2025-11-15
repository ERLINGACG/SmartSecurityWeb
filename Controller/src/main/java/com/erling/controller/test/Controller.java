package com.erling.controller.test;

import com.erling.service.user.ser.UserService;
import com.erling.utils.jwt.JwtUtils;
import com.erling.utils.result.Result;
import com.erling.utils.tomls.ReadToml;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/test")
public class Controller {
    UserService userService;SessionRegistry sessionRegistry;
    @Autowired
    public Controller(UserService userService, SessionRegistry sessionRegistry) {
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello(HttpServletRequest request) {
        String SessionId = request.getSession().getId();
        SessionInformation sessionInfo = sessionRegistry.getSessionInformation(SessionId);
        if (sessionInfo != null && !sessionInfo.isExpired()) {
            return ResponseEntity.ok(
                    new Result<>(200, "有效JSESSIONID", true)
            );
        }
        return ResponseEntity.ok(
                new Result<>(401, "无效JSESSIONID", false)
        );
    }
    @GetMapping("/hello2")
    public ResponseEntity<?> hello2(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        System.out.println(token);
        try{
        if (JwtUtils.isTokenExpired(token)) {
            return ResponseEntity.ok(
                    new Result<>(401, "无效token", false)
            );

        }else {
            return ResponseEntity.ok(
                    new Result<>(200, "有效token", true)
            );
        }}catch (Exception e){
            return ResponseEntity.ok(
                    new Result<>(401, "无效token", false)
            );
        }
    }
    @GetMapping("/hello3")
    public ResponseEntity<?> hello3(HttpServletRequest request) {
        return ResponseEntity.ok(
                new Result<>(200, "helloVue", true)
        );
    }
    @GetMapping("/hello4")
    public ResponseEntity<?> hello4(HttpServletRequest request) {
        return ResponseEntity.ok(
                new Result<>(200, "helloVue", true)
        );
    }

    @GetMapping("toml")
    public String toml() {
        return ReadToml.TomlString("/config/test.toml","Title") ;
    }

}
