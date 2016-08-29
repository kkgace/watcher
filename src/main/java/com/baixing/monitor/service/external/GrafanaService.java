package com.baixing.monitor.service.external;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.util.FileUtil;
import com.baixing.monitor.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;


/**
 * Created by kofee on 16/8/28.
 */
@Service
public class GrafanaService {
    private static final Logger logger = LoggerFactory.getLogger(GrafanaService.class);

    @Value(value = "${grafana.url}")
    private String grafanaUrl;
    @Value(value = "${grafana.api.key}")
    private String apiKey;

    //curl -H "Authorization: Bearer your_key_above" http://your.grafana.com/api/dashboards/db/mydash

    private static final String API_DASHBOARD = "/api/dashboards/db";

    private static final String BEARER = "Bearer ";

    @Autowired
    InfluxDBService influxDBService;


    private String dashStr;
    private String rowStr;
    private String panelStr;
    private String linkStr;

    @PostConstruct
    public void init() {
        dashStr = FileUtil.getFileToStr("dashboard.json");
        rowStr = FileUtil.getFileToStr("row.json");
        panelStr = FileUtil.getFileToStr("panel.json");
        linkStr = FileUtil.getFileToStr("link.json");
    }

    //根据应用名称查询生成的监控数据
    public String getDashboard(String appName) {
        String result = HttpUtil.get(grafanaUrl + API_DASHBOARD + "/" + appName, BEARER + apiKey);
        return result;
    }


    //创建一个dashboard
    public String crateDashboard(AppModel appModel) {
        //生成grafana的json数据
        JSONObject grafana = joinDashJson(appModel);
        //通过api接口创建dashboard
        String result = HttpUtil.post(grafanaUrl + API_DASHBOARD, grafana.toString(), BEARER + apiKey);

        return result;
    }

    public int deleteDashboard(String appName) {
        int result = HttpUtil.delete(grafanaUrl + API_DASHBOARD + "/" + appName, BEARER + apiKey);
        return result;
    }


    //生成grafana 的json格式
    private JSONObject joinDashJson(AppModel appModel) {

        JSONObject dashJson = JSON.parseObject(dashStr);
        dashJson.put("title", appModel.getName());
        dashJson.put("description", appModel.getDescribe());

        JSONObject linkJson = JSON.parseObject(linkStr);
        String ip = "";
        try {
            /**返回本地主机。*/
            InetAddress addr = InetAddress.getLocalHost();
            /**返回 IP 地址字符串（以文本表现形式）*/
            ip = addr.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("未获取到本机地址", e);
        }
        JSONArray links = new JSONArray();
        links.add(linkJson);

        JSONObject row = JSON.parseObject(rowStr);
        row.put("title", appModel.getName());

        List<String> fieldList = influxDBService.getFieldKeys(appModel.getGroup(), appModel.getName());
        JSONArray panels = joinPanelJson(appModel.getGroup(), appModel.getName(), fieldList);

        row.put("panels", panels);

        JSONArray rows = new JSONArray();
        //添加一行
        rows.add(row);
        dashJson.put("rows", rows);
        dashJson.put("links", links);

        JSONObject grafana = new JSONObject();
        grafana.put("dashboard", dashJson);
        grafana.put("overwrite", true);

        return grafana;
    }

    private JSONArray joinPanelJson(String database, String title, List<String> fieldList) {
        JSONArray panels = new JSONArray();
        int id = 1;
        for (String key : fieldList) {
            String panel = panelStr;
            panel = panel.replace("ApplicationName", title);
            panel = panel.replace("MonitorField", key);

            JSONObject panelJson = JSON.parseObject(panel);
            panelJson.put("datasource", database);
            panelJson.put("id", id++);
            //添加一个图
            panels.add(panelJson);
        }

        return panels;
    }
}

