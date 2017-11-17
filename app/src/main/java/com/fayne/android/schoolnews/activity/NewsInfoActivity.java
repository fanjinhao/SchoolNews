package com.fayne.android.schoolnews.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.socialize.share.core.BiliShare;
import com.bilibili.socialize.share.core.BiliShareConfiguration;
import com.bilibili.socialize.share.core.SocializeListeners;
import com.bilibili.socialize.share.core.SocializeMedia;
import com.bilibili.socialize.share.core.shareparam.BaseShareParam;
import com.bilibili.socialize.share.core.shareparam.ShareParamText;
import com.fayne.android.schoolnews.R;
import com.fayne.android.schoolnews.bean.CommonException;
import com.fayne.android.schoolnews.bean.HtmlFrame;
import com.fayne.android.schoolnews.bean.NewsDetail;
import com.fayne.android.schoolnews.biz.NewsDetailBiz;
import com.fayne.android.schoolnews.util.DataUtil;
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
//        StatusBarUtil.setColor(this, 0x3F51B5);
        StatusBarUtil.setColor(this, Color.rgb(63, 81, 181));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = this.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor();
//        }
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
        BiliShareConfiguration configuration = new BiliShareConfiguration.Builder(this)
                .qq("1106427353")
                .build();
        final BiliShare shareClient = BiliShare.global();
        shareClient.config(configuration);
        final BaseShareParam params = new ShareParamText("安科新闻", "测试新闻", mLink);
        FloatingActionButton fab = findViewById(R.id.fab_share);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                shareClient.share(NewsInfoActivity.this, SocializeMedia.QQ, params, new SocializeListeners.ShareListener() {
                    @Override
                    public void onStart(SocializeMedia type) {

                    }

                    @Override
                    public void onProgress(SocializeMedia type, String progressDesc) {

                    }

                    @Override
                    public void onSuccess(SocializeMedia type, int code) {

                    }

                    @Override
                    public void onError(SocializeMedia type, int code, Throwable error) {

                    }

                    @Override
                    public void onCancel(SocializeMedia type) {

                    }
                });
            }
        });
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
