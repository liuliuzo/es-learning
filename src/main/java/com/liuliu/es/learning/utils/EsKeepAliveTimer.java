package com.liuliu.es.learning.utils;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EsKeepAliveTimer {
    private static final Logger log = LoggerFactory.getLogger(EsKeepAliveTimer.class);
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Scheduled(cron="${spring.data.elasticsearch.rest.keepAlive.cron:*/60 * * * * ?}")
    private void keepAlive(){
        try{
            RestHighLevelClient restHighLevelClient = elasticsearchRestTemplate.getClient();
            ClusterHealthRequest request = new ClusterHealthRequest();
            ClusterHealthResponse clusterHealthResponse = restHighLevelClient.cluster().health(request, RequestOptions.DEFAULT);
            log.debug("es status is {}",clusterHealthResponse.getStatus());
        }catch (IOException ex) {
            log.error("elasticsearch healthcheck fail", ex);
        }
    }
}