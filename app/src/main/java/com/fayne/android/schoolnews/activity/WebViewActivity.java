package com.fayne.android.schoolnews.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.fayne.android.schoolnews.R;
import com.just.agentweb.AgentWeb;

public class WebViewActivity extends Activity {

    private View mWebView;
    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        mWebView = findViewById(R.id.web_view);

        Bundle data = getIntent().getExtras();
        String url = data.getString("url");
        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new WebHandle(url).run();
    }

    class WebHandle implements Runnable{
        String url;

        public WebHandle(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            ready(url);
        }
    }

    private void ready(String url) {
        mAgentWeb = AgentWeb.with(WebViewActivity.this)
                .setAgentWebParent((LinearLayout) mWebView, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }


}