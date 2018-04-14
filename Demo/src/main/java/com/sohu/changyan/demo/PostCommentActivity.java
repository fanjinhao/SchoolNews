package com.sohu.changyan.demo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.sohu.cyan.android.sdk.http.CyanRequestListener;
import com.sohu.cyan.android.sdk.http.response.AttachementResp;
import com.sohu.cyan.android.sdk.http.response.SubmitResp;
import com.sohu.cyan.android.sdk.util.Constants;

import java.io.File;

public class PostCommentActivity extends Activity {

    private long topicId;
    private long replyId;
    private String replyNick;
    private CyanSdk sdk;
    private String attachUrl;
    private int score;
    private boolean anonymouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        score = 0;
        super.onCreate(savedInstanceState);

        sdk = CyanSdk.getInstance(this);

        topicId = getIntent().getLongExtra("topic_id", 0);
        replyId = getIntent().getLongExtra("reply_id", 0);
        replyNick = getIntent().getStringExtra("reply_nick");
        anonymouse = getIntent().getBooleanExtra("anonymouse", false);
        
        this.setContentView(R.layout.post_comment);

        initView();
    }

    private void initView() {
        final EditText contentText = (EditText) findViewById(R.id.content);
        if (replyId > 0) {
            contentText.setHint("回复:@" + replyNick);
        }
        Button attcheBtn = (Button) findViewById(R.id.attache);
        Button submitBtn = (Button) findViewById(R.id.submit);
        RadioGroup scoreRadiao = (RadioGroup) findViewById(R.id.scoreRadio);

        submitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = contentText.getText().toString();
                if (txt == null || txt.trim().equals("")) {
                    Toast.makeText(PostCommentActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    CyanRequestListener<SubmitResp> listener = new CyanRequestListener<SubmitResp>() {

                        @Override
                        public void onRequestSucceeded(SubmitResp data) {
                            Toast.makeText(PostCommentActivity.this, "发表成功,id:" + data.id, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRequestFailed(CyanException e) {
                            Toast.makeText(PostCommentActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
                        }
                    };
                    if(anonymouse){
                        sdk.anonymousSubmitComment(topicId, txt, replyId, attachUrl, Constants.AppType.ANDROID, score, "metadata", "z6e0xTejZBmqP-dQcAGN2lWmTZu8_yk9fW_w7oUvJP8", listener);
                    }else{
                        sdk.submitComment(topicId, txt, replyId, attachUrl,Constants.AppType.ANDROID, score, "metadata", listener);
                    }
                } catch (CyanException e) {
                    Toast.makeText(PostCommentActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        attcheBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);
            }
        });
        
        scoreRadiao.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String text = ((RadioButton) group.findViewById(checkedId)).getText().toString();
                score = Integer.parseInt(text);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String path = getAbsoluteImagePath(data.getData());
            try {
                sdk.attachUpload(new File(path), new CyanRequestListener<AttachementResp>() {
                    @Override
                    public void onRequestSucceeded(AttachementResp data) {
                        attachUrl = data.url;
                    }

                    @Override
                    public void onRequestFailed(CyanException e) {
                        Toast.makeText(PostCommentActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (CyanException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getAbsoluteImagePath(Uri uri) {
        String fileName = null;
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            fileName = cursor.getString(column_index); // 取出文件路径
//            if (!fileName.startsWith("/mnt")) {
//                // 检查是否有”/mnt“前缀
//
//                fileName = "/mnt" + fileName;
//            }
        }
        return fileName;
    }
}
