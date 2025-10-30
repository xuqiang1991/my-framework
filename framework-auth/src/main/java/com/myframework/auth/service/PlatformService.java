package com.myframework.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myframework.api.auth.dto.PlatformDTO;
import com.myframework.auth.entity.Platform;
import com.myframework.auth.mapper.PlatformMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformService {
    
    private final PlatformMapper platformMapper;
    
    /**
     * 获取所有可用平台列表
     */
    public List<PlatformDTO> getAllPlatforms() {
        LambdaQueryWrapper<Platform> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Platform::getStatus, 1)
                .eq(Platform::getDeleted, 0);
        List<Platform> platforms = platformMapper.selectList(wrapper);
        
        return platforms.stream()
                .map(platform -> {
                    PlatformDTO dto = new PlatformDTO();
                    BeanUtils.copyProperties(platform, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 根据平台ID获取平台信息
     */
    public PlatformDTO getPlatformById(String platformId) {
        Platform platform = platformMapper.selectById(platformId);
        if (platform == null) {
            return null;
        }
        
        PlatformDTO dto = new PlatformDTO();
        BeanUtils.copyProperties(platform, dto);
        return dto;
    }
    
    /**
     * 根据平台ID列表批量获取平台信息
     */
    public List<PlatformDTO> getPlatformsByIds(List<String> platformIds) {
        if (platformIds == null || platformIds.isEmpty()) {
            return List.of();
        }
        
        List<Platform> platforms = platformMapper.selectBatchIds(platformIds);
        
        return platforms.stream()
                .map(platform -> {
                    PlatformDTO dto = new PlatformDTO();
                    BeanUtils.copyProperties(platform, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

