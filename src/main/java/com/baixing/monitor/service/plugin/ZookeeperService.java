package com.baixing.monitor.service.plugin;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by kofee on 16/8/24.
 */
@Service
public class ZookeeperService {

    @Value(value = "${zookeeper.cluster.url}")
    private String zookeeperUrl;

    @Value(value = "${zookeeper.database}")
    private String database;

    @Value(value = "${zookeeper.measurement}")
    private String measurement;


    @Autowired
    ZooKeeper zooKeeper;

    @Bean
    private ZooKeeper getZookeeper() {
        ZooKeeper zookeeper = null;
        try {
            zookeeper = new ZooKeeper(zookeeperUrl, 30000, watchedEvent -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(zookeeper);
        return zookeeper;
    }


    public void monitorZookeeper() {
        System.out.println(zooKeeper);
        try {
            List<String> root = zooKeeper.getChildren("/", watchedEvent -> {
            });

            System.out.println(root);

            byte[] data = zooKeeper.getData("/consumers/yaoguang_push/offsets/push_contact/0", false, null);
            System.out.println(new String(data));


        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
