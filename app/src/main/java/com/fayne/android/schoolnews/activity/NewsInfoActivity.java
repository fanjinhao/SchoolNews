package com.fayne.android.schoolnews.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.lang.reflect.InvocationTargetException;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by fan on 2017/11/15.
 * Update by fan on 2018/4/14.
 */

public class NewsInfoActivity extends BaseActivity {
    private SwipeRefreshLayout mRefresh;
    private WebView mWeb;
    private String mLink;
    private WebSettings mWebSettings;
    private NewsDetailBiz mNewsDetail;
    public static String mTitle = "安科资讯";
    private  String mText = "安科资讯";
    private String mInfo = "信息";
    public static String mUrl = "https://www.fayne.cn";
    private TextView mTag;
    private TextView mTextView;
    private Toolbar mToolbar;
    private FloatingActionMenu mMenuRed;
    private FloatingActionButton mFabShare;
    private FloatingActionButton mFabComment;

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
        mToolbar.setTitle(R.string.content);
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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }






    @Override
    protected void onPause() {
        super.onPause();
        try {
            mWeb.getClass().getMethod("onPause").invoke(mWeb, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mWeb.getClass().getMethod("onResume").invoke(mWeb, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String name = pref.getString("user", "null");
        if (name.equals("null")) {
            startActivity(new Intent(NewsInfoActivity.this, LoginActivity.class));
        }
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
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("user", "null");
                editor.commit();
                startActivity(new Intent(NewsInfoActivity.this, LoginActivity.class));
                NewsInfoActivity.this.finish();
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
            } else {
                mTag.setVisibility(View.VISIBLE);
            }
            mRefresh.setRefreshing(false);

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
        // FloatActionBar begin
        mMenuRed = findViewById(R.id.menu_red);
        mFabComment = findViewById(R.id.fab_comment);
        mFabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewsInfoActivity.this, CommentActivity.class));
            }
        });
        mFabShare = findViewById(R.id.fab_share);
        mFabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShare();
            }
        });
        //FloatActionBar end
        mRefresh = findViewById(R.id.id_newsinfo_refresh);
        mWeb = findViewById(R.id.id_newsinfo_webview);
        mTag = findViewById(R.id.id_loadfailed);
        //获取webView的相关设置
        mWebSettings = mWeb.getSettings();
        //webView的缓存模式
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        mWeb.setWebChromeClient(new WebChromeClient());

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
