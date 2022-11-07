package com.example.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateClient {

    @Bean(name = "restTemplate")
    RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(closeableHttpClient());
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(30000);
        return new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
    }

    private CloseableHttpClient closeableHttpClient() {
        return HttpClients.createDefault();
    }

}
