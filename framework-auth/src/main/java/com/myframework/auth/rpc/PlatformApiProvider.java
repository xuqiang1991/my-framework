package com.myframework.auth.rpc;

import com.myframework.api.auth.PlatformApi;
import com.myframework.api.auth.dto.PlatformDTO;
import com.myframework.auth.service.PlatformService;
import com.myframework.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 平台API RPC提供者
 */
@Slf4j
@DubboService
@RequiredArgsConstructor
public class PlatformApiProvider implements PlatformApi {
    
    private final PlatformService platformService;
    
    @Override
    public Result<List<PlatformDTO>> getAllPlatforms() {
        try {
            List<PlatformDTO> platforms = platformService.getAllPlatforms();
            return Result.success(platforms);
        } catch (Exception e) {
            log.error("获取所有平台列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<PlatformDTO> getPlatformById(String platformId) {
        try {
            PlatformDTO platform = platformService.getPlatformById(platformId);
            return Result.success(platform);
        } catch (Exception e) {
            log.error("根据平台ID获取平台信息失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<List<PlatformDTO>> getPlatformsByIds(List<String> platformIds) {
        try {
            List<PlatformDTO> platforms = platformService.getPlatformsByIds(platformIds);
            return Result.success(platforms);
        } catch (Exception e) {
            log.error("批量获取平台信息失败", e);
            return Result.error(e.getMessage());
        }
    }
}

