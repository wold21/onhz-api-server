package com.onhz.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Value("${spring.elasticsearch.host}")
    private String elasticsearchHost;
    @Value("${spring.elasticsearch.port}")
    private String elasticsearchPort;

    @Bean
    public RestTemplate elasticSearchClient() {
        String baseUrl = "http://" + elasticsearchHost + ":" + elasticsearchPort;
        return new RestTemplateBuilder()
                .rootUri(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
