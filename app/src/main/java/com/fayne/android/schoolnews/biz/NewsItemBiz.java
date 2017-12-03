package com.fayne.android.schoolnews.biz;

import com.fayne.android.schoolnews.bean.CommonException;
import com.fayne.android.schoolnews.bean.NewsItem;
import com.fayne.android.schoolnews.util.DataUtil;
import com.fayne.android.schoolnews.util.UrlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan on 2017/11/14.
 */

public class NewsItemBiz {

    public static int getNewsTotal(int newsType) {
        String s = "000";
        try {
            String url = UrlUtil.getUrl(newsType, 1);
            String htmlStr = DataUtil.doGet(url);
            Document doc = Jsoup.parse(htmlStr);
            Element element = doc.select(".Next").first();
            String text = element.attr("href").toString();
            s = text;
        } catch (CommonException e) {
            e.printStackTrace();
        }
        return 1 + Integer.parseInt(s.substring(s.indexOf("/")+1, s.indexOf(".")));
    }

    public  List<NewsItem> getNewsItems(int newsType, int curPage) throws CommonException{
        List<NewsItem> newsItems = new ArrayList<>();
        String url = UrlUtil.getUrl(newsType, curPage);
        String htmlStr = DataUtil.doGet(url);
        NewsItem item;
        Document doc = Jsoup.parse(htmlStr);
        Elements units = doc.getElementsByClass("rigthConBox-conList");
        for (Element unit : units) {
            item = new NewsItem();

            String href = unit.attr("href");
            if (!href.contains("http")) {
                href = "http://www.ahstu.edu.cn" + href.substring(2);
                item.setNewsType(newsType);
                item.setLink(href);
                Element conListWord = unit.getElementsByClass("conListWord").get(0);
                item.setTitle(conListWord.text());
                Element conListTime = unit.getElementsByClass("conListTime").get(0);
                item.setDate(conListTime.text());
                newsItems.add(item);
            }
        }
        return newsItems;
    }
}
