package com.hui.pand.models;

import lombok.Data;

/**
 * @author daihui
 * @date 2020/5/21 16:14
 */
@Data
public class LoginContextInfo {

    private String username;

    //用户登录次数，次数大于n时锁定
    private Integer loginCount;

    //用户锁定状态，0-已锁定，1-未锁定
    private Integer status;

}
