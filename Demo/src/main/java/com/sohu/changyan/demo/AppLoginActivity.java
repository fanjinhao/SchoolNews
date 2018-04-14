package com.sohu.changyan.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sohu.cyan.android.sdk.api.CallBack;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.entity.AccountInfo;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.sohu.cyan.android.sdk.util.StringUtil;

public class AppLoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.app_login_layout);
		Button appLoginBtn = (Button) this.findViewById(R.id.appLogin);
		
		appLoginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText nameTxt = (EditText) findViewById(R.id.appUserName);
				EditText pwdTxt = (EditText) findViewById(R.id.appPassword);
				
				String name = nameTxt.getText().toString();
				String pwd = pwdTxt.getText().toString();
				
				if(StringUtil.isNotBlank(name)&&StringUtil.isNotBlank(pwd)){
					AccountInfo account = new AccountInfo();
					//应用自己的用户id
					account.isv_refer_id = "10001";
					account.nickname = "testuser";
					account.img_url = "http://assets.changyan.sohu.com/upload/asset/scs/images/pic/pic42_null.gif";
					CyanSdk.getInstance(AppLoginActivity.this).setAccountInfo(account,new CallBack() {
                        
                        @Override
                        public void success() {
                            Toast.makeText(AppLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            AppLoginActivity.this.finish();
                        }
                        @Override
                        public void error(CyanException e){
                            Toast.makeText(AppLoginActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
                        }
                    });
				}
			}
		});
	}
}
