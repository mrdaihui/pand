package com.hui.pand.security.handler;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hui.pand.context.RequestWrapper;
import com.hui.pand.models.R;
import com.hui.pand.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author daihui
 * @date 2020/5/21 16:06
 */
@Slf4j
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = (String) request.getAttribute("username");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        log.info(exception.getMessage());
        if (exception instanceof LockedException) {
            out.write(new ObjectMapper().writeValueAsString(R.ok().data("账户被锁定，请联系管理员!")));
        } else if (exception instanceof CredentialsExpiredException) {
            out.write(new ObjectMapper().writeValueAsString(R.ok().data("密码过期，请联系管理员!")));
        } else if (exception instanceof AccountExpiredException) {
            out.write(new ObjectMapper().writeValueAsString(R.ok().data("账户过期，请联系管理员!")));
        } else if (exception instanceof DisabledException) {
            out.write(new ObjectMapper().writeValueAsString(R.ok().data("账户被禁用，请联系管理员!")));
        } else if (exception instanceof BadCredentialsException) {
            //用户名或密码错误,修改redis中的用户登录次数+1
            //登录错误5次锁定用户
            if(redisUtils.getLoginCount(username) ==5){
                redisUtils.lockLogin(username);
            }else {
                //修改redis中的用户登录次数+1
                redisUtils.addLoginCount(username);
            }

            out.write(new ObjectMapper().writeValueAsString(R.ok().data("用户名或密码错误!")));
        }
        out.flush();
        out.close();
    }
}
