package com.myframework.user.controller;

import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.result.Result;
import com.myframework.user.dto.ThirdPartyRegisterRequest;
import com.myframework.user.dto.UserRegisterRequest;
import com.myframework.user.service.ThirdPartyApiService;
import com.myframework.user.service.UserRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 第三方平台API控制器
 * 提供给外部平台的注册和查询接口
 */
@Tag(name = "第三方平台API", description = "供外部平台调用的用户注册接口")
@RestController
@RequestMapping("/api/third-party")
@RequiredArgsConstructor
public class ThirdPartyApiController {
    
    private final ThirdPartyApiService thirdPartyApiService;
    private final UserRegisterService userRegisterService;
    
    /**
     * 第三方平台用户注册接口
     * 需要提供API Key和签名验证
     * 
     * @param request 注册请求
     * @return 用户信息
     */
    @Operation(summary = "第三方平台用户注册", 
               description = "供外部平台直接注册用户，需要API Key认证和签名验证")
    @PostMapping("/register")
    public Result<UserDTO> thirdPartyRegister(@RequestBody ThirdPartyRegisterRequest request) {
        // 验证API Key和签名
        thirdPartyApiService.validateApiRequest(request);
        
        // 转换为内部注册请求
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(request.getUsername());
        registerRequest.setNickname(request.getNickname());
        registerRequest.setRealName(request.getRealName());
        registerRequest.setPhone(request.getPhone());
        registerRequest.setEmail(request.getEmail());
        registerRequest.setGender(request.getGender());
        
        // 调用平台用户注册
        UserDTO user = userRegisterService.platformRegister(
            registerRequest, 
            request.getPlatformId(), 
            request.getExternalUserId()
        );
        
        return Result.success(user);
    }
    
    /**
     * 查询第三方平台用户绑定状态
     * 
     * @param platformId 平台ID
     * @param externalUserId 外部用户ID
     * @param apiKey API密钥
     * @return 用户信息（如已绑定）
     */
    @Operation(summary = "查询平台用户绑定状态")
    @GetMapping("/user/status")
    public Result<UserDTO> getUserBindStatus(
            @RequestParam String platformId,
            @RequestParam String externalUserId,
            @RequestParam String apiKey) {
        
        // 验证API Key
        thirdPartyApiService.validateApiKey(platformId, apiKey);
        
        // 查询绑定的用户
        UserDTO user = thirdPartyApiService.getBindUser(platformId, externalUserId);
        
        return Result.success(user);
    }
    
    /**
     * 生成API Key（管理员接口）
     * 
     * @param platformId 平台ID
     * @return API Key和Secret
     */
    @Operation(summary = "生成API Key", description = "管理员为第三方平台生成API凭证")
    @PostMapping("/admin/generate-key")
    public Result<String> generateApiKey(@RequestParam String platformId) {
        // TODO: 添加管理员权限验证
        String apiCredentials = thirdPartyApiService.generateApiKey(platformId);
        return Result.success(apiCredentials);
    }
}

