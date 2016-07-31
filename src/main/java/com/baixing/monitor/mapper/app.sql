CREATE TABLE application (
  `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT
  COMMENT '自增id',
  `org_id`       INT(4)       NOT NULL
  COMMENT ' 应用所在部门ID',
  `app_name`     VARCHAR(255) NOT NULL
  COMMENT '应用名字',
  `app_host`     VARCHAR(255) NOT NULL
  COMMENT '应用地址 HOST:PORT [, ...]',
  `app_desc` VARCHAR(255) NOT NULL
  COMMENT '应用描述',
  `charger`      VARCHAR(255) NOT NULL
  COMMENT '负责人',
  `mail`         VARCHAR(255) NOT NULL
  COMMENT '邮箱,多个邮箱用,号分割',
  `created`      DATETIME     NOT NULL
  COMMENT '创建时间',
  `updated`      DATETIME     NOT NULL
  COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQE_NAME_ORGID` (`app_name`, `org_id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 18
  DEFAULT CHARSET = utf8mb4;