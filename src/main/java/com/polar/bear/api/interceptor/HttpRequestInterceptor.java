package com.polar.bear.api.interceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.utils.LogTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpRequestInterceptor extends HandlerInterceptorAdapter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        LogTool logSb = new LogTool();
        logSb.addLog("[Request URI          ] " + request.getRequestURI());
        logSb.addLog("[Request Method       ] " + request.getMethod());
        logSb.addLog("[Request IP           ] " + ((null != request.getHeader("X-Forwarded-For")) ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr()));
        logSb.addLog("[Request User-Agent   ] " + ((null != request.getHeader("User-Agent")) ? request.getHeader("User-Agent") : ""));
        logSb.addLog("[Request Service-Key  ] " + ((null != request.getHeader("X-Auth-User-Service-Key")) ? request.getHeader("X-Auth-User-Service-Key") : ""));
        logSb.addLog("[Request Content-Type ] " + ((null != request.getHeader("Content-Type")) ? request.getHeader("Content-Type") : ""));

        Map<String, Object> paramMap = new HashMap<>();
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            logSb.addLog("[Request Parameter    ] Key:[" + key + "]->" + "Value:[" + request.getParameter(key) + "]");
            paramMap.put(key, request.getParameter(key));
        }
        logSb.addLog("[Request Process Start]");

        String ip = !StringUtils.isEmpty(request.getHeader("X-Forwarded-For")) ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr();
        request.setAttribute("log", logSb);
        request.setAttribute("paramMap", paramMap);
        request.setAttribute("ip", ip);


        response.setHeader("Access-Control-Expose-Headers", "X-Total-Count,X-Search-Total-Count,Status-Code");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3) throws Exception {
        LogTool logSb = (LogTool) request.getAttribute("log");
        logSb.addLog("[Request Process End  ]");
        
        logger.info("request.getHeader(\"Authorization\") ");
        if (request.getHeader("Authorization") != null) {
            if (this.jwtUtil == null) {
                log.info("jwt token is null");
            }

            LoginRedisVo loginRedisVo = this.jwtUtil.validateAccessToken(request.getHeader("authorization"));
            if (loginRedisVo != null) {
                
                logSb.addLog("[Request Authorization] " + loginRedisVo.getCsrKey());

                if (StringUtils.equals(loginRedisVo.getType(), "CSR")) {
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", loginRedisVo.getCsrId());
                }

            } else {
                logSb.addLog("[Request Authorization]  ");
            }
        } else {
            logSb.addLog("[Request Authorization]  ");
        }

        logSb.addLog("[Response Status Code ] " + response.getStatus());
//        if(response.getStatus() == 200 || response.getStatus() == 201){
//        }
        if (logSb != null) {
            log.info(logSb.toString());
        }
    }
}