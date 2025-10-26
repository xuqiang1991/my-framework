package com.myframework.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token配置类
 */
@Configuration
public class SaTokenConfig {
    
    /**
     * 注册Sa-Token全局过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截所有路径
                .addInclude("/**")
                // 排除登录接口
                .addExclude("/auth/login", "/auth/captcha")
                // 排除静态资源
                .addExclude("/favicon.ico", "/actuator/**")
                // 排除接口文档
                .addExclude("/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**")
                // 鉴权规则
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由，并排除上面的路径
                    SaRouter.match("/**", r -> StpUtil.checkLogin());
                    
                    // 权限校验 -- 不同模块，不同权限
                    SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
                    SaRouter.match("/admin/**", r -> StpUtil.checkRole("admin"));
                })
                // 异常处理
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                });
    }
}

