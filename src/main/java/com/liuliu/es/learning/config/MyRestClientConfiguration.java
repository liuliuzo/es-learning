package com.liuliu.es.learning.config;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.TerminalClientConfigurationBuilder;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.util.StringUtils;

import com.liuliu.es.learning.utils.SSLContextUtils;

//@Configuration
public class MyRestClientConfiguration extends AbstractElasticsearchConfiguration {
    
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
        HttpHost[] httpHosts = formattedHosts(clientConfiguration.getEndpoints(), clientConfiguration.useSsl()).stream().map(HttpHost::create).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(httpHosts);
        builder.setHttpClientConfigCallback(clientBuilder -> {
            Optional<SSLContext> sslContext1 = clientConfiguration.getSslContext();
            Optional<HostnameVerifier> hostNameVerifier1 = clientConfiguration.getHostNameVerifier();
            sslContext1.ifPresent(clientBuilder::setSSLContext);
            hostNameVerifier1.ifPresent(clientBuilder::setSSLHostnameVerifier);
            Duration connectTimeout = clientConfiguration.getConnectTimeout();
            Duration timeout = clientConfiguration.getSocketTimeout();
            Builder requestConfigBuilder = RequestConfig.custom();
            if (!connectTimeout.isNegative()) {
                requestConfigBuilder.setConnectTimeout(Math.toIntExact(connectTimeout.toMillis()));
                requestConfigBuilder.setConnectionRequestTimeout(Math.toIntExact(connectTimeout.toMillis()));
            }
            if (!timeout.isNegative()) {
                requestConfigBuilder.setSocketTimeout(Math.toIntExact(timeout.toMillis()));
            }
            clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
            clientBuilder.setKeepAliveStrategy(CustomConnectionKeepAliveStrategy.INSTANCE);
            clientConfiguration.getProxy().map(HttpHost::create).ifPresent(clientBuilder::setProxy);
            return clientBuilder;
        });
        
        return new RestHighLevelClient(builder);
    }

    private static List<String> formattedHosts(List<InetSocketAddress> hosts, boolean useSsl) {
        return hosts.stream().map(it -> (useSsl ? "https" : "http") + "://" + it).collect(Collectors.toList());
    }
}