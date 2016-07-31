package com.baixing.monitor.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baixing.monitor.dao.InfluxDBDao;
import com.baixing.monitor.mapper.DashMapper;
import com.baixing.monitor.model.DashModel;
import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.service.DashService;
import com.baixing.monitor.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by kofee on 16/7/22.
 */
@Service
public class DashServiceImpl implements DashService {

    @Autowired
    private DashMapper dashMapper;

    @Autowired
    private InfluxDBDao influxDBDao;

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


    private static final String REFRUSH_URL = "http://localhost:10933/api/refresh?orgId=%s&appName=%s";

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
        dashJson.put("description", appModel.getAppDesc());


        JSONObject linkJson = JSON.parseObject(linkStr);
        linkJson.put("url", String.format(REFRUSH_URL, appModel.getOrgId(), appModel.getName()));
        links.add(linkJson);

        JSONObject row = JSON.parseObject(rowStr);
        row.put("title", appModel.getName());

        //添加一行
        rows.add(row);
        dashJson.put("rows", rows);
        dashJson.put("links", links);


        DashModel dashboard = new DashModel();
        dashboard.setVersion(0);
        dashboard.setSlug(appModel.getName());
        dashboard.setTitle(appModel.getName());
        dashboard.setData(dashJson.toString());
        dashboard.setOrgId(appModel.getOrgId());

        int result = dashMapper.addDashboard(dashboard);

        return result;
    }


    @Override
    public int addDashRow(JSONObject rowJson, long id) {
        return 0;
    }

    @Override
    public int addDashPanel(List<String> keyList, long orgId, String title) {
        DashModel dashboard = dashMapper.getDashboardById(orgId, title);

        JSONObject data = JSON.parseObject(dashboard.getData());

        JSONArray rows = data.getJSONArray("rows");

        JSONObject row = rows.getJSONObject(0);

        JSONArray panels = new JSONArray();

        int count = 1;
        for (String key : keyList) {
            String temp = panelStr;
            temp = temp.replace("app_name", title);
            temp = temp.replace("monitor_key", key);

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
    public int deleteDashPanel(long panelId, long dashId) {
        return 0;
    }

    @Override
    public int deleteRow(String title, long id) {
        return 0;
    }

    @Override
    public int refreshDashboard(long orgId, String appName) {

        //TODO 通过orgId 获取 database
        if (orgId == 1) {
            List<String> keyList = influxDBDao.getFildKeys("grafana", appName);
            return addDashPanel(keyList, orgId, appName);

        }
        return -1;
    }


}
