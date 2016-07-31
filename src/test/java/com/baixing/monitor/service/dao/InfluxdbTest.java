package com.baixing.monitor.service.dao;

import com.baixing.monitor.Application;
import com.baixing.monitor.dao.InfluxDBDao;
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
@SpringApplicationConfiguration(Application.class)
public class InfluxdbTest {

    @Autowired
    private InfluxDBDao influxDBDao;

    @Test
    public void writeTest() {
        Map<String, Object> monitor = new HashMap<>();
        monitor.put("key1", 1L);
        monitor.put("key2", 2L);
        monitor.put("key3", 3L);

        influxDBDao.writePoints("grafana", "test", "localhost:8080", monitor);


    }

    @Test
    public void getFieldTest() {
        List<String> keys = influxDBDao.getFildKeys("grafana", "test");
        System.out.println(keys);

        keys = influxDBDao.getTagKeys("grafana", "test");
        System.out.println(keys);
    }
}
