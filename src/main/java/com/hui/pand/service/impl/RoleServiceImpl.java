package com.hui.pand.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hui.pand.entity.RoleEntity;
import com.hui.pand.entity.UserEntity;
import com.hui.pand.mapper.RoleMapper;
import com.hui.pand.mapper.UserMapper;
import com.hui.pand.service.RoleService;
import com.hui.pand.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author daihui
 * @date 2020/5/7 14:09
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper,RoleEntity> implements RoleService{

    @Override
    public List<RoleEntity> selectRolesByUserId(String userId) {
        return baseMapper.selectRolesByUserId(userId);
    }
}
