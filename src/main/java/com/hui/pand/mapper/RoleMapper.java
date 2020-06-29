package com.hui.pand.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hui.pand.entity.RoleEntity;
import com.hui.pand.entity.UserEntity;

import java.util.List;

/**
 * @author daihui
 * @date 2020/5/7 14:09
 */
public interface RoleMapper extends BaseMapper<RoleEntity>{

    List<RoleEntity> selectRolesByUserId(String userId);

}
