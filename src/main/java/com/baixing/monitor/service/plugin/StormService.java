package com.baixing.monitor.service.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baixing.monitor.util.HttpUtil;
import com.baixing.monitor.service.external.InfluxDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kofee on 16/8/24.
 */

@Service
public class StormService {

    @Value(value = "${storm.cluster.url}")
    private String stormUrl;

    @Value(value = "${storm.database}")
    private String database;

    @Value(value = "${storm.measurement}")
    private String measurement;

    @Autowired
    private InfluxDBService influxDBService;

    private static final String CLUSTER_SUMMARY = "/api/v1/cluster/summary";
    //private static final String SUPERVISOR_SUMMARY = "/api/v1/supervisor/summary";
    //private static final String NIMBUS_SUMMARY = "/api/v1/nimbus/summary";
    //private static final String TOPOLOGY_SUMMARY = "/api/v1/topology/summary";


    public void monitorStorm() {

        String cluster = HttpUtil.get(stormUrl + CLUSTER_SUMMARY);
        JSONObject jsonObject = JSON.parseObject(cluster);
        System.out.println(jsonObject);

        Map<String, Object> fieldMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                continue;
            }
            fieldMap.put(key, value);
        }

        influxDBService.writePoints(database, measurement, new HashMap<String, String>(), fieldMap);

    }
}
