package com.myframework.user.controller;

import com.myframework.common.result.Result;
import com.myframework.user.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 验证码控制器
 */
@Tag(name = "验证码管理", description = "图形验证码生成")
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    
    private final CaptchaService captchaService;
    
    /**
     * 获取图形验证码
     * @return 验证码key和base64图片
     */
    @Operation(summary = "获取图形验证码")
    @GetMapping("/generate")
    public Result<Map<String, String>> generateCaptcha() {
        Map<String, String> captcha = captchaService.generateCaptcha();
        return Result.success(captcha);
    }
}

