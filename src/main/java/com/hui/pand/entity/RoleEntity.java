package com.hui.pand.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.hui.pand.models.VG;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author daihui
 * @date 2020/5/7 13:39
 */
@Data
@TableName(value = "role")
public class RoleEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String roleCode;

    private String roleName;

    private Date createTime;

    private Date modifyTime;

}
