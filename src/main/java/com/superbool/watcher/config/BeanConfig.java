package com.superbool.watcher.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by superbool on 16/10/1.
 */
@Configuration
public class BeanConfig {


    @Value(value = "${influxdb.url}")
    private String url;

    @Value(value = "${influxdb.user}")
    private String user;

    @Value(value = "${influxdb.password}")
    private String password;

    @Bean
    public InfluxDB initInfluxDB() {
        return InfluxDBFactory.connect(url, user, password);
    }
}
