package com.sohu.changyan.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.entity.Comment;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.sohu.cyan.android.sdk.http.CyanRequestListener;
import com.sohu.cyan.android.sdk.http.response.CommentReplyResp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CommentRepliesActivity extends Activity {
    private int curPageNo = 1;
    private CyanSdk sdk;
    private long topicId;
    private long commentId;
    private List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        topicId = getIntent().getLongExtra("topic_id", 0);
        commentId = getIntent().getLongExtra("comment_id", 0);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.replies);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ListView listView = (ListView) this.findViewById(R.id.repliesList);

        sdk = CyanSdk.getInstance(this);

        listView.addHeaderView(inflater.inflate(R.layout.reply_header, null));

        sdk.commentReplies(topicId, commentId, 1, 30, "", new CyanRequestListener<CommentReplyResp>() {

            @Override
            public void onRequestSucceeded(CommentReplyResp data) {
                // 翻页
                Button nextPage = (Button) findViewById(R.id.nextPage);
                nextPage.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curPageNo++;
                        sdk.commentReplies(topicId, commentId, 1, 30, "", new CyanRequestListener<CommentReplyResp>() {
                            @Override
                            public void onRequestSucceeded(CommentReplyResp data) {
                                for (Comment comment : data.comments) {
                                    listData.add(getListItemData(comment));
                                    // 创建SimpleAdapter适配器将数据绑定到item显示控件上
                                    CommentAdapter adapter = new CommentAdapter(CommentRepliesActivity.this, listData);
                                    // 实现列表的显示
                                    listView.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onRequestFailed(CyanException e) {
                                Toast.makeText(CommentRepliesActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                // 获取到集合数据
                for (Comment comment : data.comments) {
                    listData.add(getListItemData(comment));
                }
                // 创建SimpleAdapter适配器将数据绑定到item显示控件上
                CommentAdapter adapter = new CommentAdapter(CommentRepliesActivity.this, listData);
                // 实现列表的显示
                listView.setAdapter(adapter);
            }

            @Override
            public void onRequestFailed(CyanException e) {
                Toast.makeText(CommentRepliesActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HashMap<String, Object> getListItemData(Comment comment) {
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("id", comment.comment_id);
        item.put("nickname", comment.passport.nickname);
        item.put("time", sdf.format(new Date(comment.create_time)));
        item.put("content", comment.content);
        return item;
    }

    private class CommentAdapter extends BaseAdapter {

        Context context;
        List<HashMap<String, Object>> data;

        public CommentAdapter(Context context, List<HashMap<String, Object>> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.comment_item, null);

            final HashMap<String, Object> itemData = data.get(position);

            TextView idView = (TextView) view.findViewById(R.id.id);
            idView.setText(itemData.get("id").toString());
            TextView nickname = (TextView) view.findViewById(R.id.nickname);
            nickname.setText(itemData.get("nickname").toString());
            TextView time = (TextView) view.findViewById(R.id.time);
            time.setText(itemData.get("time").toString());
            TextView content = (TextView) view.findViewById(R.id.content);
            content.setText(itemData.get("content").toString());

            return view;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

    }
}
