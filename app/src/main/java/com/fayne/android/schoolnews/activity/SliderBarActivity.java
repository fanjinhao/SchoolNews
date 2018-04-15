package com.fayne.android.schoolnews.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fayne.android.schoolnews.R;
import com.fayne.android.schoolnews.activity.BaseActivity;
import com.fayne.android.schoolnews.adapter.AdapterComment;
import com.fayne.android.schoolnews.bean.Comment;
import com.fayne.android.schoolnews.fragment.MainFragment;
import com.fayne.android.schoolnews.util.ActivityCollector;
import com.fayne.android.schoolnews.widget.SystemBarTintManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SliderBarActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private View mNewsInfo, mSocial, mSchoolUrl;
    private Toolbar mToolbar;
    public static String user;
    private static final String TAG = "SliderBarActivity";
    private List<Comment> data;
    private ListView mListView;
    private RelativeLayout rl_comment;
    private Button mComment_send;
    private TextView hide_down;
    private EditText mComment_content;
    private AdapterComment mAdapterComment;
    private String url = "http://www.fayne.cn/hello.htm";
    private String connectUrl = "http://project.fayne.cn/getcomment.php";
    private String sendCommentUrl = "http://project.fayne.cn/comment.php";
    private String deleteCommentUrl = "http://project.fayne.cn/deletecomment.php";
    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mFabComment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mNewsInfo.setVisibility(View.VISIBLE);
                    mSchoolUrl.setVisibility(View.GONE);
                    mSocial.setVisibility(View.GONE);
                    mToolbar.setTitle(R.string.app_name);
                    return true;
                case R.id.navigation_dashboard:
                    mNewsInfo.setVisibility(View.GONE);
                    mSocial.setVisibility(View.VISIBLE);
                    mSchoolUrl.setVisibility(View.GONE);
                    mToolbar.setTitle(R.string.social_comment);
                    return true;
                case R.id.navigation_notifications:
                    mSchoolUrl.setVisibility(View.VISIBLE);
                    mNewsInfo.setVisibility(View.GONE);
                    mSocial.setVisibility(View.GONE);
                    mToolbar.setTitle(R.string.school_url);
                    return true;
            }
            return false;
        }
    };

    private TabLayout mTab;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private String []title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliderbar);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mNewsInfo = findViewById(R.id.news_info);
        mSocial = findViewById(R.id.social_comment);
        mSchoolUrl = findViewById(R.id.url_list);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                //将侧边栏顶部延伸至status bar
                drawer.setFitsSystemWindows(true);
                //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
                drawer.setClipToPadding(false);
            }
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //login begin
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String name = pref.getString("user", "null");
        if (!name.equals("null")) {
            user = name;
        } else {
            startActivity(new Intent(SliderBarActivity.this, LoginActivity.class));
            finish();
        }
        //login end

        View headerView = navigationView.getHeaderView(0);
        ImageView imageView = headerView.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SliderBarActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mTab = findViewById(R.id.id_table_layout);
        mViewPager = findViewById(R.id.id_view_pager);


        initData();
        new LoadCommentHandle().run();
    }

    private void initView() {
        mListView = findViewById(R.id.comment_list);
        rl_comment = findViewById(R.id.rl_comment);
        rl_comment.setOnClickListener(this);
        mComment_send = findViewById(R.id.comment_send);
        mComment_send.setOnClickListener(this);
        hide_down = findViewById(R.id.hide_down);
        hide_down.setOnClickListener(this);
        mComment_content = findViewById(R.id.comment_content);
        mComment_content.setOnClickListener(this);
        mFabComment = findViewById(R.id.fab_comment);
        mFabComment.setOnClickListener(this);
        mFabMenu = findViewById(R.id.menu_red);
        mFabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                hideInput();
            }
        });
        data = new ArrayList<>();
        mAdapterComment = new AdapterComment(getApplicationContext(), data);
        mListView.setAdapter(mAdapterComment);
        mAdapterComment.setOnItemDeleteClickListener(new AdapterComment.OnItemDeleteListener() {
            @Override
            public void onDeleteClick(final int j) {
                new AlertDialog.Builder(SliderBarActivity.this).setTitle("删除").setMessage("确认删除此条评论？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                new DeleteCommentHandle(((Comment)(mAdapterComment.getItem(j))).getId(), j).run();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comment_content:
                showInput();
                break;
            case R.id.hide_down:
                hideInput();
                break;
            case R.id.comment_send:
                sendComment();
                break;
            case R.id.fab_comment:
                showInput();
                break;
        }
    }

    // add by fanjinhao for bottomView begin
    private void showInput() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        // show comment
        rl_comment.setVisibility(View.VISIBLE);
        mComment_content.requestFocus();
    }

    private void hideInput() {
        rl_comment.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mComment_content.getWindowToken(), 0);
    }

    private void sendComment() {
        if (mComment_content.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "评论不能为空", Toast.LENGTH_SHORT).show();
        } else {
            new SendCommentHandle(mComment_content.getText().toString()).run();
            mComment_content.setText("");
            Toast.makeText(getApplicationContext(), "评论成功", Toast.LENGTH_SHORT).show();
            hideInput();
        }
    }

    private void sendServerComment(final String content) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(SliderBarActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int id = jsonObject.getInt("id");
                    mAdapterComment.clearAllComment();
                    loadComment();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
                Toast.makeText(SliderBarActivity.this, "服务器错误，评论出错", Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendCommentUrl, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user", user);
                map.put("touser", "");
                map.put("url", url);
                map.put("content", content);
                return map;
            }
        };
        Log.d(TAG, "sendServerComment: "+ content + ", " + url);
        requestQueue.add(stringRequest);
    }

    class LoadCommentHandle implements Runnable {
        @Override
        public void run() {
            loadComment();
        }
    }

    class SendCommentHandle implements Runnable {
        String content = null;
        public SendCommentHandle(String content) {
            this.content = content;
        }
        @Override
        public void run() {
            sendServerComment(content);
        }
    }

    private void loadComment() {
        RequestQueue requestQueue = Volley.newRequestQueue(SliderBarActivity.this);


        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: " + response.toString());

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        int commentid = object.getInt("id");
                        String id = object.getString("userid");
                        String content = object.getString("content");
                        String time = object.getString("time");
                        Comment com = new Comment(id, content, time);
                        com.setId(commentid);
                        com.setmName(com.getmName() + ":");
                        com.setmContent(com.getmContent());
                        com.setmTime(com.getmTime());
                        mAdapterComment.addComment(com);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: error", error);
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST, connectUrl, listener, errorListener) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("url", url);
                return map;
            }
        };


        requestQueue.add(request);

    }
    // add by fanjinhao for bottomView end

    class DeleteCommentHandle implements Runnable {
        int id, i;
        public DeleteCommentHandle(int id, int i) {
            this.id = id;
            this.i = i;
        }
        @Override
        public void run() {
            RequestQueue requestQueue = Volley.newRequestQueue(SliderBarActivity.this);

            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(SliderBarActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: ", error);
                }
            };

            StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteCommentUrl, listener, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(id));
                    return map;
                }
            };

            requestQueue.add(stringRequest);
            mAdapterComment.deleteComment(i);
        }
    }

    private void initData() {
        title = new String[] {"安科新闻", "通知公告", "校园动态", "学术信息"};
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return MainFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return title.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return title[position % title.length];
            }
        };
        mViewPager.setAdapter(mAdapter);
        mTab.setupWithViewPager(mViewPager);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sliderbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putString("user", "null");
            editor.commit();
            startActivity(new Intent(SliderBarActivity.this, LoginActivity.class));
            SliderBarActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Toast.makeText(this, "测试", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SliderBarActivity.class));
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(this, "gallery", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(this, "slidershow", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(this, "manage", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "nav_share", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(this, "nav_send", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.finishAll();
    }
}
