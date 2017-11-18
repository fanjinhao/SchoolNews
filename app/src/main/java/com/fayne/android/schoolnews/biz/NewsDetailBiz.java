package com.fayne.android.schoolnews.biz;

import com.fayne.android.schoolnews.bean.NewsDetail;
import com.fayne.android.schoolnews.util.UrlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by fan on 2017/11/14.
 */

public class NewsDetailBiz {
    public  NewsDetail getNewsDetail(String html) {
        NewsDetail news = new NewsDetail();
        Document doc = Jsoup.parse(html);
        Element detail = doc.select("div.list-rconC").first();
        Element title = detail.select("div.list-textT").first();
        Element info = detail.select("div.list-infor").first();
        news.setTitle(title.text());
        news.setInfo(info.text());
        StringBuffer buffer = new StringBuffer();
        Elements elements = detail.select(".list-textC p");
        for (Element element : elements) {
            Elements imgs = element.select("img");
            for (Element img : imgs) {
                String imgUrl = img.attr("src");
                if (!imgUrl.contains("http")) {
                    imgUrl = "http://www.ahstu.edu.cn" + imgUrl;
                }
                img.attr("src", imgUrl)
                        .attr("width", "100%")
                        .attr("height", "auto")
                        .attr("style", "");
            }
            buffer.append("<p>");
            buffer.append(element.html());
            buffer.append("</p>");
        }
        news.setText(buffer.toString());
        return news;
    }
}
