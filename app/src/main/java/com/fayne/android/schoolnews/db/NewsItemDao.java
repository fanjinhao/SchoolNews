package com.fayne.android.schoolnews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fayne.android.schoolnews.bean.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan on 2017/11/14.
 */

public class NewsItemDao {

    public static final int PER_ITEM_COUNT = 6;
    private DBHelper mHelper;

    public static final String[] COLUMNS = {"title", "link", "imgLink", "content", "date", "newsType"};
    public NewsItemDao(Context context) {
        mHelper = new DBHelper(context);
    }

    /***
     * 更新数据表
     * @param newsType 新闻类型
     * @param items
     */
    public void refreshData(int newsType, List<NewsItem> items) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "DELETE FROM " + mHelper.TABLE_NEWS + " WHERE newsType=?";
            db.execSQL(sql, new Object[]{newsType});
            for (NewsItem item : items) {
                if (item != null) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMNS[0], item.getTitle());
                    values.put(COLUMNS[1], item.getLink());
                    values.put(COLUMNS[2], item.getImgLink());
                    values.put(COLUMNS[3], item.getContent());
                    values.put(COLUMNS[4], item.getDate());
                    values.put(COLUMNS[5], item.getNewsType());
                    db.insert(mHelper.TABLE_NEWS, null, values);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /***
     * 添加新闻列表
     * @param items
     */
    public void addNewsItems(List<NewsItem> items) {
        if (null == items || items.isEmpty()) {
            return;
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (NewsItem item : items) {
                if (null == item) {
                    continue;
                }
                ContentValues values = new ContentValues();
                values.put(COLUMNS[0], item.getTitle());
                values.put(COLUMNS[1], item.getLink());
                values.put(COLUMNS[2], item.getImgLink());
                values.put(COLUMNS[3], item.getContent());
                values.put(COLUMNS[4], item.getDate());
                values.put(COLUMNS[5], item.getNewsType());
                db.insert(mHelper.TABLE_NEWS, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<NewsItem> getNewsItems(int page, int newsType) {
        List<NewsItem> items = new ArrayList<>();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int offset = (page - 1) * PER_ITEM_COUNT;
        String sql = "SELECT title, link, imgLink, content, date, newsType from " + mHelper.TABLE_NEWS + " WHERE newsType=? LIMIT ?,?;";
        try {
            db.beginTransaction();
            Cursor cursor = db.rawQuery(sql, new String[]{newsType + "", offset + "", PER_ITEM_COUNT + ""});
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    NewsItem item = new NewsItem();
                    item.setTitle(cursor.getString(0));
                    item.setLink(cursor.getString(1));
                    item.setImgLink(cursor.getString(2));
                    item.setContent(cursor.getString(3));
                    item.setDate(cursor.getString(4));
                    item.setNewsType(cursor.getInt(5));
                    items.add(item);
                } while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        } catch ( Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return items;
    }
}
