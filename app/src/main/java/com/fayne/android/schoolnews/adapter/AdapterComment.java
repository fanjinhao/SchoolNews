package com.fayne.android.schoolnews.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fayne.android.schoolnews.R;
import com.fayne.android.schoolnews.activity.SliderBarActivity;
import com.fayne.android.schoolnews.bean.Comment;

import java.util.List;


/**
 * Created by fan on 18-4-10.
 */

public class AdapterComment extends BaseAdapter{

    Context mContext;
    List<Comment> mData;
    private static final String TAG = "AdapterComment";
    private OnItemDeleteListener onItemDeleteListener;
    public AdapterComment(Context context, List<Comment> data) {
        mContext = context;
        mData = data;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public List<Comment> getmData() {
        return mData;
    }

    public void setmData(List<Comment> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }



    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null);
            holder.comment_name = view.findViewById(R.id.comment_name);
            holder.comment_content = view.findViewById(R.id.comment_content);
            holder.comment_time = view.findViewById(R.id.comment_time);
            holder.delete_comment = view.findViewById(R.id.delete_comment);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.comment_name.setText(mData.get(i).getmName());
        holder.comment_content.setText(mData.get(i).getmContent());
        holder.comment_time.setText(mData.get(i).getmTime());
        String name = mData.get(i).getmName();
        String user = getUser();
        holder.delete_comment.setVisibility(getUser().equals("admin") ? View.VISIBLE : mData.get(i).getmName().toString().equals(getUser()+":") ? View.VISIBLE : View.GONE);


        holder.delete_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemDeleteListener.onDeleteClick(i);
            }
        });
        return view;
    }

    public void addComment(Comment comment) {
        mData.add(comment);
        notifyDataSetChanged();
    }

    public void hideDelete(int i) {

    }

    public void deleteComment(int i) {
        mData.remove(i);
        notifyDataSetChanged();
    }

    public void clearAllComment() {
        mData.clear();
    }

    public String getUser() {
        return SliderBarActivity.user;
    }

    public interface OnItemDeleteListener {
        void onDeleteClick(int i);
    }

    public void setOnItemDeleteClickListener(OnItemDeleteListener onItemDeleteClickListener) {
        this.onItemDeleteListener = onItemDeleteClickListener;
    }

    private static class ViewHolder {
        public TextView comment_name;
        public TextView comment_content;
        public TextView comment_time;
        public TextView delete_comment;
    }
}
