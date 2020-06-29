package com.hui.pand.service;

import com.baomidou.mybatisplus.service.IService;
import com.hui.pand.entity.UserEntity;

/**
 * @author daihui
 * @date 2020/5/7 14:08
 */
public interface UserService extends IService<UserEntity>{

    UserEntity getUserByUsername(String username);

    UserEntity selectUserRoleByUserId(String userId);
}
