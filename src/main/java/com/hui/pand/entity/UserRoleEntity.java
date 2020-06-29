package com.hui.pand.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * @author daihui
 * @date 2020/5/18 16:29
 */
@TableName(value = "user_role")
@Data
public class UserRoleEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String userId;

    private String roleId;

    private Date createTime;

    private Date modifyTime;
}
