package com.baixing.monitor.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

/**
 * Created by kofee on 16/7/21.
 */
@Data
@NoArgsConstructor
public class AppModel {

    private long id;

    //以后将是一个可选的选项 目前都为1
    private int orgId = 1;

    //应用名 同一个org下唯一  既作为应用的名字 必填
    private String name;

    //应用的描述  选填
    private String descr;

    //应用负责人  多个之间用,分割  必填
    private String duty;

    //小组邮箱 多个之间用,分割  必填
    private String mail;

    //应用部署的机器 多个之间用,分割 storm01:8080,st 必填
    private String server;

    private Date created = new Date();
    private Date updated = new Date();

}
