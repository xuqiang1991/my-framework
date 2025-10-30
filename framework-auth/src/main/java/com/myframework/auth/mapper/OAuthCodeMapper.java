package com.myframework.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myframework.auth.entity.OAuthCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * OAuth2授权码Mapper接口
 */
@Mapper
public interface OAuthCodeMapper extends BaseMapper<OAuthCode> {
}

