package com.baixing.monitor.service.dao;

import com.baixing.monitor.WatcherApplication;
import com.baixing.monitor.util.InfluxDBClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kofee on 16/7/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(WatcherApplication.class)
public class InfluxdbTest {

    @Autowired
    private InfluxDBClient influxDBClient;

    @Test
    public void writeTest() {
        Map<String, Object> monitor = new HashMap<>();
        monitor.put("key1", 1L);
        monitor.put("key2", 2L);
        monitor.put("key3", 3L);

        influxDBClient.writePoints("grafana", "test", "localhost:8080", monitor);


    }

    @Test
    public void getFieldTest() {
        List<String> keys = influxDBClient.getFildKeys("grafana", "test");
        System.out.println(keys);

        keys = influxDBClient.getTagKeys("grafana", "test");
        System.out.println(keys);
    }
}
