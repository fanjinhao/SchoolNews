package com.sohu.changyan.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.sohu.cyan.android.sdk.activity.RepliesActivity;
import com.sohu.cyan.android.sdk.api.Config;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.sohu.cyan.android.sdk.http.CyanRequestListener;
import com.sohu.cyan.android.sdk.http.response.CommentActionResp;
import com.sohu.cyan.android.sdk.http.response.ScoreResp;
import com.sohu.cyan.android.sdk.http.response.TopicCountResp;
import com.sohu.cyan.android.sdk.http.response.UserInfoResp;

import java.util.Locale;

public class CyActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
    private final static int REQUEST_LOGIN_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cy);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cy, menu);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new FixToolBarSectionFragment();

            } else if (position == 1) {
                fragment = new ApiSectionFragment();
            } else {
                fragment = new Fragment();
            }
            Bundle args = new Bundle();
            args.putInt(FixToolBarSectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
            case 0:
                return getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * SDK浮层调用示例
     * 
     * @author kaimengchen
     * 
     */
    public static class FixToolBarSectionFragment extends Fragment {
        public static final String ARG_SECTION_NUMBER = "section_number";
        private CyanSdk cyanSdk;

        public FixToolBarSectionFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Config config = new Config();
            config.ui.toolbar_bg = Color.WHITE;
            // config.ui.style="indent";
            // config.ui.depth = 1;
            // config.ui.sub_size = 20;
            config.comment.showScore = false;
            config.comment.uploadFiles = true;

            config.comment.useFace = false;
            config.login.SSO_Assets_ICon = "ico31.png";
            config.login.SSOLogin = true;
            config.login.loginActivityClass = AppLoginActivity.class;
            try {
                CyanSdk.register(getActivity(), "cyrkKYELy", "your_app_key", "http://your_site.com", config);
            } catch (CyanException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cyanSdk = CyanSdk.getInstance(this.getActivity());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.toolbar_demo, container, false);
            WebView webview = (WebView) rootView.findViewById(R.id.webview);
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });
            String url = "http://changyan.sohu.com/front-demo/page-wap-index.html";
            String sourceId= "yemianzai-changyan-0100";
            webview.getSettings().setJavaScriptEnabled(false);
            webview.loadUrl(url);
            cyanSdk.addCommentToolbar((ViewGroup) rootView, sourceId, "畅言测试页面", url);
            return rootView;
        }
    }

    /**
     * API调用示例
     * 
     * @author kaimengchen
     * 
     */
    public static class ApiSectionFragment extends Fragment {

        private CyanSdk sdk;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sdk = CyanSdk.getInstance(getActivity());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.api_demo, container, false);
            initView(rootView);
            return rootView;
        }

        private void initView(View rootView) {
            final String topicSourceId = "yemianzai-changyan-0100";
            final String topicUrl = "http://changyan.sohu.com/front-demo/page-wap-index.html";
            // 评论数
            Button commentNum = (Button) rootView.findViewById(R.id.commentNum);
            commentNum.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sdk.getCommentCount(topicSourceId, "", 0, new CyanRequestListener<TopicCountResp>() {

                        @Override
                        public void onRequestSucceeded(TopicCountResp data) {
                            Toast.makeText(getActivity(), String.valueOf(data.count), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRequestFailed(CyanException e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            // 评论列表
            Button commentsBtn = (Button) rootView.findViewById(R.id.commentsBtn);
            commentsBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CommentListActivity.class);
                    startActivity(intent);
                }
            });
            // 登录
            Button loginBtn = (Button) rootView.findViewById(R.id.login);
            loginBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN_CODE);
                }
            });
            // 登出
            Button logoutBtn = (Button) rootView.findViewById(R.id.logout);
            logoutBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sdk.logOut();
                    } catch (CyanException e) {
                        Toast.makeText(getActivity(), e.error_msg, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getActivity(), "已登出", Toast.LENGTH_SHORT).show();
                }
            });

            // 用户信息
            Button userInfo = (Button) rootView.findViewById(R.id.userInfo);
            userInfo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CyanRequestListener<UserInfoResp> listener = new CyanRequestListener<UserInfoResp>() {
                        @Override
                        public void onRequestSucceeded(UserInfoResp data) {
                            Toast.makeText(getActivity(),
                                    "name:" + data.nickname + "最新收到回复:" + data.latest_reply_sum + "条",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRequestFailed(CyanException e) {
                            Toast.makeText(getActivity(), e.error_msg, Toast.LENGTH_SHORT).show();
                        }
                    };
                    try {
                        sdk.getUserInfo(listener);
                    } catch (CyanException e) {
                        Toast.makeText(getActivity(), e.error_msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button getScore = (Button) rootView.findViewById(R.id.getScore);
            getScore.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    sdk.getScore(0, topicSourceId, topicUrl, true, new CyanRequestListener<ScoreResp>() {
                        @Override
                        public void onRequestSucceeded(ScoreResp data) {
                            Toast.makeText(getActivity(), data.score, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRequestFailed(CyanException e) {
                            Toast.makeText(getActivity(), e.error_msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            Button userCenter = (Button) rootView.findViewById(R.id.userCenter);
            userCenter.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), RepliesActivity.class);
                    startActivity(i);
                }
            });

            final long topicId = 772348950;
            final long cmtId = 1190453401;
            Button ding = (Button) rootView.findViewById(R.id.ding);
            ding.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //(long topicId, long commentId, CyanSdk.CommentActionType type, final CyanRequestListener<CommentActionResp> listener)
                    try{
                        sdk.commentAction(topicId,cmtId,CyanSdk.CommentActionType.DING, new CyanRequestListener<CommentActionResp>() {
                            @Override
                            public void onRequestSucceeded(CommentActionResp data) {
                                Toast.makeText(getActivity(), "已顶" + data.count, Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onRequestFailed(CyanException e) {
                                Toast.makeText(getActivity(), e.error_msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (CyanException e) {
                        Toast.makeText(getActivity(), "Exception Code=" + e.error_code + ", Msg=" + e.error_msg, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
        case RESULT_OK:
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            break;

        default:
            break;
        }
    }
}
