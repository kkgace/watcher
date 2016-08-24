package com.baixing.monitor.service;

import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.util.BxMonitor;
import com.baixing.monitor.util.InfluxDBClient;
import com.baixing.monitor.util.OrgEnum;
import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kofee on 16/8/24.
 */
@Service
public class PullDataService {

    private static final Logger logger = LoggerFactory.getLogger(PullDataService.class);

    @Autowired
    private InfluxDBClient influxDBClient;

    @Autowired
    private AppService appService;


    private static final String APP_URL = "http://%s/monitor";


    //全局记录应用信息
    private static List<AppModel> appList = new ArrayList<>();


    //应用启动的时候从数据库读取
    @PostConstruct
    public void getAppServer() {
        appList = appService.getAllApp();

    }

    public void pullData() {

        logger.info("开始拉监控数据, 应用数量={}", appList.size());

        long begin = System.currentTimeMillis();
        if (appList.isEmpty()) {
            BxMonitor.recordOne("应用数量为空");
            return;
        }

        appList.forEach(app -> {

            Long orgId = app.getOrgId();
            String name = app.getName();
            String hosts = app.getHost();

            //当有多台机器时要都去抓取
            Iterable<String> temp = Splitter.on(",").split(hosts);

            for (String host : temp) {
                getAndWritePoints(orgId, name, host);
            }
        });
        logger.info("结束一次拉取,花费总时间={}", System.currentTimeMillis() - begin);
        BxMonitor.recordOne("启动一次拉取监控", System.currentTimeMillis() - begin);
    }

    @Async
    private void getAndWritePoints(Long orgId, String appName, String host) {
        try {

            long begin = System.currentTimeMillis();

            //通过http抓数
            Map<String, Object> currentItems = null;// = HttpUtil.get(String.format(APP_URL, host));

            if (currentItems == null || currentItems.isEmpty()) {
                logger.warn("没有抓取到监控数据,orgId={},appName={},host={},size={}", orgId, appName, host, currentItems.size());
                return;
            }

            String database = OrgEnum.valueOf(Math.toIntExact(orgId)).getDatabase();

            influxDBClient.writePoints(database, appName, host, currentItems);

            logger.info("抓取监控数据,orgId={},appName={},host={},size={}", orgId, appName, host, currentItems.size());
            BxMonitor.recordOne("抓取监控数据成功", System.currentTimeMillis() - begin);

        } catch (Exception e) {
            logger.error("抓取监控数据失败,orgId={},appName={},host={}", orgId, appName, host, e);
            BxMonitor.recordOne("抓取监控失败");
        }
    }


    public static void addApp(AppModel app) {
        appList.add(app);
    }


}
