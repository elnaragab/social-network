package ru.itgroup.intouch.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FeignConfig {
    private final HttpServletRequest request;

    @Bean
    public RequestInterceptor requestInterceptor() {
        String authHeader = "AUTHORIZATION";
        return requestTemplate -> {
            requestTemplate.header(authHeader, request.getHeader(authHeader));
        };
    }
}