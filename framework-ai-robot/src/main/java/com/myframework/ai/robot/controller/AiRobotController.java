package com.myframework.ai.robot.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myframework.ai.robot.entity.AiRobot;
import com.myframework.ai.robot.service.AiRobotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI机器人管理控制器
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Tag(name = "AI机器人管理", description = "AI机器人的创建、配置和管理")
@RestController
@RequestMapping("/api/ai/robot")
@RequiredArgsConstructor
public class AiRobotController {

    private final AiRobotService robotService;

    /**
     * 创建机器人
     */
    @Operation(summary = "创建AI机器人")
    @PostMapping
    public AiRobot createRobot(@RequestBody AiRobot robot) {
        String userId = StpUtil.getLoginIdAsString();
        return robotService.createRobot(robot, userId);
    }

    /**
     * 更新机器人
     */
    @Operation(summary = "更新AI机器人")
    @PutMapping
    public void updateRobot(@RequestBody AiRobot robot) {
        robotService.updateRobot(robot);
    }

    /**
     * 删除机器人
     */
    @Operation(summary = "删除AI机器人")
    @DeleteMapping("/{robotId}")
    public void deleteRobot(@PathVariable String robotId) {
        robotService.deleteRobot(robotId);
    }

    /**
     * 获取机器人详情
     */
    @Operation(summary = "获取机器人详情")
    @GetMapping("/{robotId}")
    public AiRobot getRobot(@PathVariable String robotId) {
        return robotService.getRobot(robotId);
    }

    /**
     * 获取公开的机器人列表
     */
    @Operation(summary = "获取公开的机器人列表")
    @GetMapping("/public")
    public List<AiRobot> listPublicRobots() {
        return robotService.listPublicRobots();
    }

    /**
     * 获取用户创建的机器人列表
     */
    @Operation(summary = "获取用户创建的机器人列表")
    @GetMapping("/my")
    public List<AiRobot> listMyRobots() {
        String userId = StpUtil.getLoginIdAsString();
        return robotService.listUserRobots(userId);
    }

    /**
     * 分页查询机器人
     */
    @Operation(summary = "分页查询机器人")
    @GetMapping("/page")
    public Page<AiRobot> pageRobots(
        @RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) String keyword
    ) {
        return robotService.pageRobots(pageNum, pageSize, keyword);
    }
}

