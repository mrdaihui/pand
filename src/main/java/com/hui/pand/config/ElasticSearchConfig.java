package com.hui.pand.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticSearchConfig {

    private Logger logger = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Value("${es.ip}")
    private String elasticSearchIp;

    @Value("${es.port}")
    private Integer elasticSearchPort;

    @Bean
    public TransportClient createElasticSearchClient() {
//        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();

        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticSearchIp), elasticSearchPort));
        } catch (UnknownHostException e) {
            logger.error("createElasticSearchClient:",e.getMessage());
            client.close();
        }
        logger.info("连接信息:" + client.toString());
        return client;
    }

}
