package com.fayne.android.schoolnews.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.fayne.android.schoolnews.R;
import com.fayne.android.schoolnews.bean.CommonException;
import com.fayne.android.schoolnews.bean.HtmlFrame;
import com.fayne.android.schoolnews.bean.NewsDetail;
import com.fayne.android.schoolnews.biz.NewsDetailBiz;
import com.fayne.android.schoolnews.util.DataUtil;
import com.fayne.android.schoolnews.widget.SystemBarTintManager;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by fan on 2017/11/15.
 */

public class NewsInfoActivity extends BaseActivity {
    private SwipeRefreshLayout mRefresh;
    private WebView mWeb;
    private String mLink;
    private WebSettings mWebSettings;
    private NewsDetailBiz mNewsDetail;
    private String mTitle = "安科资讯";
    private String mText = "安科资讯";
    private String mInfo = "信息";
    private String mUrl = "https://www.fayne.cn";
    private TextView mTag;
    private boolean mFabOpened;
    private TextView mTextView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info);
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
        mToolbar = findViewById(R.id.my_toolbar);
        mToolbar.setTitle("正文");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.activity_back_bg);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newsdetail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

                mUrl = mLink;
                stringBuffer.append(formatHtml(HtmlFrame.FRAME, news.getTitle(), news.getInfo(), news.getText()));
                mTitle = news.getTitle();
                mInfo = news.getInfo();
                mText = news.getTitle() + "\n链接：" + mUrl;
                mWeb.loadData(stringBuffer.toString(), "text/html; charset=UTF-8", null);
                share("安科新闻", news.getTitle());
            } else {
                mTag.setVisibility(View.VISIBLE);
            }
            mRefresh.setRefreshing(false);
        }

        private void share(String title, String content) {
            final FloatingActionButton fab = findViewById(R.id.fab_share);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            mFabOpened = false;
            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View view) {
                    showShare();
                }
            });
        }
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(mTitle);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(mUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mText);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(mUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(mUrl);

        // 启动分享GUI
        oks.show(this);
    }



    private void initView() {
        mRefresh = findViewById(R.id.id_newsinfo_refresh);
        mWeb = findViewById(R.id.id_newsinfo_webview);
        mTag = findViewById(R.id.id_loadfailed);
        mWebSettings = mWeb.getSettings();
        mWebSettings.setSupportZoom(true);
        mTextView = findViewById(R.id.cloud);
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
