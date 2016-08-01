package com.baixing.monitor.service;

import com.baixing.monitor.dao.InfluxDBDao;
import com.baixing.monitor.mapper.DashMapper;
import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.util.BXMonitor;
import com.baixing.monitor.util.OrgEnum;
import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.baixing.monitor.util.HttpUtil.httpGet;


/**
 * Created by kofee on 16/7/20.
 */
@Component
public class TaskService implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);


    @Autowired
    private AppService appService;

    @Autowired
    private InfluxDBDao influxDBDao;


    //全局记录应用信息
    private static List<AppModel> appList = new ArrayList<>();


    //应用启动的时候从数据库读取
    @PostConstruct
    public void getAppServer() {
        appList = appService.getAllApp();

    }

    //每一分钟执行一次
    @Scheduled(cron = "0 0/1 * * * ?")
    public void runMonitorServer() {

        logger.info("开始拉监控数据, 应用数量={}", appList.size());

        long begin = System.currentTimeMillis();
        if (appList.isEmpty()) {
            BXMonitor.recordOne("应用数量为空");
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
        BXMonitor.recordOne("启动一次拉取监控", System.currentTimeMillis() - begin);

    }


    private static final String APP_URL = "http://%s/monitor";

    @Async
    private void getAndWritePoints(Long orgId, String appName, String host) {
        try {

            long begin = System.currentTimeMillis();

            //通过http抓数
            Map<String, Object> currentItems = httpGet(String.format(APP_URL, host));

            if (currentItems == null || currentItems.isEmpty()) {
                logger.warn("没有抓取到监控数据,orgId={},appName={},host={},size={}", orgId, appName, host, currentItems.size());
                return;
            }

            String database = OrgEnum.valueOf(Math.toIntExact(orgId)).getDatabase();

            influxDBDao.writePoints(database, appName, host, currentItems);

            logger.info("抓取监控数据,orgId={},appName={},host={},size={}", orgId, appName, host, currentItems.size());
            BXMonitor.recordOne("抓取监控数据成功", System.currentTimeMillis() - begin);

        } catch (Exception e) {
            logger.error("抓取监控数据失败,orgId={},appName={},host={}", orgId, appName, host, e);
            BXMonitor.recordOne("抓取监控失败");
        }
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(100);
        taskScheduler.initialize();
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
    }


    public static void addApp(AppModel app) {
        appList.add(app);
    }


}
