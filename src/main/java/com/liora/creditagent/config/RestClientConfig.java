package com.liora.creditagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient lioraRestClient(
            @Value("${liora.api.base-url}") String baseUrl,
            @Value("${liora.api.token}") String token,
            JsonMapper jsonMapper
    ) {

        var jsonConverter =
                new JacksonJsonHttpMessageConverter(jsonMapper);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + token
                )
                .configureMessageConverters(configurer ->
                        configurer.withJsonConverter(jsonConverter)
                )
                .build();
    }
}