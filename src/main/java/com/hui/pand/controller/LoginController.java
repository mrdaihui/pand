package com.hui.pand.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hui.pand.entity.UserEntity;
import com.hui.pand.exception.RException;
import com.hui.pand.models.CodeDefined;
import com.hui.pand.models.R;
import com.hui.pand.models.VG;
import com.hui.pand.service.UserService;
import com.hui.pand.utils.CaptchaUtil;
import com.hui.pand.utils.RedisUtils;
import com.hui.pand.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Wrapper;

/**
 * @author daihui
 * @date 2020/5/13 10:10
 */
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtils redisUtils;

//    @PostMapping("login")
//    public R login(@RequestBody @Validated(VG.Login.class) UserEntity userEntity){
//
//        EntityWrapper wrapper = new EntityWrapper<UserEntity>();
//        wrapper.eq("username",userEntity.getUsername());
//        UserEntity user = userService.selectOne(wrapper);
//        if(user==null){
//            return R.error(CodeDefined.USER_PASS_ERROR);
//        }
//
//        return R.ok();
//    }

    @GetMapping("captcha")
    public void captcha(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String captchaKey = httpRequest.getHeader("captchaKey");
        if (StringUtils.isNullOrEmptyStr(captchaKey)) {
            throw new RException(CodeDefined.ERROR_CAPTCHA_LACK_PARAM+":captchaKey");
        }

        // 禁止浏览器缓存
        httpResponse.setHeader("Expires", "-1");
        httpResponse.setHeader("Cache-Control", "no-cache");
        httpResponse.setHeader("Pragma", "-1");
        httpResponse.setContentType("image/jpeg");

        // 生成校验码
        String captcha = CaptchaUtil.getCaptcha(4);
        // 以自定义的token为key保存验证码到redis，以代替session
        redisUtils.setCaptcha(captchaKey, captcha);
        // 生成校验码图像
        BufferedImage captchaImage = CaptchaUtil.getCaptchaImg(captcha);

        ServletOutputStream outStream = httpResponse.getOutputStream();
        ImageIO.write(captchaImage, "jpeg", outStream);
        outStream.close();
    }

    @GetMapping("invalidSession")
    public R invalidSession(){
        return R.error("登录已过期，请重新登录!");
    }

}
