package com.fayne.android.schoolnews.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
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
    private SwipeRefreshLayout mRefresh;
    private WebView mWeb;
    private String mLink;
    private WebSettings mWebSettings;
    private NewsDetailBiz mNewsDetail;
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
                stringBuffer.append(formatHtml(HtmlFrame.FRAME, news.getTitle(), news.getInfo(), news.getText()));
                mWeb.loadData(stringBuffer.toString(), "text/html; charset=UTF-8", null);
                share(news.getTitle(), news.getText().substring(0, 10));
            } else {
                mTag.setVisibility(View.VISIBLE);
            }
            mRefresh.setRefreshing(false);
        }

        private void share(String title, String content) {
            BiliShareConfiguration configuration = new BiliShareConfiguration.Builder(NewsInfoActivity.this)
                    .qq("1106427353")
                    .weixin("2333333")
                    .sina("3973212242", "https://api.weibo.com/oauth2/default.html", "")
                    .build();
            final BiliShare shareClient = BiliShare.global();
            shareClient.config(configuration);
            final BaseShareParam params = new ShareParamText(title, content, mLink);
            final FloatingActionButton fab = findViewById(R.id.fab_share);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeMenu(fab);
                }
            });
            mFabOpened = false;
            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View view) {
                    if (!mFabOpened) {
                        openMenu(view, shareClient, params);
                    } else {
                        closeMenu(view);
                    }

                }
            });
        }
    }

    private void closeMenu(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, -155, -135);
        animator.setDuration(500);
        animator.start();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.7f, 0);
        alphaAnimation.setDuration(500);
        mTextView.setAnimation(alphaAnimation);
        mTextView.setVisibility(View.GONE);
        mFabOpened = false;
    }

    private void openMenu(final View view, BiliShare shareClient, BaseShareParam params) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, -155, -135);
        animator.setDuration(500);
        animator.start();
        mTextView.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 0.7f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        mTextView.startAnimation(alphaAnimation);
        mFabOpened = true;
        shareClient.share(NewsInfoActivity.this, SocializeMedia.SINA, params, new SocializeListeners.ShareListener() {
            @Override
            public void onStart(SocializeMedia type) {
            }

            @Override
            public void onProgress(SocializeMedia type, String progressDesc) {
            }

            @Override
            public void onSuccess(SocializeMedia type, int code) {
                Snackbar.make(view, "分享成功", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeMedia type, int code, Throwable error) {
                Snackbar.make(view, "分享出错", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SocializeMedia type) {
                Snackbar.make(view, "取消分享", Snackbar.LENGTH_SHORT).show();
            }
        });
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
