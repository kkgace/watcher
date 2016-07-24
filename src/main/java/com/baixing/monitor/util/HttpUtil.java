package com.baixing.monitor.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kofee on 16/7/24.
 */
public class HttpUtil {

    public static Map<String, Object> httpGet(String urlToRead) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        Map<String, Object> currentItems = new HashMap<String, Object>();
        while ((line = rd.readLine()) != null) {
            String[] strs = line.split("=");
            if (strs != null && strs.length == 2) {
                currentItems.put(strs[0], Long.parseLong(strs[1]));
            }
        }
        rd.close();
        return currentItems;
    }
}
