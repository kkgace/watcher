package com.baixing.monitor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kofee on 16/7/24.
 */
public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static Map<String, Object> httpGet(String address) {
        long begin = System.currentTimeMillis();
        Map<String, Object> currentItems = null;
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            currentItems = new HashMap<String, Object>();
            //TODO lambda表达式
            while ((line = rd.readLine()) != null) {
                String[] strs = line.split("=");
                if (strs != null && strs.length == 2) {
                    currentItems.put(strs[0], Long.parseLong(strs[1]));
                }
            }
            rd.close();
            BXMonitor.recordOne("http_get", System.currentTimeMillis() - begin);
        } catch (Exception e) {
            logger.error("http get error!", e);
            BXMonitor.recordOne("http_get_error", System.currentTimeMillis() - begin);
        } finally {
            logger.info("http_get url={},response={}", address, currentItems);
        }

        return currentItems;
    }
}
