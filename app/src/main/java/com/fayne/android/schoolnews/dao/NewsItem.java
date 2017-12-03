package com.fayne.android.schoolnews.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by fan on 2017/12/3.
 */

@Entity
public class NewsItem {

    @Id(autoincrement = true)
    private Long Id;

    //标题
    @Property(nameInDb = "TITLE")
    private String Title;
    //链接
    @Property(nameInDb = "LINK")
    private String Link;
    //图片链接
    @Property(nameInDb = "IMGLINK")
    private String ImgLink;
    //内容
    @Property(nameInDb = "CONTENT")
    private String Content;
    //发布时间
    @Property(nameInDb = "date")
    private String Date;
    //类型
    @Property(nameInDb = "NEWSTYPE")
    private Integer NewsType;
    @Generated(hash = 133578725)
    public NewsItem(Long Id, String Title, String Link, String ImgLink,
            String Content, String Date, Integer NewsType) {
        this.Id = Id;
        this.Title = Title;
        this.Link = Link;
        this.ImgLink = ImgLink;
        this.Content = Content;
        this.Date = Date;
        this.NewsType = NewsType;
    }
    @Generated(hash = 1697690472)
    public NewsItem() {
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getTitle() {
        return this.Title;
    }
    public void setTitle(String Title) {
        this.Title = Title;
    }
    public String getLink() {
        return this.Link;
    }
    public void setLink(String Link) {
        this.Link = Link;
    }
    public String getImgLink() {
        return this.ImgLink;
    }
    public void setImgLink(String ImgLink) {
        this.ImgLink = ImgLink;
    }
    public String getContent() {
        return this.Content;
    }
    public void setContent(String Content) {
        this.Content = Content;
    }
    public String getDate() {
        return this.Date;
    }
    public void setDate(String Date) {
        this.Date = Date;
    }
    public Integer getNewsType() {
        return this.NewsType;
    }
    public void setNewsType(Integer NewsType) {
        this.NewsType = NewsType;
    }
}
