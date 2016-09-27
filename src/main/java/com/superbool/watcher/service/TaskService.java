package com.superbool.watcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;


/**
 * Created by kofee on 16/7/20.
 */
@Component
public class TaskService implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);


    //每一分钟执行一次
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void runMonitorServer() {


    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(100);
        taskScheduler.initialize();
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
    }

}
