package com.myframework.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myframework.auth.entity.Platform;
import org.apache.ibatis.annotations.Mapper;

/**
 * 平台Mapper接口
 */
@Mapper
public interface PlatformMapper extends BaseMapper<Platform> {
}

