package com.fayne.android.schoolnews.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by fan on 2017/11/14.
 */

public class NetUtil {

    /***
     * 判断是否有网络连接
     */

    public static boolean isOnline(Context context) {
        boolean wifiConnected = isWifiConnected(context);
        boolean mobileConnected = isMobileConnected(context);
        if (wifiConnected || mobileConnected)
            return true;
        return false;
    }

    private static boolean isMobileConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    private static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }
}
