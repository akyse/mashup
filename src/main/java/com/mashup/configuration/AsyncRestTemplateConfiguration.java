package com.mashup.configuration;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.Arrays;

@Configuration
public class AsyncRestTemplateConfiguration {
    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(15000)
                .setSocketTimeout(15000)
                .build();

        final CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.custom()
                .setMaxConnTotal(200)
                .setMaxConnPerRoute(100)
                .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
                .setDefaultRequestConfig(requestConfig)
                .build();

        final AsyncClientHttpRequestFactory httpRequestFactory = new HttpComponentsAsyncClientHttpRequestFactory(closeableHttpAsyncClient);

        final AsyncRestTemplate asyncRest = new AsyncRestTemplate(httpRequestFactory);
        asyncRest.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter()));

        return asyncRest;
    }
}
