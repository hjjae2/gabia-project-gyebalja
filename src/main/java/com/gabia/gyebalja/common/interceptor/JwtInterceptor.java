package com.gabia.gyebalja.common.interceptor;

import com.gabia.gyebalja.common.CookieBox;
import com.gabia.gyebalja.exception.UnauthorizedException;
import com.gabia.gyebalja.service.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Environment env;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (env.getProperty("spring.profiles.active.test").equals("true")) return true;

        if(request.getMethod().equals(HttpMethod.OPTIONS.name())) return true;

        CookieBox cookieBox = new CookieBox(request);
        String token = null;
        if(cookieBox.exists("jwt_token")) {
           token = cookieBox.getValue("jwt_token");
        }

        if(token != null && jwtService.isUsable(token)){
            return true;
        }else{
            throw new UnauthorizedException();
        }

    }
}