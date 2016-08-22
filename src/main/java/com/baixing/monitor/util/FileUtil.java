package com.baixing.monitor.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;

/**
 * Created by kofee on 16/7/21.
 */
public class FileUtil {


    //读取json文件
    public static String getFileToStr(String fileName) {
        String data = null;
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(url.getFile()));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + System.lineSeparator());
            }

            data = stringBuilder.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;

    }



}
