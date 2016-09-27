package com.superbool.monitor.service;

import com.superbool.monitor.model.AppModel;
import com.superbool.monitor.service.external.InfluxDBService;
import com.superbool.monitor.util.Monitor;
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
    private InfluxDBService influxDBService;

    @Autowired
    private AppService appService;


    private static final String APP_URL = "http://%s/monitor";


    //全局记录应用信息
    private static List<AppModel> appPullList = new ArrayList<>();


    //应用启动的时候从数据库读取
    @PostConstruct
    public void getAppServer() {
        appPullList = appService.getAllPullApp();

    }

    public void pullData() {

        logger.info("开始拉监控数据, 应用数量={}", appPullList.size());

        long begin = System.currentTimeMillis();
        if (appPullList.isEmpty()) {
            Monitor.recordOne("应用数量为空");
            return;
        }

        appPullList.forEach(app -> {

            String group = app.getGroup();
            String name = app.getName();
            String hosts = app.getHost();

            //当有多台机器时要都去抓取
            Iterable<String> temp = Splitter.on(",").split(hosts);

            for (String host : temp) {
                getAndWritePoints(group, name, host);
            }
        });
        logger.info("结束一次拉取,花费总时间={}", System.currentTimeMillis() - begin);
        Monitor.recordOne("启动一次拉取监控", System.currentTimeMillis() - begin);
    }

    @Async
    private void getAndWritePoints(String group, String appName, String host) {
        try {

            long begin = System.currentTimeMillis();

            //通过http抓数
            Map<String, Object> currentItems = null;// = HttpUtil.get(String.format(APP_URL, host));

            if (currentItems == null || currentItems.isEmpty()) {
                logger.warn("没有抓取到监控数据,group={},appName={},host={},size={}", group, appName, host, currentItems.size());
                return;
            }

            influxDBService.writePoints(group, appName, host, currentItems);

            logger.info("抓取监控数据,group={},appName={},host={},size={}", group, appName, host, currentItems.size());
            Monitor.recordOne("抓取监控数据成功", System.currentTimeMillis() - begin);

        } catch (Exception e) {
            logger.error("抓取监控数据失败,orgId={},appName={},host={}", group, appName, host, e);
            Monitor.recordOne("抓取监控失败");
        }
    }


    public static void addApp(AppModel app) {
        appPullList.add(app);
    }


}
