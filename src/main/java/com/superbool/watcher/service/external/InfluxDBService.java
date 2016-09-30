package com.superbool.watcher.service.external;

import com.superbool.watcher.model.MeasurementModel;
import com.superbool.watcher.util.Monitor;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by kofee on 16/7/20.
 */
@Service
public class InfluxDBService {
    private static final Logger logger = LoggerFactory.getLogger(InfluxDBService.class);

    private InfluxDB influxDB;

    @Value(value = "${influxdb.url}")
    private String url;

    @Value(value = "${influxdb.user}")
    private String user;

    @Value(value = "${influxdb.password}")
    private String password;


    @PostConstruct
    public void init() {
        influxDB = InfluxDBFactory.connect(url, user, password);
    }

    private static final String SHOW_FIELD_SQL = "SHOW FIELD KEYS FROM %s";
    private static final String SHOW_TAG_SQL = "SHOW TAG KEYS FROM %s";
    private static final String SHOW_MEASUREMENT_SQL = "SHOW MEASUREMENTS";


    /**
     * @param database    数据库
     * @param measurement 应用
     * @param host        host
     * @param fields      监控值
     */
    public void writePoints(String database, String measurement, String host, Map<String, Object> fields) {
        Map<String, String> tagMap = new HashMap<>();
        tagMap.put("host", host);
        writePoints(database, measurement, tagMap, fields);
    }


    public void writePoints(String database, String measurement, Map<String, String> tagMap, Map<String, Object> fieldMap) {

        long begin = System.currentTimeMillis();

        BatchPoints batchPoints = BatchPoints
                .database(database)
                .retentionPolicy("default")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point point = Point.measurement(measurement)
                .tag(tagMap)
                .fields(fieldMap)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();

        batchPoints.point(point);

        influxDB.write(batchPoints);

        Monitor.recordOne("influxdb_write_points", System.currentTimeMillis() - begin);

    }

    public MeasurementModel getMeasurement(String database, String measureName) {
        MeasurementModel measurement = new MeasurementModel(database, measureName);
        List<String> tags = showTags(database, measureName);
        List<String> fields = showFields(database, measureName);
        measurement.setTags(tags);
        measurement.setFields(fields);
        return measurement;
    }


    public List<String> showDatabases() {
        return influxDB.describeDatabases();
    }

    public List<String> showMeasurements(String database) {
        return getList(SHOW_MEASUREMENT_SQL, database);
    }

    public List<String> showTags(String database, String measurement) {
        String sql = String.format(SHOW_TAG_SQL, measurement);
        return getList(sql, database);

    }

    public List<String> showFields(String database, String measurement) {
        String sql = String.format(SHOW_FIELD_SQL, measurement);
        return getList(sql, database);
    }

    private List<String> getList(String sql, String database) {
        List<String> list = new ArrayList<>();
        try {
            Query query = new Query(sql, database);
            QueryResult result = influxDB.query(query);
            System.out.println(result);

            List<QueryResult.Result> resultList = result.getResults();

            if (resultList.get(0).getSeries() != null) {
                List<List<Object>> keyList = resultList.get(0).getSeries().get(0).getValues();
                keyList.forEach(key -> list.add((String) key.get(0)));
            }

        } catch (Exception e) {
            logger.error("influxDB get list error! database={}, sql={}", database, sql, e);
        }
        return list;
    }
}
