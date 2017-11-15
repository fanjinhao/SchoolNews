package com.fayne.android.schoolnews.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.fayne.android.schoolnews.R;
import com.fayne.android.schoolnews.bean.CommonException;
import com.fayne.android.schoolnews.bean.HtmlFrame;
import com.fayne.android.schoolnews.bean.NewsDetail;
import com.fayne.android.schoolnews.biz.NewsDetailBiz;
import com.fayne.android.schoolnews.util.DataUtil;
import com.githang.statusbar.StatusBarCompat;
import com.jaeger.library.StatusBarUtil;

/**
 * Created by fan on 2017/11/15.
 */

public class NewsInfoActivity extends BaseActivity {
    private ImageView mBack;
    private SwipeRefreshLayout mRefresh;
    private WebView mWeb;
    private String mLink;
    private WebSettings mWebSettings;
    private NewsDetailBiz mNewsDetail;
    private TextView mTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info);
        StatusBarUtil.setColor(this, 0x3F51B5);
        initView();
        mLink = getIntent().getStringExtra("link");
        mNewsDetail = new NewsDetailBiz();
        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(true);
            }
        });
        new LoadDataTask().execute();
    }

    class LoadDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String html = null;
            try {
                html = DataUtil.doGet(mLink);
            } catch (CommonException e) {
                e.printStackTrace();
            }
            return html;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!TextUtils.isEmpty(s)) {
                mTag.setVisibility(View.GONE);
                NewsDetail news = mNewsDetail.getNewsDetail(s);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(formatHtml(HtmlFrame.FRAME, news.getTitle(), news.getInfo(), news.getText()));
                mWeb.loadData(stringBuffer.toString(), "text/html; charset=UTF-8", null);
            } else {
                mTag.setVisibility(View.VISIBLE);
            }
            mRefresh.setRefreshing(false);
        }
    }


    private void initView() {
        mRefresh = findViewById(R.id.id_newsinfo_refresh);
        mWeb = findViewById(R.id.id_newsinfo_webview);
        mTag = findViewById(R.id.id_loadfailed);
        mBack = findViewById(R.id.id_imb_back);
        mWebSettings = mWeb.getSettings();
        mWebSettings.setSupportZoom(true);
        mWeb.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mRefresh.setRefreshing(false);
            }
        });

        mRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary,
                R.color.colorPrimary, R.color.colorPrimary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadDataTask().execute();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /***
     * 格式化html
     * @param frame
     * @param title
     * @param info
     * @param texts
     * @return
     */
    private String formatHtml(String frame, String title, String info, String texts) {
        return String.format(frame, title, info, texts);
    }

    public static void actionStart(Context context, String url) {
        Intent intent = new Intent(context, NewsInfoActivity.class);
        intent.putExtra("link", url);
        context.startActivity(intent);
    }
}
