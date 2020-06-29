package com.hui.pand.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
//lombok使用:https://www.jianshu.com/p/2543c71a8e45
@Data
@TableName("api_request_records")
public class ApiRequestRecordsEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String apiDes;

    private String uri;

    private String method;

    private String ip;

    private String classMethod;

    private String requestParams;

    private Integer responseCode;

    private String requestId;

    private String responseMsg;

    private String responseData;

    private Date createTime;

    private Date modifyTime;
}
