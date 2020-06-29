package com.hui.pand.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 处理简单http请求的类
 *
 * @author liuliangcai
 */
public class HttpClientUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * 处理GET类请求,参数URL应包含完整的请求参数
     */
    public static String doGet(String url) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            RequestConfig config = RequestConfig.custom()
                    //从连接池中获取连接的超时时间
                    .setConnectionRequestTimeout(30000)
                    //与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间
                    .setConnectTimeout(30000)
                    //socket读数据超时时间：从服务器获取响应数据的超时时间
                    .setSocketTimeout(30000).build();
            httpGet.setConfig(config);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            LOGGER.error("ERROR, call http get" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 处理GET类请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, Object> params) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            StringBuilder urlBuilder = new StringBuilder(url);
            int i = 0;
            if (params != null && !params.isEmpty()) {
                for (String key : params.keySet()) {
                    urlBuilder.append((i == 0) ? "?" : "&");
                    urlBuilder.append(key + "=" + params.get(key));
                    i++;
                }
            }
            HttpGet httpGet = new HttpGet(urlBuilder.toString());
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            LOGGER.error("ERROR, call http get" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 处理GET类请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, Object> params, Map<String, String> headers) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            StringBuilder urlBuilder = new StringBuilder(url);
            int i = 0;
            if (params != null && !params.isEmpty()) {
                for (String key : params.keySet()) {
                    urlBuilder.append((i == 0) ? "?" : "&");
                    urlBuilder.append(key + "=" + params.get(key));
                    i++;
                }
            }
            HttpGet httpGet = new HttpGet(urlBuilder.toString());
            for (String key : headers.keySet()) {
                httpGet.setHeader(key, headers.get(key));
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            LOGGER.error("ERROR, call http get" + e.getMessage(), e);
        }
        return null;
    }

    // 处理POST类请求

    public static String doPost(String url, Map<String, String> params) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json; charset=UTF-8");
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(30000)
                    .setConnectTimeout(30000).setSocketTimeout(30000).build();

            List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();

            for (Entry<String, String> entry : params.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry
                        .getValue()));
            }
            StringEntity strEntity = new UrlEncodedFormEntity(formparams,
                    Charset.defaultCharset());
            httpPost.setEntity(strEntity);
            httpPost.setConfig(config);


            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            LOGGER.error("ERROR, call http post" + e.getMessage(), e);
        }
        return null;
    }

    // 处理POST类请求
    public static String doPost(String url, Object object) {
        return doPost(url,object,null);
    }

    /**
     * 处理POST类请求，body类型
     */
    public static String doPost(String url, Object body, Map<String, String> headers) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json; charset=UTF-8");
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(30000)
                    .setConnectTimeout(30000).setSocketTimeout(30000).build();


            if(body == null){
                body = new Object();
            }

            StringEntity strEntity = new StringEntity(JsonUtils.getJsonString(body),
                    Charset.forName("UTF-8"));
            httpPost.setEntity(strEntity);
            httpPost.setConfig(config);

            if(headers != null && !headers.isEmpty()){
                for(Entry<String,String> entry : headers.entrySet()){
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }

            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            LOGGER.error("ERROR, call http post" + e.getMessage(),e);
        }
        return null;
    }

}
