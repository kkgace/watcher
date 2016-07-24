package com.baixing.monitor.service;

import com.alibaba.fastjson.JSONObject;
import com.baixing.monitor.model.AppModel;

import java.util.Set;

/**
 * Created by kofee on 16/7/22.
 */
public interface DashService {

    //新建一个dashboard
    int addDashboard(AppModel appModel);

    //添加一个行
    int addDashRow(JSONObject rowJson, int id);

    //添加图
    int addDashPanel(Set<String> panelKeySet, int orgId,String title);

    int deleteDashPanel(int panelId, int dashId);


    int deleteRow(String title, int id);

    int refreshDashboard(String orgId, String appName);
}
