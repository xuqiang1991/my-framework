package com.myframework.api.auth;

import com.myframework.api.auth.dto.PlatformDTO;
import com.myframework.common.result.Result;

import java.util.List;

/**
 * 平台管理API接口
 */
public interface PlatformApi {
    
    /**
     * 获取所有可用平台列表
     * @return 平台列表
     */
    Result<List<PlatformDTO>> getAllPlatforms();
    
    /**
     * 根据平台ID获取平台信息
     * @param platformId 平台ID
     * @return 平台信息
     */
    Result<PlatformDTO> getPlatformById(String platformId);
    
    /**
     * 根据平台ID列表批量获取平台信息
     * @param platformIds 平台ID列表
     * @return 平台列表
     */
    Result<List<PlatformDTO>> getPlatformsByIds(List<String> platformIds);
}

