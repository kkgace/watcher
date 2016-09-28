package com.superbool.watcher.service.external;

import com.superbool.watcher.model.MeasurementModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kofee on 16/7/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InfluxDBTest {

    @Autowired
    private InfluxDBService influxDBService;

    //@Test
    public void writeTest() {
        Map<String, Object> monitor = new HashMap<>();
        monitor.put("key1", 1L);
        monitor.put("key2", 2L);
        monitor.put("key3", 3L);
        influxDBService.writePoints("grafana", "test", "localhost:8080", monitor);
    }

    @Test
    public void testShowFields() {
        List<String> keys = influxDBService.showFields("telegraf", "cpu");
        System.out.println(keys);
        Assert.assertEquals("usage_guest", keys.get(0));
    }

    @Test
    public void testShowTags() {
        List<String> keys = influxDBService.showTags("telegraf", "cpu");
        System.out.println(keys);
        Assert.assertEquals("host", keys.get(1));
    }

    @Test
    public void testShowDatabases() {
        List<String> databases = influxDBService.showDatabases();
        System.out.println(databases);
        Assert.assertEquals("_internal", databases.get(0));
    }

    @Test
    public void testShowMeasurements() {
        List<String> measurements = influxDBService.showMeasurements("telegraf");
        System.out.println(measurements);
    }

    @Test
    public void testGetMeasurement() {
        List<MeasurementModel> measurementList = new ArrayList<>();
        List<String> databases = influxDBService.showDatabases();
        for (String database : databases) {
            if ("_internal".equals(database)) {
                continue;
            }
            List<String> measuresName = influxDBService.showMeasurements(database);
            for (String measureName : measuresName) {
                MeasurementModel measurement = influxDBService.getMeasurement(database, measureName);
                measurementList.add(measurement);
            }
        }

        measurementList.forEach(measurementModel -> System.out.println(measurementModel.toJsonStr()));

    }

}
