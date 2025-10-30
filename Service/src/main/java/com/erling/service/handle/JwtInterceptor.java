package com.erling.service.handle;

import com.erling.utils.jwt.JwtUtils;
import com.erling.utils.log.Logger;
import com.erling.utils.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/user/api/login",
            "/user/api/register",
            "/user/api/getCodeImage",
            "/HAL/esp32/",
            "/ai/deepseek/ai/chat/historyTest2"
    );


    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler
    ) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 排除登录/注册等无需验证的接口
        boolean shouldExclude = EXCLUDE_PATHS.stream()
                .anyMatch(path -> request.getRequestURI().contains(path));

        if (shouldExclude) {
            return true;
        }


        if (getToHeard(request)) {
            return true;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        objectMapper.writeValue(
                response.getWriter(),
                new Result<>(401, "身份验证失败", null)
        );
        return false;
    }


    public boolean getToCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        System.out.println("cookies = " + Arrays.toString(cookies));
        String token = Optional.ofNullable(cookies) // 过滤空数组
                .map(Arrays::stream)                // 转换为Stream流
                .flatMap(stream ->  // 过滤掉非jwt_token的cookie
                        stream.filter(c -> "jwt_token".
                                        equals(c.getName())
                                ).
                                findFirst())
                .map(Cookie::getValue)                   // 获取token值
                .orElse(null);                     // 若无token，则返回null

        return token != null && JwtUtils.isTokenExpired(token);
    }
    public boolean getToHeard(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 去掉"Bearer "前缀
        }
        if (token != null && !JwtUtils.isTokenExpired(token)) {
            Logger.getLogger(this.getClass()).info("token未过期:{}",token);
            return true;
        } else if (token == null) {
            Logger.getLogger(this.getClass()).info("Authorization头中无token,来自:{}",request.getRequestURI());
            return false;
        } else{
            Logger.getLogger(this.getClass()).info("token已过期:{}",token);
            return false;
        }
    }
}
