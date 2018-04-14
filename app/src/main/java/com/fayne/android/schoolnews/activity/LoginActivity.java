package com.fayne.android.schoolnews.activity;

import android.content.Intent;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;

import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fayne.android.schoolnews.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import java.util.Map;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private Button mLoginButton;
    private Button mRegisterButton;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: ");
        initView();
        //if logged success
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String name = pref.getString("user", "null");
        if (!name.equals("null")) {
            startActivity(new Intent(LoginActivity.this, SliderBarActivity.class));
            LoginActivity.this.finish();
        }
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginHandle().run();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LoginActivity.this.finish();
        }
        return true;
    }

    class LoginHandle implements Runnable {

        @Override
        public void run() {
            final String username = mUsernameView.getText().toString();
            final String password = mPasswordView.getText().toString();
            String connectUrl = "http://project.fayne.cn/login.php";
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    int retCode = 0;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        retCode = jsonObject.getInt("success");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (retCode == 1) {
                        saveUser();

                        startActivity(new Intent(LoginActivity.this, SliderBarActivity.class));
                        LoginActivity.this.finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        mPasswordView.setText("");
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: error", error);
                }
            };

            StringRequest stringRequest = new StringRequest(Request.Method.POST, connectUrl, listener, errorListener) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("username", username);
                    map.put("password", password);
                    return map;
                }
            };
            requestQueue.add(stringRequest);

        }
    }

    private void saveUser() {
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("user", mUsernameView.getText().toString());
        editor.commit();
    }

    private void initView() {
        mUsernameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.sign_in_button);
        mRegisterButton = findViewById(R.id.register_button);
    }

}

