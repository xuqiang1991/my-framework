package com.myframework.auth.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myframework.auth.dto.OAuth2AuthorizeRequest;
import com.myframework.auth.dto.OAuth2TokenRequest;
import com.myframework.auth.dto.OAuth2TokenResponse;
import com.myframework.auth.entity.OAuthClient;
import com.myframework.auth.entity.OAuthCode;
import com.myframework.auth.entity.OAuthToken;
import com.myframework.auth.mapper.OAuthClientMapper;
import com.myframework.auth.mapper.OAuthCodeMapper;
import com.myframework.auth.mapper.OAuthTokenMapper;
import com.myframework.common.constant.BusinessConstant;
import com.myframework.common.exception.BusinessException;
import com.myframework.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * OAuth2服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {
    
    private final OAuthClientMapper oauthClientMapper;
    private final OAuthCodeMapper oauthCodeMapper;
    private final OAuthTokenMapper oauthTokenMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 验证客户端信息
     */
    public OAuthClient validateClient(String clientId, String clientSecret) {
        if (!StringUtils.hasText(clientId)) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "客户端ID不能为空");
        }
        
        OAuthClient client = oauthClientMapper.selectById(clientId);
        if (client == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "客户端不存在");
        }
        
        if (client.getStatus().equals(BusinessConstant.Status.DISABLED)) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "客户端已被禁用");
        }
        
        // 如果提供了密钥，则验证密钥
        if (StringUtils.hasText(clientSecret)) {
            if (!passwordEncoder.matches(clientSecret, client.getClientSecret())) {
                throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "客户端密钥错误");
            }
        }
        
        return client;
    }
    
    /**
     * 验证重定向URI
     */
    public void validateRedirectUri(OAuthClient client, String redirectUri) {
        if (!StringUtils.hasText(redirectUri)) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "重定向URI不能为空");
        }
        
        String[] allowedUris = client.getRedirectUris().split(",");
        boolean isValid = Arrays.stream(allowedUris)
                .anyMatch(uri -> uri.trim().equals(redirectUri));
        
        if (!isValid) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "重定向URI不合法");
        }
    }
    
    /**
     * 生成授权码
     */
    @Transactional(rollbackFor = Exception.class)
    public String generateAuthorizationCode(String userId, OAuth2AuthorizeRequest request) {
        // 验证客户端
        OAuthClient client = validateClient(request.getClientId(), null);
        
        // 验证重定向URI
        validateRedirectUri(client, request.getRedirectUri());
        
        // 生成授权码
        String code = IdUtil.simpleUUID();
        
        // 保存授权码
        OAuthCode oauthCode = new OAuthCode();
        oauthCode.setCode(code);
        oauthCode.setClientId(request.getClientId());
        oauthCode.setUserId(userId);
        oauthCode.setRedirectUri(request.getRedirectUri());
        oauthCode.setScope(request.getScope());
        oauthCode.setState(request.getState());
        oauthCode.setExpiresAt(LocalDateTime.now().plusSeconds(BusinessConstant.OAuth2.CODE_VALIDITY_SECONDS));
        oauthCode.setUsed(BusinessConstant.Status.DISABLED);
        oauthCodeMapper.insert(oauthCode);
        
        log.info("生成授权码成功: userId={}, clientId={}, code={}", userId, request.getClientId(), code);
        
        return code;
    }
    
    /**
     * 使用授权码换取访问令牌
     */
    @Transactional(rollbackFor = Exception.class)
    public OAuth2TokenResponse exchangeToken(OAuth2TokenRequest request) {
        log.info("exchangeToken - 收到的grant_type: {}, 期望值: {}", request.getGrantType(), BusinessConstant.OAuth2.GRANT_TYPE_AUTHORIZATION_CODE);
        
        if (BusinessConstant.OAuth2.GRANT_TYPE_AUTHORIZATION_CODE.equals(request.getGrantType())) {
            return exchangeCodeForToken(request);
        } else if (BusinessConstant.OAuth2.GRANT_TYPE_REFRESH_TOKEN.equals(request.getGrantType())) {
            return refreshAccessToken(request);
        } else {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), 
                "不支持的授权类型: " + request.getGrantType() + ", 仅支持: authorization_code 或 refresh_token");
        }
    }
    
    /**
     * 使用授权码换取令牌
     */
    private OAuth2TokenResponse exchangeCodeForToken(OAuth2TokenRequest request) {
        // 验证客户端
        OAuthClient client = validateClient(request.getClientId(), request.getClientSecret());
        
        // 验证重定向URI
        validateRedirectUri(client, request.getRedirectUri());
        
        // 查询授权码
        LambdaQueryWrapper<OAuthCode> codeWrapper = new LambdaQueryWrapper<>();
        codeWrapper.eq(OAuthCode::getCode, request.getCode())
                .eq(OAuthCode::getClientId, request.getClientId())
                .eq(OAuthCode::getUsed, BusinessConstant.Status.DISABLED);
        OAuthCode oauthCode = oauthCodeMapper.selectOne(codeWrapper);
        
        if (oauthCode == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "授权码无效或已使用");
        }
        
        // 验证授权码是否过期
        if (oauthCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "授权码已过期");
        }
        
        // 验证重定向URI是否一致
        if (!oauthCode.getRedirectUri().equals(request.getRedirectUri())) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "重定向URI不一致");
        }
        
        // 标记授权码已使用
        oauthCode.setUsed(BusinessConstant.Status.ENABLED);
        oauthCodeMapper.updateById(oauthCode);
        
        // 生成访问令牌和刷新令牌
        String accessToken = IdUtil.simpleUUID();
        String refreshToken = IdUtil.simpleUUID();
        
        // 保存令牌
        OAuthToken oauthToken = new OAuthToken();
        oauthToken.setAccessToken(accessToken);
        oauthToken.setRefreshToken(refreshToken);
        oauthToken.setClientId(request.getClientId());
        oauthToken.setUserId(oauthCode.getUserId());
        oauthToken.setScope(oauthCode.getScope());
        oauthToken.setTokenType("Bearer");
        oauthToken.setAccessTokenExpiresAt(LocalDateTime.now().plusSeconds(client.getAccessTokenValidity()));
        oauthToken.setRefreshTokenExpiresAt(LocalDateTime.now().plusSeconds(client.getRefreshTokenValidity()));
        oauthToken.setRevoked(BusinessConstant.Status.DISABLED);
        oauthTokenMapper.insert(oauthToken);
        
        log.info("生成访问令牌成功: userId={}, clientId={}", oauthCode.getUserId(), request.getClientId());
        
        // 构建响应
        return OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn((long) client.getAccessTokenValidity())
                .scope(oauthCode.getScope())
                .build();
    }
    
    /**
     * 刷新访问令牌
     */
    private OAuth2TokenResponse refreshAccessToken(OAuth2TokenRequest request) {
        // 验证客户端
        OAuthClient client = validateClient(request.getClientId(), request.getClientSecret());
        
        // 查询刷新令牌
        LambdaQueryWrapper<OAuthToken> tokenWrapper = new LambdaQueryWrapper<>();
        tokenWrapper.eq(OAuthToken::getRefreshToken, request.getRefreshToken())
                .eq(OAuthToken::getClientId, request.getClientId())
                .eq(OAuthToken::getRevoked, BusinessConstant.Status.DISABLED);
        OAuthToken oldToken = oauthTokenMapper.selectOne(tokenWrapper);
        
        if (oldToken == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "刷新令牌无效");
        }
        
        // 验证刷新令牌是否过期
        if (oldToken.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "刷新令牌已过期");
        }
        
        // 撤销旧令牌
        oldToken.setRevoked(BusinessConstant.Status.ENABLED);
        oauthTokenMapper.updateById(oldToken);
        
        // 生成新的访问令牌和刷新令牌
        String newAccessToken = IdUtil.simpleUUID();
        String newRefreshToken = IdUtil.simpleUUID();
        
        // 保存新令牌
        OAuthToken newToken = new OAuthToken();
        newToken.setAccessToken(newAccessToken);
        newToken.setRefreshToken(newRefreshToken);
        newToken.setClientId(request.getClientId());
        newToken.setUserId(oldToken.getUserId());
        newToken.setScope(oldToken.getScope());
        newToken.setTokenType("Bearer");
        newToken.setAccessTokenExpiresAt(LocalDateTime.now().plusSeconds(client.getAccessTokenValidity()));
        newToken.setRefreshTokenExpiresAt(LocalDateTime.now().plusSeconds(client.getRefreshTokenValidity()));
        newToken.setRevoked(BusinessConstant.Status.DISABLED);
        oauthTokenMapper.insert(newToken);
        
        log.info("刷新访问令牌成功: userId={}, clientId={}", oldToken.getUserId(), request.getClientId());
        
        // 构建响应
        return OAuth2TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn((long) client.getAccessTokenValidity())
                .scope(oldToken.getScope())
                .build();
    }
    
    /**
     * 验证访问令牌
     */
    public OAuthToken validateAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
        
        // 移除Bearer前缀
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }
        
        // 查询令牌
        LambdaQueryWrapper<OAuthToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OAuthToken::getAccessToken, accessToken)
                .eq(OAuthToken::getRevoked, 0);
        OAuthToken token = oauthTokenMapper.selectOne(wrapper);
        
        if (token == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
        
        // 验证令牌是否过期
        if (token.getAccessTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }
        
        return token;
    }
    
    /**
     * 撤销令牌
     */
    @Transactional(rollbackFor = Exception.class)
    public void revokeToken(String accessToken) {
        LambdaQueryWrapper<OAuthToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OAuthToken::getAccessToken, accessToken);
        OAuthToken token = oauthTokenMapper.selectOne(wrapper);
        
        if (token != null) {
            token.setRevoked(1);
            oauthTokenMapper.updateById(token);
            log.info("撤销令牌成功: accessToken={}", accessToken);
        }
    }
}

