package com.superbool.monitor.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by kofee on 16/7/21.
 */
@Data
@NoArgsConstructor
public class AppModel {

    private long id;

    //部门组织id,选择
    private int organization;

    //应用所在的分组,自定义
    private String group;

    //应用名 同一个group下是唯一必填
    private String name;

    //应用部署的机器 多个之间用','分割,比如:storm01:8080,storm02:8080 必填
    private String host;

    //存储数据的方式 是聚合存储还是分别存储  gather or disperse
    private String style;

    //是将数据推送过来还是watcher进行拉取  push  or pull 默认为pull
    private String mode;

    //应用的描述  选填
    private String describe;

    //应用联系人姓名  多个之间用,分割  必填
    private String contact;

    //联系人邮箱 多个之间用,分割  必填
    private String email;

    //应用的token 供推数据的时候使用
    private String token;

    private Date created = new Date();
    private Date updated = new Date();

}
