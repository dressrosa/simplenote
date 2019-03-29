/**
 * 
 */
package com.xiaoyu.simplenote.common.utils;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 查询ip的地址
 * 
 * @author hongyu
 * @date 2018-08
 * @description
 */
public class HostHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HostHttpUtil.class);
    private static final int DEFAULT_SOCKET_TIMEOUT = 30_000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 10_000;

    private static String URL = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=";
    private static String TAIL = "&co=&resource_id=6006&t=1532423287708&ie=utf8&oe=gbk&cb=op_aladdin_callback&format=json"
            + "&tn=baidu&cb=jQuery110202725177272863297_1532423236534&_=1532423236538";

    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
            .setConnectionRequestTimeout(DEFAULT_CONNECT_TIMEOUT)
            .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
            .build();

    /**
     * @param ip
     * @return location
     */
    public static Map<String, String> sendRequest(String... ips) {
        Map<String, String> result = new HashMap<>(ips.length);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setConfig(requestConfig);

        String resp = null;
        CloseableHttpResponse response = null;
        String requestUrl = null;
        try {
            for (String ip : ips) {
                requestUrl = URL.concat(ip).concat(TAIL);
                httpGet.setURI(URI.create(requestUrl));
                response = httpclient.execute(httpGet);
                int state = response.getStatusLine().getStatusCode();
                if (state == HttpStatus.SC_OK) {
                    resp = EntityUtils.toString(response.getEntity());
                    int start = resp.indexOf("(") + 1;
                    int end = resp.lastIndexOf(")");
                    JSONObject json = JSON.parseObject(resp.substring(start, end));
                    resp = json.getJSONArray("data").getJSONObject(0).getString("location");
                    result.put(ip, resp);
                } else {
                    logger.info("requestUrl->{},result->{}", requestUrl, EntityUtils.toString(response.getEntity()));
                }
            }
        } catch (Exception e) {
            logger.error("request failed", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                // do nothing
            }
        }
        return result;
    }
}
