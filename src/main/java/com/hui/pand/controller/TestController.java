package com.hui.pand.controller;

import com.hui.pand.aop.annotations.ApiRequestRecord;
import com.hui.pand.exception.RException;
import com.hui.pand.models.R;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RestController
@RequestMapping("test")
@Slf4j
public class TestController {

    @Autowired(required = false)
    private TransportClient client;

    @GetMapping("get")
    @ApiRequestRecord(des = "test接口")
    public R test(){
        throw new RException("cuowu");
    }

    @PostMapping("get2")
    @ApiRequestRecord(des = "get2")
    public R test2(HttpServletRequest request, HttpServletResponse response, @RequestParam String id){

        System.out.println(id);
        System.out.println(request.getParameter("name"));

        return R.ok();
    }

    @GetMapping("/es/get")
    public void esGet(){
        // 设置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.must(QueryBuilders.matchQuery("name", "ssm2"));
        queryBuilder.must(QueryBuilders.matchPhraseQuery("name", "ssm2"));
        // 联接查询条件
        SearchRequestBuilder responseBuilder = client.prepareSearch("robots").setTypes("developer").setQuery(queryBuilder);
        // 执行查询
        SearchResponse searchResponse = responseBuilder.setQuery(queryBuilder).execute().actionGet();
        SearchHits searchHits = searchResponse.getHits();
        Arrays.stream(searchHits.getHits()).forEach(item ->{
            System.out.println(item.getSourceAsMap().get("name"));
        });
    }

    @GetMapping("admin/get")
    @Secured(value = {"ROLE_admin"})
    public R admin(){
        return R.ok().data("admin");
    }

    @GetMapping("user/get")
    @Secured(value = {"ROLE_user"})
    public R user(){
        return R.ok().data("user");
    }

}
