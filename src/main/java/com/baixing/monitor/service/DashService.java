package com.baixing.monitor.service;

import com.alibaba.fastjson.JSONObject;
import com.baixing.monitor.model.AppModel;

import java.util.List;

/**
 * Created by kofee on 16/7/22.
 */
public interface DashService {

    //新建一个dashboard
    int addDashboard(AppModel appModel);

    //添加一个行
    int addDashRow(JSONObject rowJson, long id);

    //添加图
    int addDashPanel(List<String> panelKeySet, long orgId, String title);

    int deleteDashPanel(long panelId, long dashId);

    int deleteRow(String title, long id);

    //刷新图
    int refreshDashboard(long orgId, String appName);
}
