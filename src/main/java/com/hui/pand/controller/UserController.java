package com.hui.pand.controller;

import com.hui.pand.entity.UserEntity;
import com.hui.pand.models.R;
import com.hui.pand.models.VG;
import com.hui.pand.security.User;
import com.hui.pand.service.UserService;
import com.hui.pand.utils.DateUtil;
import com.hui.pand.utils.MD5Util;
import com.hui.pand.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author daihui
 * @date 2020/5/7 14:21
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("register")
    public R register(@RequestBody @Validated(VG.Add.class) UserEntity userEntity){
//        String md5Password = MD5Util.getEncryptedPwd(userEntity.getPassword());
        String encodePassword = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(encodePassword);
        userEntity.setId(SnowflakeIdWorker.getId().toString());
        userEntity.setCreateTime(DateUtil.getDate());
        userEntity.setModifyTime(DateUtil.getDate());
        userService.insert(userEntity);
        return R.ok();
    }

    @GetMapping("info")
    public R info(){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return R.ok().data(user.getUser());
    }



}
