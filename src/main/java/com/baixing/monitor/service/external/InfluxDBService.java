package com.baixing.monitor.service.external;

import com.baixing.monitor.util.BxMonitor;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String GET_FIELD = "SHOW FIELD KEYS FROM %s";
    private static final String GET_TAG = "SHOW TAG KEYS FROM %s";


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

        BxMonitor.recordOne("influxdb_write_points", System.currentTimeMillis() - begin);

    }

    public List<String> getFieldKeys(String database, String measurement) {

        return getKeyList(database, measurement, GET_FIELD);

    }

    public List<String> getTagKeys(String database, String measurement) {

        return getKeyList(database, measurement, GET_TAG);

    }


    /**
     * 获取所有的打点的key
     *
     * @param database    数据库名
     * @param measurement 应用名
     * @return
     */
    private List<String> getKeyList(String database, String measurement, String sql) {
        List<String> keys = new ArrayList<>();
        try {
            Query query = new Query(String.format(sql, measurement), database);
            QueryResult result = influxDB.query(query);

            List<QueryResult.Result> resultList = result.getResults();
            List<List<Object>> keyList = resultList.get(0).getSeries().get(0).getValues();

            keyList.forEach(key -> keys.add((String) key.get(0)));

        } catch (Exception e) {
            logger.error("influxDB get key list error database={},measurement={},sql={}",
                    database, measurement, sql, e);
        }
        return keys;
    }
}
