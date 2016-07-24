package com.baixing.monitor.dao;

import com.baixing.monitor.util.BXMonitor;
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

    //在influxdb中创建数据库的时候需要用这个前缀
    private static final String BAI_XING = "bx_";

    public void writeMap(String database, String measurement, String server, Map<String, Long> field) {

        long begin = System.currentTimeMillis();

        //后面将database跟org结合起来
        BatchPoints batchPoints = BatchPoints
                .database(BAI_XING + database)
                .retentionPolicy("default")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        for (Map.Entry<String, Long> entry : field.entrySet()) {
            Point point = Point.measurement(measurement)
                    .tag("server", server)
                    .tag("monitor_key", entry.getKey())
                    .addField("value", entry.getValue())
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .build();

            batchPoints.point(point);
        }


        influxDB.write(batchPoints);

        BXMonitor.recordOne("influxdb_write_points", System.currentTimeMillis() - begin);

    }

    private static final String GET_FIELD = "SHOW FIELD KEYS FROM %s";

    public Set<String> getAllKeyField(String database, String measurement) {
        long begin = System.currentTimeMillis();
        Set<String> keySet = new HashSet<>();
        try {
            Query query = new Query(String.format(GET_FIELD, measurement), BAI_XING + database);
            QueryResult result = influxDB.query(query);
            System.out.println(result);
            List<QueryResult.Result> resultList = result.getResults();
            List<List<Object>> keySets = resultList.get(0).getSeries().get(0).getValues();


            for (List<Object> keys : keySets) {
                keySet.add((String) keys.get(0));
            }
            BXMonitor.recordOne("influxdb_get_field", System.currentTimeMillis() - begin);

        } catch (Exception e) {
            e.printStackTrace();
            BXMonitor.recordOne("influxdb_get_field_error", System.currentTimeMillis() - begin);
        }
        return keySet;
    }
}
