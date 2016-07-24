package com.baixing.monitor.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baixing.monitor.dao.InfluxdbDao;
import com.baixing.monitor.mapper.DashMapper;
import com.baixing.monitor.model.DashModel;
import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.service.DashService;
import com.baixing.monitor.util.FileUtil;
import com.sun.tools.javac.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kofee on 16/7/22.
 */
@Service
public class DashServiceImpl implements DashService {

    @Autowired
    private DashMapper dashMapper;

    @Autowired
    private InfluxdbDao influxdbDao;

    private String dashStr;
    private String rowStr;
    private String panelStr;
    private String linkStr;

    @PostConstruct
    public void init() {
        dashStr = FileUtil.getFileToJson("dashboard.json");
        rowStr = FileUtil.getFileToJson("row.json");
        panelStr = FileUtil.getFileToJson("panel.json");
        linkStr = FileUtil.getFileToJson("link.json");
    }


    private static final String REFRUSH_URL = "http://localhost:10933/refresh?orgId=%s&appName=%s";

    /**
     * 每个dashboard是一个应用,每个应用只建一行（后面有需要再扩充）  每行建立多个panel
     * 创建一个空的dashboard
     *
     * @param appModel
     * @return
     */
    @Override
    public int addDashboard(AppModel appModel) {
        JSONArray rows = new JSONArray();
        JSONArray links = new JSONArray();

        JSONObject dashJson = JSON.parseObject(dashStr);
        dashJson.put("title", appModel.getName());
        dashJson.put("description", appModel.getDescr());


        JSONObject linkJson = JSON.parseObject(linkStr);
        linkJson.put("url", String.format(REFRUSH_URL, appModel.getOrgId(), appModel.getName()));
        links.add(linkJson);

        JSONObject row1 = JSON.parseObject(rowStr);
        row1.put("title", appModel.getName());

        //添加一行
        rows.add(row1);
        dashJson.put("rows", rows);
        dashJson.put("links", links);


        DashModel dashboard = new DashModel();
        dashboard.setVersion(0);
        dashboard.setSlug(appModel.getName());
        dashboard.setTitle(appModel.getName());
        dashboard.setData(dashJson.toString());
        dashboard.setOrgId(appModel.getOrgId());

        int result = dashMapper.insertADashboard(dashboard);

        return result;
    }


    @Override
    public int addDashRow(JSONObject rowJson, int id) {
        return 0;
    }

    @Override
    public int addDashPanel(Set<String> panelKeySet, int orgId, String title) {
        DashModel dashboard = dashMapper.getDashboardById(orgId, title);

        JSONObject data = JSON.parseObject(dashboard.getData());

        JSONArray rows = data.getJSONArray("rows");

        JSONObject row = rows.getJSONObject(0);

        JSONArray panels = new JSONArray();

        int count = 1;
        for (String panelKey : panelKeySet) {
            String temp = panelStr;
            temp = temp.replace("app_name", title);
            temp = temp.replace("panel_key", panelKey);

            JSONObject panel = JSON.parseObject(temp);
            panel.put("id", count++);
            //添加一个图
            panels.add(panel);
        }
        row.put("panels", panels);


        int result = dashMapper.updateDataById(data.toString(), dashboard.getId());

        return result;
    }

    @Override
    public int deleteDashPanel(int panelId, int dashId) {
        return 0;
    }

    @Override
    public int deleteRow(String title, int id) {
        return 0;
    }

    @Override
    public int refreshDashboard(String orgId, String appName) {


        Pair<String, String> key = new Pair<>(orgId, appName);
        Set<String> keySet = monitorKeyMap.get(key);
        System.out.println(keySet);
        int result = addDashPanel(keySet, Integer.parseInt(orgId), appName);

        return result;
    }

    private static Map<Pair<String, String>, Set<String>> monitorKeyMap = new HashMap<>();

    public static void refrushMap(String orgId, String appName, Set<String> keySet) {
        Pair<String, String> key = new Pair<>(orgId, appName);
        monitorKeyMap.put(key, keySet);
    }

}
