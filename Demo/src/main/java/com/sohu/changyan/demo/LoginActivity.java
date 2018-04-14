package com.sohu.changyan.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.sohu.cyan.android.sdk.api.CallBack;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.entity.AccountInfo;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.sohu.cyan.android.sdk.util.Constants;

import java.util.Set;

public class LoginActivity extends Activity {

    private CyanSdk sdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdk = CyanSdk.getInstance(this);
        this.setContentView(R.layout.login);
        initView();
    }

    private void initView() {
        Button weiboLogin = (Button) findViewById(R.id.loginweibo);
        Button qqLogin = (Button) findViewById(R.id.loginqq);
        Button sohuLogin = (Button) findViewById(R.id.loginsohu);
        Button ssoLogin = (Button) findViewById(R.id.loginsso);

        weiboLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sdk.startAuthorize(Constants.PlatFormId.PLATFORM_WEIBO, LoginActivity.this);
            }
        });

        qqLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sdk.startAuthorize(Constants.PlatFormId.PLATFORM_QQ, LoginActivity.this);
            }
        });

        sohuLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sdk.startAuthorize(Constants.PlatFormId.PLATFORM_SOHU, LoginActivity.this);
            }
        });
        
        ssoLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo accountInfo = new AccountInfo();
                accountInfo.isv_refer_id = "10001";
                accountInfo.nickname = "畅言单点测试用户";
                sdk.setAccountInfo(accountInfo,new CallBack() {
                    
                    @Override
                    public void success() {
                        // token
                        Set<String> set = CyanSdk.getCookie();
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void error(CyanException e){
                        Toast.makeText(LoginActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
