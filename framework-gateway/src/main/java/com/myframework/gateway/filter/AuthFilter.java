package com.myframework.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myframework.common.constant.CommonConstant;
import com.myframework.common.result.Result;
import com.myframework.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 认证过滤器
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 白名单路径（不需要认证）
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/captcha",
            "/auth/register",
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/actuator/**"
    );
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 白名单路径直接放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }
        
        // 获取Token
        String token = getToken(request);
        
        if (!StringUtils.hasText(token)) {
            log.warn("请求路径: {} 缺少Token", path);
            return unauthorized(exchange.getResponse(), "缺少Token");
        }
        
        try {
            // 验证Token
            StpUtil.checkLogin();
            
            // Token有效，将用户信息添加到请求头
            String userId = StpUtil.getLoginIdAsString();
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(CommonConstant.USER_ID, userId)
                    .build();
            
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.error("Token验证失败: {}, 路径: {}", e.getMessage(), path);
            return unauthorized(exchange.getResponse(), "Token无效或已过期");
        }
    }
    
    /**
     * 判断是否为白名单路径
     */
    private boolean isWhitePath(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> {
            if (pattern.endsWith("/**")) {
                String prefix = pattern.substring(0, pattern.length() - 3);
                return path.startsWith(prefix);
            }
            return path.equals(pattern);
        });
    }
    
    /**
     * 从请求中获取Token
     */
    private String getToken(ServerHttpRequest request) {
        // 从Header获取
        String token = request.getHeaders().getFirst(CommonConstant.TOKEN_HEADER);
        if (StringUtils.hasText(token) && token.startsWith(CommonConstant.TOKEN_PREFIX)) {
            return token.substring(CommonConstant.TOKEN_PREFIX.length());
        }
        
        // 从参数获取
        if (request.getQueryParams().containsKey("token")) {
            return request.getQueryParams().getFirst("token");
        }
        
        return null;
    }
    
    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        Result<Void> result = Result.error(ResultCode.UNAUTHORIZED.getCode(), message);
        
        try {
            byte[] bytes = objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return response.setComplete();
        }
    }
    
    @Override
    public int getOrder() {
        return -100;
    }
}

