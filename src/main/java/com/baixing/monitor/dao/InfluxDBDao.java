package com.baixing.monitor.dao;

import com.baixing.monitor.util.BXMonitor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by kofee on 16/7/20.
 */
@Repository
public class InfluxDBDao {
    private static final Logger logger = LoggerFactory.getLogger(InfluxDBDao.class);

    @Autowired
    private InfluxDB influxDB;


    /**
     * @param database    数据库
     * @param measurement 应用
     * @param tag         host
     * @param fields      监控值
     */
    public void writePoints(String database, String measurement, String tag, Map<String, Object> fields) {

        long begin = System.currentTimeMillis();

        BatchPoints batchPoints = BatchPoints
                .database(database)
                .retentionPolicy("default")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point point = Point.measurement(measurement)
                .tag("host", tag)
                .fields(fields)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();

        batchPoints.point(point);


        influxDB.write(batchPoints);

        BXMonitor.recordOne("influxdb_write_points", System.currentTimeMillis() - begin);

    }

    private static final String GET_FIELD = "SHOW FIELD KEYS FROM %s";
    private static final String GET_TAG = "SHOW TAG KEYS FROM %s";

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

    public List<String> getFildKeys(String database, String measurement) {

        return getKeyList(database, measurement, GET_FIELD);

    }

    public List<String> getTagKeys(String database, String measurement) {

        return getKeyList(database, measurement, GET_TAG);

    }
}
