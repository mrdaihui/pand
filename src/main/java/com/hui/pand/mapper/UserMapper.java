package com.hui.pand.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hui.pand.entity.UserEntity;

/**
 * @author daihui
 * @date 2020/5/7 14:09
 */
public interface UserMapper extends BaseMapper<UserEntity>{

    UserEntity selectUserRoleByUserId(String userId);

}
