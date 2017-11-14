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

    public static  List<NewsItem> getNewsItems(int newsType, int curPage) throws CommonException{
        List<NewsItem> newsItems = new ArrayList<>();
        String url = UrlUtil.getUrl(newsType, curPage);
        String htmlStr = DataUtil.doGet(url);
        NewsItem item = null;
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
