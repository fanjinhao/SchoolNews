package com.sohu.changyan.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.sohu.cyan.android.sdk.api.CyanSdk.CommentActionType;
import com.sohu.cyan.android.sdk.entity.Comment;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.sohu.cyan.android.sdk.http.CyanRequestListener;
import com.sohu.cyan.android.sdk.http.response.CommentActionResp;
import com.sohu.cyan.android.sdk.http.response.TopicCommentsResp;
import com.sohu.cyan.android.sdk.http.response.TopicLoadResp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CommentListActivity extends Activity {

    private int curPageNo = 1;
    private CyanSdk sdk;
    private long topicId;
    private List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.comment_list);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ListView listView = (ListView) this.findViewById(R.id.commentList);

        sdk = CyanSdk.getInstance(this);

        listView.addHeaderView(inflater.inflate(R.layout.comment_list_header, null));

        sdk.loadTopic("yemianzai-changyan-0100", "", "title", "", 30, 1,
                "", "", 30, 5, new CyanRequestListener<TopicLoadResp>() {

            @Override
            public void onRequestSucceeded(final TopicLoadResp data) {
                topicId = data.topic_id;
                // 发表评论
                Button postBtn = (Button) findViewById(R.id.post);
                postBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sdk.getAccessToken() == null) {
                            Toast.makeText(CommentListActivity.this, "您还未登录，请先登录再发表评论", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(CommentListActivity.this, PostCommentActivity.class);
                        intent.putExtra("topic_id", topicId);
                        startActivity(intent);
                    }
                });
                // 匿名发表
                Button anonymousBtn = (Button) findViewById(R.id.anonymous);
                anonymousBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CommentListActivity.this, PostCommentActivity.class);
                        intent.putExtra("topic_id", topicId);
                        intent.putExtra("anonymouse", true);
                        startActivity(intent);
                    }
                });
                // 翻页
                Button nextPage = (Button) findViewById(R.id.nextPage);
                nextPage.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curPageNo++;
                        sdk.getTopicComments(topicId, 30, curPageNo, "", "", 0, 0,new CyanRequestListener<TopicCommentsResp>() {
                            @Override
                            public void onRequestSucceeded(TopicCommentsResp data) {
                                for (Comment comment : data.comments) {
                                    listData.add(getListItemData(comment));
                                    // 创建SimpleAdapter适配器将数据绑定到item显示控件上
                                    CommentAdapter adapter = new CommentAdapter(CommentListActivity.this, listData);
                                    // 实现列表的显示
                                    listView.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onRequestFailed(CyanException e) {
                                Toast.makeText(CommentListActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                // unmask emoji
                for(Comment cmt : data.hots) {
                    cmt.content = sdk.unmaskEmoji(cmt.content);
                }
                for(Comment cmt : data.comments) {
                    cmt.content = sdk.unmaskEmoji(cmt.content);
                }

                // 获取到集合数据
                List<Comment> hots = data.hots;
                List<Comment> latest = data.comments;



                if (hots != null) {
                    for (Comment comment : hots) {
                        listData.add(getListItemData(comment));
                    }
                }
                if (latest != null) {
                    for (Comment comment : latest) {
                        listData.add(getListItemData(comment));
                    }
                }
                // 创建SimpleAdapter适配器将数据绑定到item显示控件上
                CommentAdapter adapter = new CommentAdapter(CommentListActivity.this, listData);
                // 实现列表的显示
                listView.setAdapter(adapter);
            }

            @Override
            public void onRequestFailed(CyanException e) {
                Toast.makeText(CommentListActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
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

            Button ding = (Button) view.findViewById(R.id.ding);
            Button cai = (Button) view.findViewById(R.id.cai);
            Button reply = (Button) view.findViewById(R.id.reply);
            Button replies = (Button) view.findViewById(R.id.replies);
//            Button floors = (Button) view.findViewById(R.id.floors);

            OnClickListener actionListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CommentActionType type = v.getId() == R.id.cai ? CommentActionType.CAI
                            : CommentActionType.DING;
                    try {
                        sdk.commentAction(topicId, (Long) itemData.get("id"), type,
                                new CyanRequestListener<CommentActionResp>() {
                                    @Override
                                    public void onRequestSucceeded(CommentActionResp data) {
                                        String action = type.equals(CommentActionType.DING) ? "顶数" : "踩数";
                                        Toast.makeText(CommentListActivity.this, action + ":" + data.count,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onRequestFailed(CyanException e) {
                                        Toast.makeText(CommentListActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
                                    }

                                });
                    } catch (CyanException e) {

                    }

                }
            };
            ding.setOnClickListener(actionListener);
            cai.setOnClickListener(actionListener);
            reply.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CommentListActivity.this, PostCommentActivity.class);
                    intent.putExtra("topic_id", topicId);
                    intent.putExtra("reply_id", (Long) itemData.get("id"));
                    intent.putExtra("reply_nick", itemData.get("nickname").toString());
                    startActivity(intent);
                }
            });
            replies.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CommentListActivity.this, CommentRepliesActivity.class);
                    intent.putExtra("topic_id", topicId);
                    intent.putExtra("comment_id", (Long) itemData.get("id"));
                    startActivity(intent);
                }
            });
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
