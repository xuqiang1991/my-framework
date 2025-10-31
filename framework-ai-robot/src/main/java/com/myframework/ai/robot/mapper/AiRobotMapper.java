package com.myframework.ai.robot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myframework.ai.robot.entity.AiRobot;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI机器人Mapper
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Mapper
public interface AiRobotMapper extends BaseMapper<AiRobot> {
}

