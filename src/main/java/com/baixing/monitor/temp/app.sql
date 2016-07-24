CREATE TABLE application (
  `id`      BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `org_id`  INT(4)       NOT NULL,    #组织id
  `name`    VARCHAR(255) NOT NULL,    #应用名字
  `descr`   VARCHAR(255) NOT NULL,    #应用描述
  `duty`    VARCHAR(255) NOT NULL,    #负责人
  `mail`    VARCHAR(255) NOT NULL,    #邮箱
  `server`  VARCHAR(255) NOT NULL,    #应用地址 host:port
  `created` DATETIME     NOT NULL,    #创建时间
  `updated` DATETIME     NOT NULL,    #更新时间
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQE_NAME_ORGID` (`name`, `org_id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 18
  DEFAULT CHARSET = utf8mb4;