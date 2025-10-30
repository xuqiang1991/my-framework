-- 创建Nacos数据库
CREATE DATABASE IF NOT EXISTS `nacos` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `nacos`;

/******************************************/
/*   Nacos 2.3.0 数据库表结构            */
/******************************************/

CREATE TABLE IF NOT EXISTS `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) DEFAULT NULL,
  `c_use` varchar(64) DEFAULT NULL,
  `effect` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `c_schema` text,
  `encrypted_data_key` text NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

CREATE TABLE IF NOT EXISTS `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) NOT NULL COMMENT 'datum_id',
  `content` longtext NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';

CREATE TABLE IF NOT EXISTS `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

CREATE TABLE IF NOT EXISTS `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

CREATE TABLE IF NOT EXISTS `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

CREATE TABLE IF NOT EXISTS `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

CREATE TABLE IF NOT EXISTS `his_config_info` (
  `id` bigint(20) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(50) DEFAULT NULL,
  `op_type` char(10) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';

CREATE TABLE IF NOT EXISTS `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';

CREATE TABLE IF NOT EXISTS `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE IF NOT EXISTS `users` (
  `username` varchar(50) NOT NULL PRIMARY KEY,
  `password` varchar(500) NOT NULL,
  `enabled` boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS `roles` (
  `username` varchar(50) NOT NULL,
  `role` varchar(50) NOT NULL,
  UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE IF NOT EXISTS `permissions` (
  `role` varchar(50) NOT NULL,
  `resource` varchar(255) NOT NULL,
  `action` varchar(8) NOT NULL,
  UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

-- 插入Nacos默认用户 (用户名: nacos, 密码: nacos)
INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);
INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');


-- 创建应用数据库
CREATE DATABASE IF NOT EXISTS `my_framework` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `my_framework`;

/******************************************/
/*   应用业务表结构                      */
/******************************************/

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 初始化用户（密码：123456）
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `nickname`, `real_name`, `status`)
VALUES 
('1', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', '系统管理员', 1),
('2', 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '普通用户', '测试用户', 1);

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `role_id` varchar(64) NOT NULL COMMENT '角色ID',
  `role_code` varchar(64) NOT NULL COMMENT '角色编码',
  `role_name` varchar(64) NOT NULL COMMENT '角色名称',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 初始化默认角色
INSERT INTO `sys_role` (`role_id`, `role_code`, `role_name`)
VALUES 
('1', 'ADMIN', '管理员'),
('2', 'USER', '普通用户');

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `role_id` varchar(64) NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 初始化用户角色关系
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
VALUES 
('1', '1'),
('2', '2');

/******************************************/
/*   单点登录（SSO）相关表结构            */
/******************************************/

-- 平台表（用于管理接入SSO的各个平台）
CREATE TABLE IF NOT EXISTS `sys_platform` (
  `platform_id` varchar(64) NOT NULL COMMENT '平台ID',
  `platform_code` varchar(64) NOT NULL COMMENT '平台编码',
  `platform_name` varchar(128) NOT NULL COMMENT '平台名称',
  `platform_url` varchar(512) DEFAULT NULL COMMENT '平台地址',
  `platform_desc` varchar(512) DEFAULT NULL COMMENT '平台描述',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`platform_id`),
  UNIQUE KEY `uk_platform_code` (`platform_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台表';

-- 用户平台关联表（支持一个用户关联多个平台，实现账号绑定）
CREATE TABLE IF NOT EXISTS `sys_user_platform` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `platform_id` varchar(64) NOT NULL COMMENT '平台ID',
  `platform_user_id` varchar(128) DEFAULT NULL COMMENT '平台用户ID',
  `platform_username` varchar(128) DEFAULT NULL COMMENT '平台用户名',
  `bind_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-解绑，1-已绑定',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_platform` (`user_id`, `platform_id`),
  KEY `idx_platform_user` (`platform_id`, `platform_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户平台关联表';

-- OAuth2客户端表（存储接入SSO的第三方应用信息）
CREATE TABLE IF NOT EXISTS `sys_oauth_client` (
  `client_id` varchar(64) NOT NULL COMMENT '客户端ID',
  `client_secret` varchar(256) NOT NULL COMMENT '客户端密钥',
  `client_name` varchar(128) NOT NULL COMMENT '客户端名称',
  `platform_id` varchar(64) NOT NULL COMMENT '关联的平台ID',
  `redirect_uris` varchar(1024) NOT NULL COMMENT '重定向URI列表（逗号分隔）',
  `grant_types` varchar(256) DEFAULT 'authorization_code,refresh_token' COMMENT '授权类型（逗号分隔）',
  `scope` varchar(256) DEFAULT 'read,write' COMMENT '授权范围',
  `access_token_validity` int(11) DEFAULT 3600 COMMENT '访问令牌有效期（秒）',
  `refresh_token_validity` int(11) DEFAULT 604800 COMMENT '刷新令牌有效期（秒）',
  `auto_approve` tinyint(1) DEFAULT 0 COMMENT '自动授权：0-需要用户确认，1-自动授权',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`client_id`),
  KEY `idx_platform_id` (`platform_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2客户端表';

-- OAuth2授权码表（存储授权码，用于授权码模式）
CREATE TABLE IF NOT EXISTS `sys_oauth_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `code` varchar(128) NOT NULL COMMENT '授权码',
  `client_id` varchar(64) NOT NULL COMMENT '客户端ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `redirect_uri` varchar(512) NOT NULL COMMENT '重定向URI',
  `scope` varchar(256) DEFAULT NULL COMMENT '授权范围',
  `state` varchar(128) DEFAULT NULL COMMENT '状态码（防CSRF攻击）',
  `expires_at` datetime NOT NULL COMMENT '过期时间',
  `used` tinyint(1) DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_client_user` (`client_id`, `user_id`),
  KEY `idx_expires` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2授权码表';

-- OAuth2令牌表（存储访问令牌和刷新令牌）
CREATE TABLE IF NOT EXISTS `sys_oauth_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `access_token` varchar(256) NOT NULL COMMENT '访问令牌',
  `refresh_token` varchar(256) DEFAULT NULL COMMENT '刷新令牌',
  `client_id` varchar(64) NOT NULL COMMENT '客户端ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `scope` varchar(256) DEFAULT NULL COMMENT '授权范围',
  `token_type` varchar(32) DEFAULT 'Bearer' COMMENT '令牌类型',
  `access_token_expires_at` datetime NOT NULL COMMENT '访问令牌过期时间',
  `refresh_token_expires_at` datetime DEFAULT NULL COMMENT '刷新令牌过期时间',
  `revoked` tinyint(1) DEFAULT 0 COMMENT '是否已撤销：0-有效，1-已撤销',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_access_token` (`access_token`),
  UNIQUE KEY `uk_refresh_token` (`refresh_token`),
  KEY `idx_client_user` (`client_id`, `user_id`),
  KEY `idx_access_expires` (`access_token_expires_at`),
  KEY `idx_refresh_expires` (`refresh_token_expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2令牌表';

-- 初始化测试平台和OAuth2客户端
INSERT INTO `sys_platform` (`platform_id`, `platform_code`, `platform_name`, `platform_url`, `platform_desc`)
VALUES 
('1', 'MAIN_APP', '主应用平台', 'http://localhost:8080', '主应用系统'),
('2', 'ADMIN_PLATFORM', '管理后台', 'http://localhost:8081', '管理后台系统'),
('3', 'MOBILE_APP', '移动端应用', 'http://localhost:8082', '移动端应用');

-- 初始化OAuth2测试客户端（密钥：secret123）
INSERT INTO `sys_oauth_client` (`client_id`, `client_secret`, `client_name`, `platform_id`, `redirect_uris`, `grant_types`, `scope`, `auto_approve`)
VALUES 
('main-app-client', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '主应用客户端', '1', 'http://localhost:8080/callback,http://localhost:8080/login/callback', 'authorization_code,refresh_token', 'read,write,user_info', 1),
('admin-client', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理后台客户端', '2', 'http://localhost:8081/callback', 'authorization_code,refresh_token', 'read,write,admin', 0),
('mobile-client', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '移动端客户端', '3', 'myapp://callback', 'authorization_code,refresh_token', 'read,write,user_info', 1);