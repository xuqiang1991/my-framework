package com.myframework.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.myframework.auth.dto.OAuth2AuthorizeRequest;
import com.myframework.auth.dto.OAuth2TokenRequest;
import com.myframework.auth.dto.OAuth2TokenResponse;
import com.myframework.auth.entity.OAuthClient;
import com.myframework.auth.entity.OAuthToken;
import com.myframework.auth.service.OAuth2Service;
import com.myframework.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;

/**
 * OAuth2控制器
 */
@Slf4j
@Tag(name = "OAuth2管理", description = "OAuth2单点登录相关接口")
@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {
    
    private final OAuth2Service oauth2Service;
    
    /**
     * OAuth2授权端点
     * 用户访问此接口进行授权，如果用户已登录则直接生成授权码并重定向，否则跳转到登录页面
     */
    @Operation(summary = "OAuth2授权端点")
    @GetMapping("/authorize")
    public RedirectView authorize(OAuth2AuthorizeRequest request) {
        log.info("OAuth2授权请求: clientId={}, redirectUri={}", request.getClientId(), request.getRedirectUri());
        
        // 验证客户端
        OAuthClient client = oauth2Service.validateClient(request.getClientId(), null);
        
        // 验证重定向URI
        oauth2Service.validateRedirectUri(client, request.getRedirectUri());
        
        // 检查用户是否已登录
        if (!StpUtil.isLogin()) {
            // 未登录，重定向到登录页面
            String loginUrl = String.format("/auth/sso/login?client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                    request.getClientId(),
                    request.getRedirectUri(),
                    request.getScope() != null ? request.getScope() : "",
                    request.getState() != null ? request.getState() : "");
            return new RedirectView(loginUrl);
        }
        
        // 已登录，检查是否需要用户授权确认
        if (client.getAutoApprove() == 0) {
            // 需要用户确认授权，重定向到授权确认页面
            String approveUrl = String.format("/auth/sso/approve?client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                    request.getClientId(),
                    request.getRedirectUri(),
                    request.getScope() != null ? request.getScope() : "",
                    request.getState() != null ? request.getState() : "");
            return new RedirectView(approveUrl);
        }
        
        // 自动授权，直接生成授权码
        String userId = StpUtil.getLoginIdAsString();
        String code = oauth2Service.generateAuthorizationCode(userId, request);
        
        // 构建重定向URL
        String redirectUrl = request.getRedirectUri() + 
                "?code=" + code +
                (request.getState() != null ? "&state=" + request.getState() : "");
        
        log.info("OAuth2授权成功: userId={}, code={}, redirectUrl={}", userId, code, redirectUrl);
        
        return new RedirectView(redirectUrl);
    }
    
    /**
     * 用户确认授权
     * 用户在授权确认页面点击确认后调用此接口
     */
    @Operation(summary = "用户确认授权")
    @PostMapping("/approve")
    public Result<String> approve(@RequestBody OAuth2AuthorizeRequest request) {
        log.info("用户确认授权: clientId={}", request.getClientId());
        
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        // 生成授权码
        String code = oauth2Service.generateAuthorizationCode(userId, request);
        
        // 构建重定向URL
        String redirectUrl = request.getRedirectUri() + 
                "?code=" + code +
                (request.getState() != null ? "&state=" + request.getState() : "");
        
        return Result.success(redirectUrl);
    }
    
    /**
     * OAuth2令牌端点
     * 客户端使用授权码换取访问令牌
     */
    @Operation(summary = "OAuth2令牌端点")
    @PostMapping("/token")
    public Result<OAuth2TokenResponse> token(@RequestBody OAuth2TokenRequest request) {
        log.info("OAuth2令牌请求: grantType={}, clientId={}", request.getGrantType(), request.getClientId());
        
        OAuth2TokenResponse response = oauth2Service.exchangeToken(request);
        
        return Result.success(response);
    }
    
    /**
     * 验证访问令牌
     */
    @Operation(summary = "验证访问令牌")
    @PostMapping("/introspect")
    public Result<OAuthToken> introspect(@RequestParam("token") String accessToken) {
        log.info("验证访问令牌: token={}", accessToken);
        
        OAuthToken token = oauth2Service.validateAccessToken(accessToken);
        
        return Result.success(token);
    }
    
    /**
     * 撤销令牌
     */
    @Operation(summary = "撤销令牌")
    @PostMapping("/revoke")
    public Result<Void> revoke(@RequestParam("token") String accessToken) {
        log.info("撤销令牌: token={}", accessToken);
        
        oauth2Service.revokeToken(accessToken);
        
        return Result.success();
    }
    
    /**
     * 获取用户信息（OAuth2资源端点）
     * 第三方应用使用访问令牌获取用户信息
     */
    @Operation(summary = "获取用户信息")
    @GetMapping("/userinfo")
    public Result<Object> userinfo(HttpServletRequest request) {
        // 从请求头获取访问令牌
        String authorization = request.getHeader("Authorization");
        
        // 验证令牌
        OAuthToken token = oauth2Service.validateAccessToken(authorization);
        
        // TODO: 根据userId获取用户详细信息
        // 这里简化处理，实际应该调用用户服务获取完整信息
        return Result.success(token);
    }
}

