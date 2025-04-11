package com.onhz.server.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestId = UUID.randomUUID().toString();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        try {
            log.info("[{}] Request: {} {} (Client IP: {})",
                    requestId,
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    request.getRemoteAddr()
            );

            chain.doFilter(requestWrapper, responseWrapper);

            long duration = System.currentTimeMillis() - startTime;
            String requestBody = maskSenditiveData(new String(requestWrapper.getContentAsByteArray(),
                    StandardCharsets.UTF_8), requestWrapper.getContentType());
            String responseBody = maskSenditiveData(new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8),
                    responseWrapper.getContentType());

            if (httpRequest.getRequestURI().startsWith("/api/")) {
                log.info("[{}] Response: Status={}, Duration={}ms, Request Body={}, Response Body={}",
                        requestId,
                        responseWrapper.getStatus(),
                        duration,
                        requestWrapper,
                        responseWrapper
                );
            }

        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private String maskSenditiveData(String content, String contentType) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        if (contentType == null || !contentType.toLowerCase().contains("application/json")) {
            return content;
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(content);
            if(jsonNode.isObject()){
                ObjectNode objectNode = (ObjectNode) jsonNode;
                List<String> fieldsToMask = new ArrayList<>();

                objectNode.fields().forEachRemaining(entry -> {
                    String key = entry.getKey().toLowerCase();
                    if (key.contains("password")
                            || key.contains("token")) {
                        fieldsToMask.add(entry.getKey());
                    }
                });

                for (String field : fieldsToMask) {
                    objectNode.put(field, "*****");
                }
                return mapper.writeValueAsString(jsonNode);
            }
            return content;
        }
        catch (Exception e) {
            log.error("민감한 정보 처리 중 에러", e);
            return content;
        }
    }
}
