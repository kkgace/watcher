package com.superbool.watcher.service.external;

import com.superbool.watcher.WatcherApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by kofee on 16/8/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(WatcherApplication.class)
public class KapacitorTest {

    @Autowired
    private KapacitorService kapacitorService;


    @Test
    public void getTaskTest() {
        kapacitorService.getTask(null);
    }

    @Test
    public void addTaskTest() {
        String database = "telegraf";
        String measurement = "cpu";
        String tag = "host";
        String field = "usage_idle";
        String condition = ">96";
        String email = "kefei@baixing.com";

        String result = kapacitorService.addTask(database, measurement, tag, field, condition, email);
        System.out.println(result);

    }

    @Test
    public void deleteTaskTest() {
        int result = kapacitorService.deleteTask("usage_idle");
        System.out.println(result);
    }
}
