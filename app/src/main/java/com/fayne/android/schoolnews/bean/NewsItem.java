package com.fayne.android.schoolnews.bean;

/**
 * Created by fan on 2017/11/14.
 */

public class NewsItem {
    private int mId;
    //标题
    private String mTitle;
    //链接
    private String mLink;
    //图片链接
    private String mImgLink;
    //内容
    private String mContent;
    //发布时间
    private String mDate;
    //类型
    private int mNewsType;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getImgLink() {
        return mImgLink;
    }

    public void setImgLink(String imgLink) {
        mImgLink = imgLink;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getNewsType() {
        return mNewsType;
    }

    public void setNewsType(int newsType) {
        mNewsType = newsType;
    }

    @Override
    public String toString() {
        return String.format("NewsItem[id=%d, title=%s, link=%s, imgLink=%s, content=%s, data=%s, type=%d]",
                mId, mTitle, mLink, mImgLink, mContent, mDate, mNewsType);
    }
}
