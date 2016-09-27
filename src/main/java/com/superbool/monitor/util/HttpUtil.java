package com.superbool.monitor.util;


import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static final String TYPE_URL_ENCODED = "application/x-www-form-urlencoded; charset=utf-8";
    private static final String TYPE_JSON = "application/json; charset=UTF-8";


    /**
     * 表单方式提交
     *
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        params.forEach((key, value) -> nameValuePairList.add(new BasicNameValuePair(key, value)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairList, Charset.forName("UTF-8"));

        return post(url, entity, TYPE_URL_ENCODED, null);
    }

    /**
     * json 字符串提交
     *
     * @param url
     * @param json
     * @return
     */
    public static String post(String url, String json) {
        HttpEntity entity = new StringEntity(json, Charset.forName("utf-8"));
        return post(url, entity, TYPE_JSON, null);
    }

    public static String post(String url, String json, String auth) {
        HttpEntity entity = new StringEntity(json, Charset.forName("utf-8"));
        return post(url, entity, TYPE_JSON, auth);
    }


    /**
     * get 方法
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        return get(url, null);
    }

    public static String get(String url, String auth) {
        HttpGet httpGet = new HttpGet(url);
        if (!Strings.isNullOrEmpty(auth)) {
            httpGet.setHeader("Authorization", auth);
        }
        String result = null;
        try {
            result = request(httpGet);
            logger.info("[http-get] url=[{}],response=[{}]", url, result);
        } catch (Exception e) {
            logger.error("http get error! url=[{}]", url, e);
        }

        return result;
    }

    public static int delete(String url) {
        return delete(url, null);
    }

    public static int delete(String url, String auth) {
        int result = -1;
        HttpDelete delete = new HttpDelete(url);
        if (!Strings.isNullOrEmpty(auth)) {
            delete.setHeader("Authorization", auth);
        }
        CloseableHttpClient httpclient = HttpClients.custom().build();
        try {
            CloseableHttpResponse response = httpclient.execute(delete);
            result = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
            result = -1;
        } finally {
            delete.releaseConnection();
        }

        return result;
    }


    private static String post(String url, HttpEntity entity, String contentType, String auth) {

        String result = null;

        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(3000)
                    .setConnectTimeout(3000)
                    .build();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", contentType);
            if (!Strings.isNullOrEmpty(auth)) {
                httpPost.setHeader("Authorization", auth);
            }
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(entity);

            result = request(httpPost);
            String request = CharStreams.toString(new InputStreamReader(entity.getContent(), "UTF-8"));
            logger.info("[http-post] url=[{}],request=[{}],response=[{}]", url, request, result);

        } catch (Exception e) {
            logger.error("http post error! url=[{}],request=[{}]", url, result, e);
        }

        return result;
    }

    private static String request(HttpUriRequest request) throws Exception {
        String result = null;
        //重试三次 每次间隔3000ms
        CloseableHttpClient httpclient = HttpClients.custom()
                .setRetryHandler((e, i, httpContext) -> {
                            if (i <= 3) {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e1) {
                                    logger.error("http sleep error!", e1);
                                }
                                return true;
                            }
                            return false;
                        }
                )
                .build();

        CloseableHttpResponse response = httpclient.execute(request);
        try {
            HttpEntity respEntity = response.getEntity();

            result = EntityUtils.toString(respEntity, Charset.forName("UTF-8"));

            EntityUtils.consume(respEntity);
        } finally {
            response.close();
        }

        return result;
    }


}
