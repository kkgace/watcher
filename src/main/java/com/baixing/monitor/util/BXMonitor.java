package com.baixing.monitor.util;

/**
 * Created by kofee on 16/7/20.
 */

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class BXMonitor {
    private static Map<String, MonitorItem> items = new ConcurrentHashMap();
    private static Map<String, AtomicLong> values = new ConcurrentHashMap();
    private static Map<String, MonitorItem> jvmItems = new ConcurrentHashMap();
    private static Map<String, Long> currentItems = new HashMap();
    private static Timer timer = new Timer("BXMonitor", true);
    private static long lastUpdate;

    public BXMonitor() {
    }

    public static void recordOne(String name, long time) {
        recordMany(name, 1L, time);
    }

    public static void recordOne(String name) {
        recordMany(name, 1L, 0L);
    }

    public static void decrRecord(String name) {
        recordMany(name, -1L, 0L);
    }

    public static void recordMany(String name, long count, long time) {
        BXMonitor.MonitorItem item = (BXMonitor.MonitorItem) items.get(name);
        if (item == null) {
            item = new BXMonitor.MonitorItem();
            items.put(name, item);
        }

        item.add(count, time);
    }

    public static void recordSize(String name, long size) {
        AtomicLong v = (AtomicLong) values.get(name);
        if (v == null) {
            v = new AtomicLong();
            values.put(name, v);
        }

        v.set(size);
    }

    public static void recordValue(String name, long count) {
        AtomicLong v = (AtomicLong) values.get(name);
        if (v == null) {
            v = new AtomicLong();
            values.put(name, v);
        }

        v.addAndGet(count);
    }

    public static Map<String, Long> getValues() {
        return currentItems;
    }

    static {
        timer.schedule(new BXMonitor.MonitorTask(), 0L, 2000L);
        lastUpdate = 0L;
    }

    private static class MonitorTask extends TimerTask {
        private MonitorTask() {
        }

        public void run() {
            try {
                long e = System.currentTimeMillis();
                if (e - BXMonitor.lastUpdate < 50000L) {
                    return;
                }

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(e);
                if (cal.get(13) > 10) {
                    return;
                }

                BXMonitor.lastUpdate = e;
                HashMap ret = new HashMap();
                ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
                ret.put("JVM_Thread_Count", Long.valueOf((long) threadBean.getThreadCount()));
                List beans = ManagementFactory.getGarbageCollectorMXBeans();
                Iterator i$ = beans.iterator();

                String name;
                while (i$.hasNext()) {
                    GarbageCollectorMXBean entry = (GarbageCollectorMXBean) i$.next();
                    name = "JVM_" + entry.getName();
                    long item = entry.getCollectionCount();
                    long time = entry.getCollectionTime();
                    BXMonitor.MonitorItem item1 = (BXMonitor.MonitorItem) BXMonitor.jvmItems.get(name);
                    if (item1 == null) {
                        item1 = new BXMonitor.MonitorItem();
                        item1.add(item, time);
                        BXMonitor.jvmItems.put(name, item1);
                    }

                    ret.put(this.makeName(name + "_Count"), Long.valueOf(item - item1.count));
                    if (item - item1.count > 0L) {
                        ret.put(this.makeName(name + "_Time"), Long.valueOf((time - item1.time) / (item - item1.count)));
                    }

                    item1 = new BXMonitor.MonitorItem();
                    item1.add(item, time);
                    BXMonitor.jvmItems.put(name, item1);
                }

                i$ = BXMonitor.items.entrySet().iterator();

                Entry entry1;
                while (i$.hasNext()) {
                    entry1 = (Entry) i$.next();
                    name = (String) entry1.getKey();
                    BXMonitor.MonitorItem item2 = ((BXMonitor.MonitorItem) entry1.getValue()).dumpAndClearItem();
                    long count = item2.count;
                    long time1 = item2.time;
                    ret.put(this.makeName(name + "_Count"), Long.valueOf(count));
                    if (count > 0L) {
                        ret.put(this.makeName(name + "_Time"), Long.valueOf(time1 / count));
                    } else {
                        ret.put(this.makeName(name + "_Time"), Long.valueOf(0L));
                    }
                }

                i$ = BXMonitor.values.entrySet().iterator();

                while (i$.hasNext()) {
                    entry1 = (Entry) i$.next();
                    ret.put(this.makeName((String) entry1.getKey() + "_Value"), Long.valueOf(((AtomicLong) entry1.getValue()).get()));
                }

                BXMonitor.currentItems = Collections.unmodifiableMap(ret);
            } catch (Exception var15) {
                ;
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

        public synchronized BXMonitor.MonitorItem dumpAndClearItem() {
            BXMonitor.MonitorItem item = new BXMonitor.MonitorItem();
            item.count = this.count;
            item.time = this.time;
            this.count = 0L;
            this.time = 0L;
            return item;
        }
    }
}

