package com.hui.pand.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hui.pand.security.handler.LoginFailureHandler;
import com.hui.pand.filter.LoginFilter;
import com.hui.pand.models.R;
import com.hui.pand.security.User;
import com.hui.pand.security.UserPasswordEncoder;
import com.hui.pand.security.TokenUserDetailService;
import com.hui.pand.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author daihui
 * @date 2020/5/7 10:38
 */
@Configuration
@Slf4j
@EnableGlobalMethodSecurity(securedEnabled = true)//开启注解控制接口权限
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private RedisConnectionFactory connectionFactory;

    @Autowired
    private TokenUserDetailService tokenUserDetailService;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
//                .antMatchers("/test/get").permitAll()
                .antMatchers("/captcha").permitAll()
                .antMatchers("/user/register").permitAll()
//                .antMatchers("/test/admin/**").hasRole("admin")
//                .antMatchers("/test/user/**").hasRole("user")
                .anyRequest().authenticated()
                .and()
//                .formLogin()
//                .loginProcessingUrl("/login")
//                .successHandler((req, resp, authentication) -> {
//                    Object principal = authentication.getPrincipal();
//                    resp.setContentType("application/json;charset=utf-8");
//                    PrintWriter out = resp.getWriter();
//                    out.write(new ObjectMapper().writeValueAsString(R.ok().data(principal)));
//                    out.flush();
//                    out.close();
//                })
//                .failureHandler((req, resp, e) -> {
//                    resp.setContentType("application/json;charset=utf-8");
//                    PrintWriter out = resp.getWriter();
//                    log.info(e.getMessage());
//                    out.write(new ObjectMapper().writeValueAsString(R.ok().data("用户名或密码错误!")));
//                    out.flush();
//                    out.close();
//                })
//                .permitAll()
//                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((req, resp, authentication) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(R.ok().data("注销成功!")));
                    out.flush();
                    out.close();
                })
                .permitAll()
//                .and().sessionManagement().invalidSessionUrl("/invalidSession")
                .and()
                .csrf().disable().exceptionHandling()
                .authenticationEntryPoint((req, resp, authException) -> {
                            resp.setContentType("application/json;charset=utf-8");
                            PrintWriter out = resp.getWriter();
                            out.write(new ObjectMapper().writeValueAsString(R.ok().data("尚未登录，请登录!")));
                            out.flush();
                            out.close();
                        }
                );
            http.addFilterAt(new ConcurrentSessionFilter(sessionRegistry(), event -> {
            HttpServletResponse resp = event.getResponse();
            resp.setContentType("application/json;charset=utf-8");
            resp.setStatus(401);
            PrintWriter out = resp.getWriter();
            out.write(new ObjectMapper().writeValueAsString(R.error("您已在另一台设备登录，本次登录已下线!")));
            out.flush();
            out.close();
        }), ConcurrentSessionFilter.class);
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
                    Object principal = authentication.getPrincipal();
                    //登录成功，刷新redis里的用户登录次数
                    redisUtils.refreshLoginContextInfoStatus(((User) principal).getUsername(),1);
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(R.ok().data(principal)));
                    out.flush();
                    out.close();
                }
        );
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setFilterProcessesUrl("/login");
        ConcurrentSessionControlAuthenticationStrategy sessionStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        sessionStrategy.setMaximumSessions(1);
        loginFilter.setSessionAuthenticationStrategy(sessionStrategy);
        return loginFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(tokenUserDetailService);
    }

    /**
     * 自定义UserDetailsService，并发布为Spring Bean
     *
     * @return
     */
//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        return new TokenUserDetailService();
//    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new UserPasswordEncoder();
    }

    @Bean
    SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

//    @Bean
//    public TokenStore tokenStore() {
//        RedisTokenStore redis = new RedisTokenStore(connectionFactory);
//        return redis;
//    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
