package com.hui.pand.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.hui.pand.models.VG;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @author daihui
 * @date 2020/5/7 13:39
 */
@Data
@TableName(value = "user")
public class UserEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    @NotBlank(groups = {VG.Login.class,VG.Add.class})
    private String username;

    @NotBlank(groups = {VG.Login.class,VG.Add.class})
    private String password;

    @NotBlank(groups = {VG.Add.class})
    private String name;

    @NotBlank(groups = {VG.Add.class})
    private String phone;

    private String email;

    private Date createTime;

    private Date modifyTime;

    @TableField(exist = false)
    private List<RoleEntity> roles;

}
