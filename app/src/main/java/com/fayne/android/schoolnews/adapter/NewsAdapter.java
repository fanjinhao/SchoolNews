package com.fayne.android.schoolnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fayne.android.schoolnews.R;
import com.fayne.android.schoolnews.bean.NewsItem;
import com.squareup.picasso.Picasso;

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
    private OnItemClickListener mOnItemClickListener;
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_listview_item, parent, false);
            return new ItemViewHolder(view);
        }
        if (viewType == TYPE_FOOT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_footer, parent, false);
            return new FootHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).mTitle.setText(mDatas.get(position).getTitle());
            ((ItemViewHolder) holder).mContent.setText(mDatas.get(position).getContent());
            ((ItemViewHolder) holder).mDate.setText(mDatas.get(position).getDate());
            if (mDatas.get(position).getImgLink() != null) {
                ((ItemViewHolder) holder).mIcon.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(mDatas.get(position).getImgLink()).placeholder(R.mipmap.news_default_icon)
                        .error(R.mipmap.news_default_icon).into(((ItemViewHolder) holder). mIcon);
            }else{
                ((ItemViewHolder) holder).mIcon.setVisibility(View.GONE);
            }
            //TODO 设置回调事件处理
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView, pos);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }
            if (holder instanceof FootHolder) {
                ((FootHolder) holder).mFoot.setVisibility(mIsLoading ? View.VISIBLE : View.GONE);
                if (mError != null) {
                    ((FootHolder) holder).mProgressBar.setVisibility(View.GONE);
                    ((FootHolder) holder).mMessage.setText(mError);
                } else {
                    ((FootHolder) holder).mProgressBar.setVisibility(View.VISIBLE);
                    ((FootHolder) holder).mMessage.setText("加载中....");
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + 1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<NewsItem> getDatas() {
        return mDatas;
    }

    public String getError() {
        return mError;
    }

    public void setError(String error) {
        mError = error;
    }
}
