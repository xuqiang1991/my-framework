package com.myframework.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myframework.user.entity.ApiCredential;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * API凭证Mapper接口
 */
@Mapper
public interface ApiCredentialMapper extends BaseMapper<ApiCredential> {
    
    /**
     * 根据API Key查询凭证
     */
    @Select("SELECT * FROM sys_api_credential WHERE api_key = #{apiKey}")
    ApiCredential selectByApiKey(@Param("apiKey") String apiKey);
}

