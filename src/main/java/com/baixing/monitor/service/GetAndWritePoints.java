package com.baixing.monitor.service;

import com.baixing.monitor.dao.InfluxdbDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.baixing.monitor.util.HttpUtil.httpGet;

/**
 * Created by kofee on 16/7/24.
 */
public class GetAndWritePoints implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GetAndWritePoints.class);

    private static ApplicationContext applicationContext;

    private static final String APP_URL = "http://%s/monitor";
    private int orgId;
    private String appName;
    private String host;

    public GetAndWritePoints(Integer orgId, String appName, String host) {
        this.orgId = orgId;
        this.appName = appName;
        this.host = host;
    }

    @Override
    public void run() {
        try {
            logger.info("抓取监控数据,orgId={},appName={},server={}", orgId, appName, host);

            //Map<String, Object> currentItems = httpGet(String.format(APP_URL, host));

            Map<String, Object> currentItems = getMock();

            InfluxdbDao influxdbDao = applicationContext.getBean(InfluxdbDao.class);

            influxdbDao.writeMap(Integer.toString(orgId), appName, host, currentItems);


        } catch (Exception e) {
            logger.error("抓取监控数据失败,orgId={},appName={},server={}", orgId, appName, host, e);
        }

    }


    public static Map<String, Object> getMock() {
        Map<String, Object> monitor = new HashMap<>();
        monitor.put("key1", 1L);
        monitor.put("key2", 2L);
        monitor.put("key3", 3L);
        return monitor;
    }

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;

    }
}
