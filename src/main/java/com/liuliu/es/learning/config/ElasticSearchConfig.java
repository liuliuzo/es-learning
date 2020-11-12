package com.liuliu.es.learning.config;

import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.StringUtils;

import com.liuliu.es.learning.utils.SSLContextUtils;


/**
 * @author liuliu e077417
 * @version 1.0
 * @email Liuliu.Zhao@mastercard.com
 * @date 12/9/2019 6:46 PM
 */
@Configuration
public class ElasticSearchConfig {

    @Value("${spring.data.elasticsearch.rest.uris}")
    String uris;

    @Value("${spring.data.elasticsearch.rest.ssl.trustStorePath}")
    String trustStorePath;

    @Value("${spring.data.elasticsearch.rest.acl.username}")
    String username;

    @Value("${spring.data.elasticsearch.rest.acl.password}")
    String password;

    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    @Bean
    public RestClients.ElasticsearchRestClient elasticsearchRestClient(){
        SSLContext sslContext = SSLContextUtils.getSSLContext(trustStorePath);
        HostnameVerifier hostnameVerifier = SSLContextUtils.createSkipHostnameVerifier();
        String[] connected = StringUtils.trimWhitespace(uris).split(",");
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(connected)
                .usingSsl(sslContext, hostnameVerifier)
                .withBasicAuth(username, password)
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(3))
                .build();
        return RestClients.create(clientConfiguration);
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchRestClient().rest());
    } 
}
