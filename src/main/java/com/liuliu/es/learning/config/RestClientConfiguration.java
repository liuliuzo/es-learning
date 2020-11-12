package com.liuliu.es.learning.config;

import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.util.StringUtils;

import com.liuliu.es.learning.utils.SSLContextUtils;

//@Configuration
public class RestClientConfiguration extends AbstractElasticsearchConfiguration {
    
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

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
      SSLContext sslContext = SSLContextUtils.getSSLContext(trustStorePath);
      HostnameVerifier hostnameVerifier = SSLContextUtils.createSkipHostnameVerifier();
      String[] connected = StringUtils.trimWhitespace(uris).split(",");
      ClientConfiguration clientConfiguration = ClientConfiguration.builder()
              .connectedTo(connected)
              .usingSsl(sslContext, hostnameVerifier)
              .withBasicAuth(username, password)
              .withConnectTimeout(Duration.ofSeconds(5))
              .withSocketTimeout(Duration.ofSeconds(5))
              .build();
      return RestClients.create(clientConfiguration).rest();
    }
}