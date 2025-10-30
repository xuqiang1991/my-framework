package com.myframework.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myframework.auth.entity.OAuthClient;
import org.apache.ibatis.annotations.Mapper;

/**
 * OAuth2客户端Mapper接口
 */
@Mapper
public interface OAuthClientMapper extends BaseMapper<OAuthClient> {
}

