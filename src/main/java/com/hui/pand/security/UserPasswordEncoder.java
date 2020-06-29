package com.hui.pand.security;

import com.hui.pand.utils.MD5Util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author daihui
 * @date 2020/5/7 13:53
 */
//@Component
public class UserPasswordEncoder implements PasswordEncoder{

    /**
     * 加密逻辑，自行修改
     * @param charSequence
     * @return
     */
    @Override
    public String encode(CharSequence charSequence) {
//        return MD5Util.getEncryptedPwd(charSequence.toString());
        return new BCryptPasswordEncoder().encode(charSequence);
    }

    /**
     * security密码比对
     * @param charSequence 用户输入的密码
     * @param password 从数据库取的密码
     * @return
     */
    @Override
    public boolean matches(CharSequence charSequence, String password) {
        return new BCryptPasswordEncoder().matches(charSequence,password);
//        boolean result = false;
//        try {
//            result = MD5Util.validPassword(charSequence.toString(),password);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        if(result){
//            return true;
//        }else {
//            return false;
//        }
    }
}
