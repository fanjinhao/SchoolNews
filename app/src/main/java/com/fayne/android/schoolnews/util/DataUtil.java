package com.fayne.android.schoolnews.util;

import com.fayne.android.schoolnews.bean.CommonException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fan on 2017/11/14.
 */

public class DataUtil {
    /**
     * 返回该链接地址的html字符串数据
     */

    public static String doGet(String urlStr) throws CommonException{
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            int resultCode = conn.getResponseCode();

            if (resultCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + '\n');
                }
                reader.close();
            } else {
                throw new CommonException("网络连接失败.");
            }
        } catch (Exception e) {
            throw new CommonException("网络连接失败.");
        }
        return sb.toString();
    }
}
