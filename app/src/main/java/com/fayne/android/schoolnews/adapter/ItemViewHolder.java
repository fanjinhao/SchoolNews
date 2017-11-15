package com.fayne.android.schoolnews.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fayne.android.schoolnews.R;

/**
 * Created by fan on 2017/11/15.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {
//    ImageView mIcon;
    TextView mTitle;
//    TextView mContent;
    TextView mDate;

    public ItemViewHolder(View itemView) {
        super(itemView);
//        mIcon = itemView.findViewById(R.id.id_newsItem_icon);
        mTitle = itemView.findViewById(R.id.id_newsItem_title);
//        mContent = itemView.findViewById(R.id.id_newItem_content);
        mDate = itemView.findViewById(R.id.id_newsItem_date);
    }
}
