package com.baixing.monitor.service;

import com.baixing.monitor.mapper.DashMapper;
import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.util.BXMonitor;
import com.google.common.base.Splitter;
import com.sun.tools.javac.util.Pair;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by kofee on 16/7/20.
 */
@Component
public class TaskService implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);


    @Autowired
    private DashMapper dashMapper;

    @Autowired
    private AppService appService;


    //组织和应用名称 是key   和 应用地址
    private static Map<Pair<Integer, String>, String> serverMap = new HashMap<>();


    //应用启动的时候从数据库读取
    @PostConstruct
    public void getAppServer() {
        List<AppModel> temps = appService.getAllApp();
        for (AppModel app : temps) {
            Pair<Integer, String> key = new Pair<>(app.getOrgId(), app.getName());
            serverMap.put(key, app.getServer());
        }
    }


    //每次启动线程数的大小
    private ExecutorService executor = Executors.newFixedThreadPool(5);

    //每60秒执行一次
    @Scheduled(fixedRate = 60000)
    public void runMonitorServer() {

        logger.info("开始拉监控数据, 应用数量={}", serverMap.size());
        long begin = System.currentTimeMillis();

        for (Map.Entry<Pair<Integer, String>, String> app : serverMap.entrySet()) {
            Pair<Integer, String> key = app.getKey();
            Integer orgId = key.fst;
            String name = key.snd;
            String server = app.getValue();

            //当有多台机器时要都去抓取
            Iterable<String> temp = Splitter.on(",").split(server);
            for (String host : temp) {
                executor.submit(new GetAndWritePoints(orgId, name, host));
            }
        }
        logger.info("结束一次拉取,花费总时间={}", System.currentTimeMillis() - begin);
        BXMonitor.recordOne("启动一次拉取监控", System.currentTimeMillis() - begin);

    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(100);
        taskScheduler.initialize();
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
    }


    public static void addServerMap(AppModel app) {
        Pair<Integer, String> key = new Pair<>(app.getOrgId(), app.getName());
        serverMap.put(key, app.getServer());
    }

}
