CREATE TABLE application (
  `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT
  COMMENT '自增id',
  `organization` INT(4)       NOT NULL
  COMMENT ' 应用所在组织ID',
  `group`        VARCHAR(255) NOT NULL
  COMMENT '应用所在组',
  `name`         VARCHAR(255) NOT NULL
  COMMENT '应用名字',
  `host`         VARCHAR(255) NOT NULL
  COMMENT '应用地址 HOST:PORT [, ...]',
  `style`        VARCHAR(16)  NOT NULL
  COMMENT '保存数据方式 ',
  `mode`         VARCHAR(16)  NOT NULL
  COMMENT 'pull or push',
  `describe`     VARCHAR(255) NOT NULL
  COMMENT '应用描述',
  `contact`      VARCHAR(255) NOT NULL
  COMMENT '联系人',
  `email`        VARCHAR(255) NOT NULL
  COMMENT '邮箱,多个邮箱用,号分割',
  `token`        VARCHAR(64)  NOT NULL
  COMMENT '应用的token',
  `created`      DATETIME     NOT NULL
  COMMENT '创建时间',
  `updated`      DATETIME     NOT NULL
  COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQE_NAME_GROUP` (`name`, `group`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 18
  DEFAULT CHARSET = utf8;