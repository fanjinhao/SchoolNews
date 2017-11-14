package com.fayne.android.schoolnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.fayne.android.schoolnews.bean.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan on 2017/11/14.
 */

public class NewsAdapter extends RecyclerView.Adapter {

    private final int TYPE_NOMAL = 0;
    private final int TYPE_FOOT = 1;
    private Context mContext;
    private List<NewsItem> mDatas = new ArrayList<>();
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private boolean mIsLoading;
    private String mError = null;


    public NewsAdapter(Context context) {
        mContext = context;
    }


    public void setDatas(List<NewsItem> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
    }

    public void addDatas(List<NewsItem> datas) {
        mDatas.addAll(datas);
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOT;
        }
        return TYPE_NOMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NOMAL) {
//            View view = LayoutInflater.from(mContext).inflate();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
