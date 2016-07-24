package com.baixing.monitor.util;

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

    public static Map<String, Long> httpGet(String urlToRead) {
        long begin = System.currentTimeMillis();
        Map<String, Long> currentItems = null;
        try {
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            currentItems = new HashMap<String, Long>();
            while ((line = rd.readLine()) != null) {
                String[] strs = line.split("=");
                if (strs != null && strs.length == 2) {
                    currentItems.put(strs[0], Long.parseLong(strs[1]));
                }
            }
            rd.close();
            BXMonitor.recordOne("http_get", System.currentTimeMillis() - begin);
        } catch (Exception e) {
            e.printStackTrace();
            BXMonitor.recordOne("http_get_error", System.currentTimeMillis() - begin);
        }

        return currentItems;
    }
}
