package com.baixing.monitor.service;

import com.baixing.monitor.dao.InfluxDBDao;
import com.baixing.monitor.service.impl.DashServiceImpl;
import com.baixing.monitor.util.BXMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static com.baixing.monitor.util.HttpUtil.httpGet;


/**
 * Created by kofee on 16/7/24.
 */
public class GetAndWritePoints implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GetAndWritePoints.class);

    private static ApplicationContext applicationContext;

    private static final String APP_URL = "http://%s/monitor";
    private long orgId;
    private String appName;
    private String host;

    public GetAndWritePoints(Long orgId, String appName, String host) {
        this.orgId = orgId;
        this.appName = appName;
        this.host = host;
    }

    @Override
    public void run() {
        if (applicationContext == null) {
            BXMonitor.recordOne("context_null");
            return;
        }
        try {

            long begin = System.currentTimeMillis();

            Map<String, Object> currentItems = httpGet(String.format(APP_URL, host));

            InfluxDBDao influxDBDao = applicationContext.getBean(InfluxDBDao.class);

            influxDBDao.writePoints(Long.toString(orgId), appName, host, currentItems);

            logger.info("抓取监控数据,orgId={},appName={},host={},size={}", orgId, appName, host, currentItems.size());
            BXMonitor.recordOne("抓取监控数据成功", System.currentTimeMillis() - begin);

        } catch (Exception e) {
            logger.error("抓取监控数据失败,orgId={},appName={},host={}", orgId, appName, host, e);
            BXMonitor.recordOne("抓取监控失败");
        }

    }


    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
}
