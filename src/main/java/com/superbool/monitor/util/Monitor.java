package com.superbool.monitor.util;

/**
 * Created by kofee on 16/7/20.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Monitor {
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    private static Map<String, Monitor.MonitorItem> items = new ConcurrentHashMap();
    private static Map<String, AtomicLong> values = new ConcurrentHashMap();
    private static Map<String, Long> currentItems = new HashMap();
    private static Timer timer = new Timer("Monitor", true);

    public Monitor() {
    }

    /**
     * @param name 设置的key
     * @param time 调用的时间  最终记录的是调用次数的平均值
     */
    public static void recordOne(String name, long time) {
        recordMany(name, 1L, time);
    }

    /**
     * @param name 调用该方法一次,name对应的值+1 默认每分钟清零
     */
    public static void recordOne(String name) {
        recordMany(name, 1L, 0L);
    }

    public static void decrRecord(String name) {
        recordMany(name, -1L, 0L);
    }

    public static void recordMany(String name, long count, long time) {
        Monitor.MonitorItem item = items.get(name);
        if (item == null) {
            item = new Monitor.MonitorItem();
            items.put(name, item);
        }
        item.add(count, time);
    }

    //覆盖统计 每分钟多次调用只会记录最后一次
    public static void recordSize(String name, long size) {
        AtomicLong value = values.get(name);
        if (value == null) {
            value = new AtomicLong();
            values.put(name, value);
        }

        value.set(size);
    }

    //累加统计 从程序运行是开始累加
    public static void recordValue(String name, long count) {
        AtomicLong value = values.get(name);
        if (value == null) {
            value = new AtomicLong();
            values.put(name, value);
        }

        value.addAndGet(count);
    }

    public static Map<String, Long> getItemMap() {
        return currentItems;
    }


    //初始化 一个定时器
    static {
        Calendar start = Calendar.getInstance();
        //启动的时候计算下一个分钟数,保证每次在整点的时候清数
        start.set(Calendar.MINUTE, start.get(Calendar.MINUTE) + 1);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        //后面一个参数设置定时任务间隔,默认30s
        timer.scheduleAtFixedRate(new Monitor.MonitorTask(), start.getTime(), 30000L);
    }

    private static class MonitorTask extends TimerTask {
        private MonitorTask() {
        }

        public void run() {

            try {

                Map<String, Long> itemMap = new HashMap<>();

                Monitor.items.forEach((name, items) -> {
                    Monitor.MonitorItem item = items.dumpAndClearItem();
                    long count = item.count;
                    long time = item.time;
                    itemMap.put(this.makeName(name + "_count"), Long.valueOf(count));
                    //将时间平均
                    if (count > 0L) {
                        itemMap.put(this.makeName(name + "_time"), Long.valueOf(time / count));
                    } else {
                        itemMap.put(this.makeName(name + "_time"), Long.valueOf(0L));
                    }
                });

                Monitor.values.forEach((key, value) ->
                        itemMap.put(this.makeName(key), value.get())
                );

                Monitor.currentItems = Collections.unmodifiableMap(itemMap);

                //写入influxdb
                if (!currentItems.isEmpty()) {
                    logger.debug("write into influxdb {}", currentItems);
                    //InfluxDBClient.writePoints(currentItems);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private String makeName(String name) {
            return name.replaceAll(" ", "_");
        }
    }

    private static class MonitorItem {
        private long count;
        private long time;

        private MonitorItem() {
        }

        public synchronized void add(long count, long time) {
            this.count += count;
            this.time += time;
        }

        public synchronized Monitor.MonitorItem dumpAndClearItem() {
            Monitor.MonitorItem item = new Monitor.MonitorItem();
            item.count = this.count;
            item.time = this.time;
            this.count = 0L;
            this.time = 0L;
            return item;
        }
    }

}

