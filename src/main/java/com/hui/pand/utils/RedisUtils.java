package com.hui.pand.utils;

import com.hui.pand.models.LoginContextInfo;
import com.hui.pand.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author daihui
 * @date 2020/5/13 10:26
 */
@Component
public class RedisUtils {

    //token信息
    private static final int TOKEN_EXPIRE = 60 * 2;
    private static final String TOKEN_PREFIX = "TOKEN:";

    //验证码信息
    private static final int CAPTCHA_EXPIRE = 20;
    private static final String CAPTCHA_PREFIX = "CAPTCHA:";

    //用户锁定
    private static final int LOCK_EXPIRE = 60;
    private static final String LOCK_PREFIX = "LOCK:";

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 根据username刷新LoginContextInfo的登录次数（登录成功时调用）
     *
     * @param username
     */
    public void refreshLoginContextInfoStatus(String username,int count) {
        LoginContextInfo loginContextInfo = new LoginContextInfo();
        loginContextInfo.setUsername(username);
        loginContextInfo.setLoginCount(count);
        loginContextInfo.setStatus(1);
        redisTemplate.opsForValue().set(LOCK_PREFIX+username.toLowerCase(),loginContextInfo);
        redisTemplate.expire(LOCK_PREFIX+username.toLowerCase(),LOCK_EXPIRE, TimeUnit.MINUTES);
    }

    /**
     * 根据username刷新登录次数，每错误一次，次数+1（登录错误时调用，redis中以username作为隔离）
     *
     * @param username
     */
    public void addLoginCount(String username) {
        LoginContextInfo loginContextInfo = (LoginContextInfo) redisTemplate.opsForValue().get(LOCK_PREFIX+username.toLowerCase());
        loginContextInfo.setLoginCount(loginContextInfo.getLoginCount() + 1);
        redisTemplate.opsForValue().set(LOCK_PREFIX+username.toLowerCase(),loginContextInfo);
    }

    /**
     * 根据username锁定账号，更新账号的状态，并重置登录次数（登录失败到达n次调用）
     *
     * @param username
     */
    public void lockLogin(String username) {
        LoginContextInfo loginContextInfo = new LoginContextInfo();
        loginContextInfo.setUsername(username.toLowerCase());
        loginContextInfo.setStatus(0);
        loginContextInfo.setLoginCount(1);
        redisTemplate.opsForValue().set(LOCK_PREFIX+username.toLowerCase(),loginContextInfo);
        redisTemplate.expire(LOCK_PREFIX+username.toLowerCase(),LOCK_EXPIRE, TimeUnit.MINUTES);
    }

    /**
     * 判断记录用户锁定状态的key是否存在
     *
     * @param username
     * @return
     */
    public boolean isExistLock(String username) {
        return redisTemplate.hasKey(LOCK_PREFIX+username.toLowerCase());
    }

    /**
     * 根据username获取登录次数
     *
     * @param username
     * @return
     */
    public int getLoginCount(String username) {
        LoginContextInfo loginContextInfo = (LoginContextInfo) redisTemplate.opsForValue().get(LOCK_PREFIX+username.toLowerCase());
        return loginContextInfo.getLoginCount();
    }

    /**
     * 根据username获取用户锁定状态
     *
     * @param username
     * @return
     */
    public int getStatus(String username) {
        LoginContextInfo loginContextInfo = (LoginContextInfo) redisTemplate.opsForValue().get(LOCK_PREFIX+username.toLowerCase());
        return loginContextInfo.getStatus();
    }
    /**
     * 使用key存储验证码,设置超时时间2分钟
     * @param key
     * @param captcha
     */
    public void setCaptcha(String key, String captcha){
        redisTemplate.opsForValue().set(CAPTCHA_PREFIX+key,captcha);
        redisTemplate.expire(CAPTCHA_PREFIX+key,CAPTCHA_EXPIRE, TimeUnit.MINUTES);
    }

    /**
     * 使用key获取验证码
     * @param key
     */
    public String getCaptcha(String key){
       return (String) redisTemplate.opsForValue().get(CAPTCHA_PREFIX+key);
    }

    /**
     * 删除验证码
     *
     * @param key
     */
    public void deleteCaptcha(String key) {
        redisTemplate.delete(TOKEN_PREFIX+key);
    }

    /**
     * 使用token存储用户信息,设置超时时间2小时
     * @param token
     * @param user
     */
    public void setToken(String token, User user){
        redisTemplate.opsForValue().set(TOKEN_PREFIX+token,user);
        redisTemplate.expire(TOKEN_PREFIX+token,TOKEN_EXPIRE, TimeUnit.MINUTES);
    }

    /**
     * 判断token是否存在
     *
     * @param token
     * @return
     */
    public boolean isTokenExist(String token) {
        if (redisTemplate.hasKey(TOKEN_PREFIX+token)) {
            return true;
        }
        return false;
    }

    /**
     * 根据token获取用户信息
     * @param token
     * @return
     */
    public User getUserByToken(String token){
        return (User) redisTemplate.opsForValue().get(TOKEN_PREFIX+token);
    }

    /**
     * 删除TOKEN
     *
     * @param token
     */
    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX+token);
    }
}
