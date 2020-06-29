package com.hui.pand.security;

import com.hui.pand.entity.RoleEntity;
import com.hui.pand.entity.UserEntity;
import com.hui.pand.exception.MyUsernameNotFoundException;
import com.hui.pand.exception.RException;
import com.hui.pand.service.RoleService;
import com.hui.pand.service.UserService;
import com.hui.pand.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author daihui
 * @date 2020/5/7 13:35
 */
@Component
public class TokenUserDetailService implements UserDetailsService{

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserEntity userEntity = userService.getUserByUsername(username);
        if(userEntity == null){
            throw new MyUsernameNotFoundException("用户不存在!");
        }
        List<RoleEntity> roles = roleService.selectRolesByUserId(userEntity.getId());
        userEntity.setRoles(roles);
        User user = new User(userEntity);
        //判断当前登录用户在redis里是否存在锁定记录信息,不存在则初始化
        if(redisUtils.isExistLock(username)){
            if(redisUtils.getStatus(username) ==0){
                user.setAccountNonLocked(false);
            }else{
                user.setAccountNonLocked(true);
            }
        }else {
            redisUtils.refreshLoginContextInfoStatus(username,0);
            user.setAccountNonLocked(true);
        }
        return user;
    }
}
