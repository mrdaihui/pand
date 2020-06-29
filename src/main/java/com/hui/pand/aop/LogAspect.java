package com.hui.pand.aop;

import com.alibaba.fastjson.JSONObject;
import com.hui.pand.aop.annotations.ApiRequestRecord;
import com.hui.pand.entity.ApiRequestRecordsEntity;
import com.hui.pand.models.R;
import com.hui.pand.service.ApiRequestRecordsService;
import com.hui.pand.utils.DateUtil;
import com.hui.pand.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * aop解析ApiRequestRecord注解修饰的接口记录接口请求及返回信息
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    private String id;

    @Autowired
    private ApiRequestRecordsService apiRequestRecordsService;

    /**
     * ..表示包及子包 该方法代表controller层的所有方法
     */
    @Pointcut("execution(public * com.hui.pand.controller..*.*(..))")
    public void controllerMethod() {
    }

    /**
     * 方法执行前
     *
     * @param joinPoint
     * @throws Exception
     */
    @Before("controllerMethod()")
    public void LogRequestInfo(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        ApiRequestRecord apiRequestRecord = ((MethodSignature) signature).getMethod().getAnnotation(ApiRequestRecord.class);
        if(apiRequestRecord != null){
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            ApiRequestRecordsEntity apiRequestRecordsEntity = new ApiRequestRecordsEntity();
            id = SnowflakeIdWorker.getId().toString();
            apiRequestRecordsEntity.setId(id);

            StringBuilder requestLog = new StringBuilder();

            String uri = request.getRequestURI();
            String method = request.getMethod();
            String ip = getIpAddr(request);

            StringBuilder classMethod = new StringBuilder();
            classMethod.append(signature.getDeclaringTypeName()).append(".")
                    .append(signature.getName());

            StringBuilder requestParams = new StringBuilder();
            // 处理请求参数
            String[] paramNames = ((MethodSignature) signature).getParameterNames();
            Object[] paramValues = joinPoint.getArgs();
            int paramLength = null == paramNames ? 0 : paramNames.length;
            if (paramLength == 0) {
                requestParams.append("{}");
            } else {
                requestParams.append("[");
                for (int i = 0; i < paramLength - 1; i++) {
                    if ((paramValues[i] instanceof HttpServletResponse) || (paramValues[i] instanceof HttpServletRequest)
                            || (paramValues[i] instanceof MultipartFile) || (paramValues[i] instanceof MultipartFile[])) {
                        continue;
                    }
                    requestParams.append(paramNames[i]).append("=").append(JSONObject.toJSONString(paramValues[i])).append(",");
                }
                requestParams.append(paramNames[paramLength - 1]).append("=").append(JSONObject.toJSONString(paramValues[paramLength - 1])).append("]");
            }
            requestLog.append(apiRequestRecord.des()).append("\t");

            apiRequestRecordsEntity.setApiDes(apiRequestRecord.des());
            apiRequestRecordsEntity.setUri(uri);
            apiRequestRecordsEntity.setMethod(method);
            apiRequestRecordsEntity.setIp(ip);
            apiRequestRecordsEntity.setClassMethod(classMethod.toString());
            apiRequestRecordsEntity.setRequestParams(requestParams.toString());
            apiRequestRecordsEntity.setCreateTime(DateUtil.getDate());
            apiRequestRecordsEntity.setModifyTime(DateUtil.getDate());

            apiRequestRecordsService.insert(apiRequestRecordsEntity);

            //拼接日志
            requestLog.append("请求信息：").append("URL = {").append(request.getRequestURI()).append("},\t")
                    .append("请求方式 = {").append(request.getMethod()).append("},\t")
                    .append("请求IP = {").append(getIpAddr(request)).append("},\t")
                    .append("类方法 = {").append(signature.getDeclaringTypeName()).append(".")
                    .append(signature.getName()).append("},\t");

            //请求参数
            requestLog.append("请求参数 = ").append(requestParams.toString());

            log.info(requestLog.toString());
        }
    }

    public  String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        if (ip.split(",").length > 1) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    /**
     * 方法执行后
     *
     * @param joinPoint
     * @param r
     * @throws Exception
     */
    @AfterReturning(returning = "r", pointcut = "controllerMethod()")
    public void logResultInfo(JoinPoint joinPoint,R r) throws Exception {
        Signature signature = joinPoint.getSignature();
        ApiRequestRecord apiRequestRecord = ((MethodSignature) signature).getMethod().getAnnotation(ApiRequestRecord.class);
        if(apiRequestRecord != null){
            int code = Integer.parseInt(r.get("code").toString());
            String msg =  r.get("msg").toString();
            String requestId =  r.get("requestId").toString();
            String data = r.get("data")==null?"":r.get("data").toString();

            ApiRequestRecordsEntity apiRequestRecordsEntity = new ApiRequestRecordsEntity();
            apiRequestRecordsEntity.setId(id);
            apiRequestRecordsEntity.setResponseCode(code);
            apiRequestRecordsEntity.setResponseMsg(msg);
            apiRequestRecordsEntity.setRequestId(requestId);
            apiRequestRecordsEntity.setResponseData(data);
            apiRequestRecordsEntity.setModifyTime(DateUtil.getDate());

            apiRequestRecordsService.updateById(apiRequestRecordsEntity);

            StringBuilder responseLog = new StringBuilder();
            responseLog.append("请求结果: code:{ ").append(code).append(" }, msg:{ ").append(msg).append(" }, data:[ ")
                    .append(data).append(" ]");
            log.info(responseLog.toString());
        }

    }


}
