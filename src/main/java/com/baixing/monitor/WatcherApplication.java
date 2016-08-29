package com.baixing.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by kofee on 16/7/11.
 */
@SpringBootApplication
//@EnableScheduling
@EnableAsync
public class WatcherApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(WatcherApplication.class, args);
    }
}
