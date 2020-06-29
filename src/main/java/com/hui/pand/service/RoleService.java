package com.hui.pand.service;

import com.baomidou.mybatisplus.service.IService;
import com.hui.pand.entity.RoleEntity;
import com.hui.pand.entity.UserEntity;

import java.util.List;

/**
 * @author daihui
 * @date 2020/5/7 14:08
 */
public interface RoleService extends IService<RoleEntity>{

    List<RoleEntity> selectRolesByUserId(String userId);

}
