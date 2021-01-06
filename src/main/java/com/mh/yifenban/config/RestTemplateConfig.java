package com.mh.yifenban.config;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 注入resttemplate用于请求http
 */
@Configuration
public class RestTemplateConfig {

    @Resource
    private RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate restTemplate(){

        return restTemplateBuilder.build();
    }


}
