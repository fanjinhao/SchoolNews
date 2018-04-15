package com.fayne.android.schoolnews.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.fayne.android.schoolnews.adapter.AdapterComment;
import com.fayne.android.schoolnews.bean.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {


    // add comment by fanjinhao begin
    private ListView comment_list;
    private ImageView comment;
    private ImageView chat;
    private LinearLayout rl_enroll;
    private TextView hide_down;
    private EditText comment_content;
    private TextView delete_comment;
    private Button comment_send;
    private RelativeLayout rl_comment;
    private AdapterComment adapterComment;
    private List<Comment> data;
    private String mUrl;
    private String mTitle;
    public static String user;
    private TextView mTextTitle;
    // connect server url
    private String connectUrl = "http://project.fayne.cn/getcomment.php";
    private String sendCommentUrl = "http://project.fayne.cn/comment.php";
    private String deleteCommentUrl = "http://project.fayne.cn/deletecomment.php";
    private static final String TAG = "CommentActivity";
    // add comment by fanjinhao end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_comment);

        // add by fanjinhao for comment begin
        initUser();
        initCommentView();
        new LoadCommentHandle().run();
        // add by fanjinhao for comment end

        Toolbar mToolbar = findViewById(R.id.my_toolbar);
        mToolbar.setTitle(R.string.comment);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.activity_back_bg);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    // add by fanjinhao for comment begin 2018-4-14

    private void initUser() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String name = pref.getString("user", "null");
        if (!name.equals("null")) {
            user = name;
        } else {
            startActivity(new Intent(CommentActivity.this, LoginActivity.class));
            finish();
        }

        mUrl = NewsInfoActivity.mUrl;
        mTitle = NewsInfoActivity.mTitle;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
        RequestQueue requestQueue = Volley.newRequestQueue(CommentActivity.this);

        mTextTitle.setText(mTitle);
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
                        adapterComment.addComment(com);
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
                map.put("url", mUrl);
                return map;
            }
        };


        requestQueue.add(request);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CommentActivity.this.finish();
        }
        return true;
    }

    private void initCommentView() {
        mTextTitle = findViewById(R.id.news_title);
        comment_list = (ListView) findViewById(R.id.comment_list);
        //comment_list.setOnItemClickListener();
        comment = (ImageView) findViewById(R.id.comment);
        comment.setOnClickListener(this);
        chat = (ImageView) findViewById(R.id.chat);
        chat.setOnClickListener(this);
        rl_enroll = (LinearLayout) findViewById(R.id.rl_enroll);
        rl_enroll.setOnClickListener(this);
        hide_down = (TextView) findViewById(R.id.hide_down);
        hide_down.setOnClickListener(this);
        comment_content = (EditText) findViewById(R.id.comment_content);
        comment_content.setOnClickListener(this);
        comment_send = (Button) findViewById(R.id.comment_send);
        comment_send.setOnClickListener(this);
        rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);
        rl_comment.setOnClickListener(this);
        data = new ArrayList<>();
        adapterComment = new AdapterComment(getApplicationContext(), data);
        comment_list.setAdapter(adapterComment);
        adapterComment.setOnItemDeleteClickListener(new AdapterComment.OnItemDeleteListener() {
            @Override
            public void onDeleteClick(final int j) {
                new AlertDialog.Builder(CommentActivity.this).setTitle("删除").setMessage("确认删除此条评论？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                new DeleteCommentHandle(((Comment)(adapterComment.getItem(j))).getId(), j).run();
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

    class DeleteCommentHandle implements Runnable {
        int id, i;
        public DeleteCommentHandle(int id, int i) {
            this.id = id;
            this.i = i;
        }
        @Override
        public void run() {
            RequestQueue requestQueue = Volley.newRequestQueue(CommentActivity.this);

            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(CommentActivity.this, response, Toast.LENGTH_SHORT).show();
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
            adapterComment.deleteComment(i);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String name = pref.getString("user", "null");
        if (name.equals("null")) {
            startActivity(new Intent(CommentActivity.this, LoginActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment:
                showInput();
                break;
            case R.id.hide_down:
                // hide comment
                hideInput();
                break;
            case R.id.comment_send:
                sendComment();
                break;
        }
    }

    private void showInput() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        // show comment
        rl_enroll.setVisibility(View.GONE);
        rl_comment.setVisibility(View.VISIBLE);
        comment_content.requestFocus();
    }

    private void hideInput() {
        rl_enroll.setVisibility(View.VISIBLE);
        rl_comment.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment_content.getWindowToken(), 0);
    }

    private void sendComment() {
        if (comment_content.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "评论不能为空", Toast.LENGTH_SHORT).show();
        } else {
            new SendCommentHandle(comment_content.getText().toString()).run();
            comment_content.setText("");
            Toast.makeText(getApplicationContext(), "评论成功", Toast.LENGTH_SHORT).show();
            hideInput();
        }
    }

    private void sendServerComment(final String content) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(CommentActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int id = jsonObject.getInt("id");
                    adapterComment.clearAllComment();
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
                Toast.makeText(CommentActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendCommentUrl, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user", user);
                map.put("touser", "");
                map.put("url", mUrl);
                map.put("content", content);
                return map;
            }
        };
        Log.d(TAG, "sendServerComment: "+ content + ", " + mUrl);
        requestQueue.add(stringRequest);
    }
    // add by fanjinhao for commnet end 2018-4-14

}
