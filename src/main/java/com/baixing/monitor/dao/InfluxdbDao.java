package com.baixing.monitor.dao;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by kofee on 16/7/20.
 */
@Repository
public class InfluxdbDao {

    @Autowired
    private InfluxDB influxDB;

    public void writeMap(String database, String measurement, String server, Map<String, Object> field) {


        //后面将database跟org结合起来
        BatchPoints batchPoints = BatchPoints
                .database(database)
                .retentionPolicy("default")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point point1 = Point.measurement(measurement)
                .tag("server", server)
                .fields(field)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();

        batchPoints.point(point1);

        influxDB.write(batchPoints);
    }

    private static final String GET_FIELD = "SHOW FIELD KEYS FROM %s";

    public Set<String> getAllKeyField(String database, String measurement) {
        Set<String> keySet = new HashSet<>();
        try {
            Query query = new Query(String.format(GET_FIELD, measurement), database);
            QueryResult result = influxDB.query(query);
            List<QueryResult.Result> resultList = result.getResults();
            List<List<Object>> keySets = resultList.get(0).getSeries().get(0).getValues();


            for (List<Object> keys : keySets) {
                keySet.add((String) keys.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keySet;
    }
}
