package com.polar.bear.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.polar.bear.api.utils.LogTool;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpLoggingInterceptor extends HandlerInterceptorAdapter {

    private final ObjectMapper objectMapper;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3) throws Exception {
        LogTool logSb = (LogTool) request.getAttribute("log");

        final ContentCachingRequestWrapper contentCachingRequestWrapper = (ContentCachingRequestWrapper) request;
        if (contentCachingRequestWrapper.getContentType() != null && contentCachingRequestWrapper.getContentType().contains("application/json")) {
            if (contentCachingRequestWrapper.getContentAsByteArray().length != 0) {
                if(!"/v1/auth/login/ad/".equals(contentCachingRequestWrapper.getRequestURI())) {
                    logSb.addLog("[Request Body         ] " + objectMapper.readTree(contentCachingRequestWrapper.getContentAsByteArray()).toPrettyString());
                }
            }
        }
    }
}

