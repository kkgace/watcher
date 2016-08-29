package com.baixing.monitor.service;

import com.alibaba.fastjson.JSONObject;
import com.baixing.monitor.mapper.AppMapper;
import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.model.ResponseModel;
import com.baixing.monitor.service.external.InfluxDBService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kofee on 16/8/27.
 */
@Service
public class PushDataService {

    @Autowired
    private AppMapper appMapper;
    @Autowired
    private InfluxDBService influxDBService;

    //全局记录应用信息
    private static Map<String, AppModel> appPushMap = new HashMap<>();

    //检查token是否有效
    public AppModel checkToken(String token) {

        AppModel appModel = appPushMap.get(token);

        if (appModel != null && appModel.getToken().equals(token)) {
            return appModel;
        } else {
            appModel = appMapper.getByToken(token);
            if (appModel != null && appModel.getToken().equals(token)) {
                appPushMap.put(token, appModel);
                return appModel;
            } else {
                return null;
            }
        }
    }


    //校验token并且写入InfluxDB
    public ResponseModel writePushMetric(JSONObject metricJson) {
        ResponseModel response = new ResponseModel(-1, "服务器错误");

        String token = metricJson.getString("token");
        String host = metricJson.getString("host");

        if (Strings.isNullOrEmpty(host)) {
            response.setMsg("host不能为空");
            return response;
        }

        AppModel appModel = checkToken(token);

        if (appModel == null) {
            response.setMsg("未注册 token=" + token);
        } else {
            JSONObject metric = metricJson.getJSONObject("metric");

            Map<String, Object> fieldMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : metric.entrySet()) {
                fieldMap.put(entry.getKey(), entry.getValue());
            }

            influxDBService.writePoints(appModel.getGroup(), appModel.getName(), host, fieldMap);
            response.setCode(0);
            response.setMsg("成功");
        }

        return response;
    }
}
