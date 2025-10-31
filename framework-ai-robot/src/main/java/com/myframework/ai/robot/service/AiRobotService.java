package com.myframework.ai.robot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myframework.ai.robot.entity.AiRobot;
import com.myframework.ai.robot.mapper.AiRobotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI机器人管理服务
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiRobotService {

    private final AiRobotMapper robotMapper;

    /**
     * 创建机器人
     */
    public AiRobot createRobot(AiRobot robot, String userId) {
        robot.setCreateBy(userId);
        robot.setCreateTime(LocalDateTime.now());
        robot.setUpdateTime(LocalDateTime.now());
        robotMapper.insert(robot);
        return robot;
    }

    /**
     * 更新机器人
     */
    public void updateRobot(AiRobot robot) {
        robot.setUpdateTime(LocalDateTime.now());
        robotMapper.updateById(robot);
    }

    /**
     * 删除机器人
     */
    public void deleteRobot(String robotId) {
        robotMapper.deleteById(robotId);
    }

    /**
     * 获取机器人详情
     */
    public AiRobot getRobot(String robotId) {
        return robotMapper.selectById(robotId);
    }

    /**
     * 获取公开的机器人列表
     */
    public List<AiRobot> listPublicRobots() {
        return robotMapper.selectList(
            new LambdaQueryWrapper<AiRobot>()
                .eq(AiRobot::getIsPublic, 1)
                .eq(AiRobot::getStatus, 1)
                .orderByDesc(AiRobot::getCreateTime)
        );
    }

    /**
     * 获取用户创建的机器人列表
     */
    public List<AiRobot> listUserRobots(String userId) {
        return robotMapper.selectList(
            new LambdaQueryWrapper<AiRobot>()
                .eq(AiRobot::getCreateBy, userId)
                .orderByDesc(AiRobot::getCreateTime)
        );
    }

    /**
     * 分页查询机器人
     */
    public Page<AiRobot> pageRobots(int pageNum, int pageSize, String keyword) {
        Page<AiRobot> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AiRobot> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                .like(AiRobot::getRobotName, keyword)
                .or()
                .like(AiRobot::getRobotDesc, keyword)
            );
        }
        
        wrapper.eq(AiRobot::getStatus, 1)
               .orderByDesc(AiRobot::getCreateTime);
        
        return robotMapper.selectPage(page, wrapper);
    }
}

