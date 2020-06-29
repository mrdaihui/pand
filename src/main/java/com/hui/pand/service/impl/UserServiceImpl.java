package com.hui.pand.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hui.pand.entity.UserEntity;
import com.hui.pand.mapper.UserMapper;
import com.hui.pand.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author daihui
 * @date 2020/5/7 14:09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,UserEntity> implements UserService{

    @Override
    public UserEntity getUserByUsername(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        return baseMapper.selectOne(userEntity);
    }

    @Override
    public UserEntity selectUserRoleByUserId(String userId) {
        return baseMapper.selectUserRoleByUserId(userId);
    }

}
