package com.fayne.android.schoolnews.util;

/**
 * Created by fan on 2017/11/14.
 */

public class UrlUtil {

    public static final String NEWS_LIST_URL_AKXW = "http://www.ahstu.edu.cn/index/akxw";
    public static final String NEWS_LIST_URL_TZGG = "http://www.ahstu.edu.cn/index/tzgg";
    public static final String NEWS_LIST_URL_XYDT = "http://www.ahstu.edu.cn/index/xydt";
    public static final String NEWS_LIST_URL_XSXX = "http://www.ahstu.edu.cn/index/xsxx";

    public static final String BGXT = "http://211.70.48.79/part/bgxt.html";
    public static final String JWXT = "http://jwxt.ahstu.edu.cn/";
    public static final String YJXT = "http://mail.ahstu.edu.cn/";
    public static final String XGXT = "http://www.ahstu.edu.cn/xgxt.htm";

    public static final String CWXT = "http://172.16.250.201:8080/";
    public static final String STHXY = "http://portal.ahstu.edu.cn/";
    public static final String FWXL = "http://www.ahstu.edu.cn/fwcn.htm";
    public static final String TSG = "http://lib.ahstu.edu.cn/Article/index.shtml";

    public static final String XLCX = "http://www.ahstu.edu.cn/jwc/list.jsp?urltype=tree.TreeTempUrl&wbtreeid=1969";
    public static final String XCBC = "http://www.ahstu.edu.cn/index/xcbc.htm";


    public static String getUrl(int newsType, int curPage) {
        String url = "";
        switch (newsType) {
            case Constant.NEWS_TYPE_AKXW:
                url = NEWS_LIST_URL_AKXW;
                break;
            case Constant.NEWS_TYPE_TZGG:
                url = NEWS_LIST_URL_TZGG;
                break;
            case Constant.NEWS_TYPE_XYDT:
                url = NEWS_LIST_URL_XYDT;
                break;
            case Constant.NEWS_TYPE_XSXX:
                url = NEWS_LIST_URL_XSXX;
                break;
            default:
                break;
        }
        if (curPage != 1) {
            url += "/" + curPage+ ".htm";
        } else {
            url += ".htm";
        }
        return url;
    }
}
