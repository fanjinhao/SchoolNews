package com.fayne.android.schoolnews.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fayne.android.schoolnews.R;
import com.fayne.android.schoolnews.widget.ProgressWheel;

/**
 * Created by fan on 2017/11/15.
 */

public class FootHolder extends RecyclerView.ViewHolder{

    LinearLayout mFoot;
    ProgressWheel mProgressBar;
    TextView mMessage;

    public FootHolder(View itemView) {
        super(itemView);
        mFoot = itemView.findViewById(R.id.item_news_foot);
        mProgressBar = itemView.findViewById(R.id.item_news_progressbar);
        mMessage = itemView.findViewById(R.id.item_news_message);
    }
}
