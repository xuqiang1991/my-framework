package com.myframework.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myframework.auth.entity.OAuthToken;
import org.apache.ibatis.annotations.Mapper;

/**
 * OAuth2令牌Mapper接口
 */
@Mapper
public interface OAuthTokenMapper extends BaseMapper<OAuthToken> {
}

