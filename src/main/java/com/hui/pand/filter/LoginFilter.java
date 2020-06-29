package com.hui.pand.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hui.pand.entity.UserEntity;
import com.hui.pand.models.R;
import com.hui.pand.security.User;
import com.hui.pand.utils.RedisUtils;
import com.hui.pand.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author daihui
 * @date 2020/5/20 14:51
 */
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    SessionRegistry sessionRegistry;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        if (request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE) || request.getContentType().contains(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
            Map<String, String> loginData = new HashMap<>();
            try {
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                String captcha = loginData.get("captcha");
                String captchaKey = loginData.get("captchaKey");
                checkCode(response, captchaKey, captcha);
            }
            String username = loginData.get(getUsernameParameter());
            String password = loginData.get(getPasswordParameter());
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
            username = username.trim();
            request.setAttribute("username",username);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    username, password);
            setDetails(request, authRequest);
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            User user = new User(userEntity);
            sessionRegistry.registerNewSession(request.getSession(true).getId(), user);
            AuthenticationManager authenticationManager = SpringContextUtil.getBean(AuthenticationManager.class);
            return authenticationManager.authenticate(authRequest);
        } else {
            //form表单提交
            checkCode(response, request.getParameter("captchaKey"), request.getParameter("captcha"));
            return super.attemptAuthentication(request, response);
        }
    }

    public void checkCode(HttpServletResponse response, String captchaKey, String captcha) {
        try {
            if (captchaKey == null) {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.write(new ObjectMapper().writeValueAsString(R.ok().data("缺少参数:captchaKey")));
                out.flush();
                out.close();
            }
            if(captcha == null){
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.write(new ObjectMapper().writeValueAsString(R.ok().data("缺少参数:captcha")));
                out.flush();
                out.close();
            }
            //filter里面bean无法自动注入
            String redisCaptcha = SpringContextUtil.getBean(RedisUtils.class).getCaptcha(captchaKey);
            if (redisCaptcha == null || captcha.length() != 4 || !redisCaptcha.toLowerCase().equals(captcha.toLowerCase())) {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.write(new ObjectMapper().writeValueAsString(R.ok().data("验证码不正确")));
                out.flush();
                out.close();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
