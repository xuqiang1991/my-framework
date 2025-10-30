package com.myframework.user.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 * 生成图形验证码并存储到Redis中
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {
    
    private final StringRedisTemplate redisTemplate;
    
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final int CAPTCHA_EXPIRE_MINUTES = 5; // 验证码5分钟过期
    private static final int CAPTCHA_WIDTH = 130;
    private static final int CAPTCHA_HEIGHT = 48;
    private static final int CAPTCHA_CODE_LENGTH = 4;
    private static final int CAPTCHA_CIRCLE_COUNT = 3;
    
    /**
     * 生成验证码
     * @return 包含验证码key和base64图片的Map
     */
    public Map<String, String> generateCaptcha() {
        // 生成圆圈干扰的验证码
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(
                CAPTCHA_WIDTH, 
                CAPTCHA_HEIGHT, 
                CAPTCHA_CODE_LENGTH, 
                CAPTCHA_CIRCLE_COUNT
        );
        
        String code = captcha.getCode();
        String imageBase64 = captcha.getImageBase64Data();
        
        // 生成唯一的验证码key
        String captchaKey = IdUtil.simpleUUID();
        
        // 存储验证码到Redis（5分钟过期）
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisTemplate.opsForValue().set(
                redisKey, 
                code.toLowerCase(), // 存储小写，验证时不区分大小写
                CAPTCHA_EXPIRE_MINUTES, 
                TimeUnit.MINUTES
        );
        
        log.debug("生成验证码: key={}, code={}", captchaKey, code);
        
        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("captchaImage", imageBase64);
        
        return result;
    }
    
    /**
     * 验证验证码
     * @param captchaKey 验证码key
     * @param captchaCode 用户输入的验证码
     * @return true-验证成功，false-验证失败
     */
    public boolean verifyCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            log.warn("验证码参数为空");
            return false;
        }
        
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        
        if (storedCode == null) {
            log.warn("验证码不存在或已过期: key={}", captchaKey);
            return false;
        }
        
        // 验证码只能使用一次，验证后立即删除
        redisTemplate.delete(redisKey);
        
        boolean isValid = storedCode.equalsIgnoreCase(captchaCode.trim());
        
        if (isValid) {
            log.info("验证码验证成功: key={}", captchaKey);
        } else {
            log.warn("验证码验证失败: key={}, expected={}, actual={}", 
                    captchaKey, storedCode, captchaCode);
        }
        
        return isValid;
    }
}

