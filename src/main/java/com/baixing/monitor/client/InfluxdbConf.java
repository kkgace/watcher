package com.baixing.monitor.client;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kofee on 16/7/20.
 */
@Configuration
public class InfluxdbConf {

    @Value(value = "${influxdb.url}")
    private String url;

    @Value(value = "${influxdb.user}")
    private String user;

    @Value(value = "${influxdb.password}")
    private String password;

    @Bean
    public InfluxDB initInfluxDB() {
        InfluxDB influxDB = InfluxDBFactory.connect(url, user, password);
        return influxDB;
    }

}
