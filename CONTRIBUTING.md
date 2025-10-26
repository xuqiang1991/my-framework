# 贡献指南

感谢你考虑为 My Framework 项目做出贡献！

## 📋 行为准则

- 尊重所有贡献者
- 使用友好和包容的语言
- 接受建设性的批评
- 关注对社区最有利的事情

## 🚀 如何贡献

### 报告Bug

如果你发现了Bug，请创建一个Issue并包含以下信息：

1. **Bug描述**：清晰简洁的描述问题
2. **复现步骤**：详细的步骤来复现问题
3. **期望行为**：你期望发生什么
4. **实际行为**：实际发生了什么
5. **环境信息**：
   - 操作系统和版本
   - Java版本
   - 项目版本
6. **截图**：如果适用，添加截图
7. **日志**：相关的错误日志

### 提出新功能

如果你有新功能的想法：

1. 先创建一个Issue讨论这个功能
2. 说明为什么需要这个功能
3. 描述你期望的行为
4. 如果可能，提供示例代码

### 提交代码

#### 1. Fork项目

点击项目页面右上角的Fork按钮

#### 2. 克隆仓库

```bash
git clone https://github.com/your-username/my-framework.git
cd my-framework
```

#### 3. 创建分支

```bash
git checkout -b feature/your-feature-name
# 或
git checkout -b fix/your-bug-fix
```

分支命名规范：
- `feature/xxx` - 新功能
- `fix/xxx` - Bug修复
- `docs/xxx` - 文档更新
- `refactor/xxx` - 代码重构
- `test/xxx` - 测试相关

#### 4. 进行更改

- 遵循项目的代码风格
- 添加必要的注释
- 更新相关文档
- 编写单元测试

#### 5. 提交更改

```bash
git add .
git commit -m "feat: 添加新功能描述"
```

提交信息规范：
- `feat:` - 新功能
- `fix:` - Bug修复
- `docs:` - 文档更新
- `style:` - 代码格式调整
- `refactor:` - 代码重构
- `test:` - 测试相关
- `chore:` - 构建/工具相关

#### 6. 推送到GitHub

```bash
git push origin feature/your-feature-name
```

#### 7. 创建Pull Request

1. 在GitHub上打开你的Fork
2. 点击"Pull Request"按钮
3. 填写PR描述：
   - 简要说明更改内容
   - 关联相关Issue
   - 列出测试情况
4. 提交PR

## 💻 代码规范

### Java代码风格

```java
// 1. 类名使用大驼峰
public class UserService {
    
    // 2. 常量使用全大写，下划线分隔
    private static final String DEFAULT_NAME = "admin";
    
    // 3. 变量和方法使用小驼峰
    private String userName;
    
    public String getUserName() {
        return userName;
    }
    
    // 4. 注释要清晰
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getUser(String userId) {
        // 实现代码
        return null;
    }
}
```

### 命名规范

- **包名**：小写，多个单词用点分隔
  ```
  com.myframework.user.service
  ```

- **类名**：大驼峰，名词
  ```java
  UserService, LoginRequest, ResultCode
  ```

- **接口名**：大驼峰，使用形容词或名词
  ```java
  UserMapper, Serializable
  ```

- **方法名**：小驼峰，动词开头
  ```java
  getUserById(), createUser(), isValid()
  ```

- **变量名**：小驼峰，名词
  ```java
  userName, userId, loginTime
  ```

- **常量名**：全大写，下划线分隔
  ```java
  MAX_SIZE, DEFAULT_TIMEOUT, TOKEN_PREFIX
  ```

### 注释规范

```java
/**
 * 用户服务类
 * 提供用户的增删改查功能
 * 
 * @author Your Name
 * @since 1.0.0
 */
public class UserService {
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID，不能为空
     * @return 用户信息，如果不存在返回null
     * @throws BusinessException 当用户ID格式错误时抛出
     */
    public User getUserById(String userId) {
        // 实现代码
    }
}
```

### 异常处理

```java
// 使用自定义业务异常
if (user == null) {
    throw new BusinessException(ResultCode.USER_NOT_FOUND);
}

// 记录日志
try {
    // 业务代码
} catch (Exception e) {
    log.error("操作失败: {}", e.getMessage(), e);
    throw new BusinessException("操作失败");
}
```

### 日志规范

```java
// 使用Slf4j
@Slf4j
public class UserService {
    
    public void createUser(User user) {
        log.info("创建用户: {}", user.getUsername());
        
        try {
            // 业务代码
            log.debug("用户数据: {}", user);
        } catch (Exception e) {
            log.error("创建用户失败: {}", e.getMessage(), e);
            throw new BusinessException("创建用户失败");
        }
    }
}
```

## 🧪 测试

### 单元测试

```java
@SpringBootTest
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    void testGetUserById() {
        // given
        String userId = "1";
        
        // when
        User user = userService.getUserById(userId);
        
        // then
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }
}
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest

# 运行指定测试方法
mvn test -Dtest=UserServiceTest#testGetUserById
```

## 📝 文档

### 更新文档

如果你的更改影响到：
- API接口 - 更新 `API.md`
- 部署流程 - 更新 `DEPLOY.md`
- 功能特性 - 更新 `README.md`
- 版本历史 - 更新 `CHANGELOG.md`

### API文档

使用Swagger注解：

```java
@Tag(name = "用户管理", description = "用户增删改查")
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{userId}")
    public Result<User> getUser(
            @Parameter(description = "用户ID") @PathVariable String userId) {
        // 实现代码
    }
}
```

## 🔍 代码审查

Pull Request会经过以下审查：

1. **代码质量**
   - 是否符合代码规范
   - 是否有适当的注释
   - 是否有重复代码

2. **功能实现**
   - 是否正确实现了功能
   - 是否处理了异常情况
   - 是否考虑了性能

3. **测试覆盖**
   - 是否有单元测试
   - 测试是否充分
   - 是否通过了所有测试

4. **文档更新**
   - 是否更新了相关文档
   - API文档是否完整
   - 注释是否清晰

## ⚡ 开发建议

### 环境配置

1. **IDE推荐**
   - IntelliJ IDEA
   - Visual Studio Code

2. **必装插件**
   - Lombok
   - MyBatis Plus
   - RestfulTool
   - Maven Helper

3. **代码格式化**
   - 使用项目统一的代码格式
   - 提交前格式化代码

### 开发流程

1. **开始前**
   - 确保本地代码是最新的
   - 创建新分支
   - 理解需求

2. **开发中**
   - 频繁提交
   - 写清晰的提交信息
   - 定期拉取主分支更新

3. **完成后**
   - 自测功能
   - 运行测试
   - 更新文档
   - 创建PR

### 性能考虑

- 避免N+1查询
- 合理使用缓存
- 注意事务范围
- 避免大事务

### 安全考虑

- 验证所有输入
- 避免SQL注入
- 不要硬编码密码
- 敏感信息加密

## 🎯 优先级

高优先级：
- 安全漏洞修复
- 严重Bug修复
- 性能优化

中优先级：
- 新功能开发
- 文档完善
- 代码重构

低优先级：
- 代码风格调整
- 注释完善
- 示例代码

## 📮 联系方式

- **Issue**: 在GitHub上创建Issue
- **讨论**: 使用GitHub Discussions
- **邮件**: 发送至项目维护者

## 🙏 致谢

感谢所有为项目做出贡献的人！

你的贡献让这个项目变得更好！💪

