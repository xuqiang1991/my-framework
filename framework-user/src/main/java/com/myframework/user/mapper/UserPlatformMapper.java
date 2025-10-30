package com.myframework.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myframework.user.entity.UserPlatform;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户平台关联Mapper接口
 */
@Mapper
public interface UserPlatformMapper extends BaseMapper<UserPlatform> {
}

