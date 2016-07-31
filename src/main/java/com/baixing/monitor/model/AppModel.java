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

    //部门组织id
    private long orgId;

    //应用名 同一个org下唯一  既作为应用的名字 必填
    private String name;

    //应用部署的机器 多个之间用,分割 storm01:8080 必填
    private String host;

    //应用的描述  选填
    private String appDesc;

    //应用负责人  多个之间用,分割  必填
    private String charger;

    //小组邮箱 多个之间用,分割  必填
    private String mail;

    private Date created = new Date();
    private Date updated = new Date();

}
