package com.fayne.android.schoolnews.activity;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
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

public class RegisterActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private EditText mPasswordAgainView;
    private Button mRegisterButton;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    new RegisterHandle().run();
                } else {
                    Toast.makeText(RegisterActivity.this, "input error, please check", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    class RegisterHandle implements Runnable {

        @Override
        public void run() {
            final String username = mUserNameView.getText().toString();
            final String password = mPasswordView.getText().toString();
            String connectUrl = "http://project.fayne.cn/register.php";
            RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);

            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    int retCode = 0;

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        retCode = jsonObject.getInt("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (retCode == 1) {
                        Toast.makeText(RegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Register error", Toast.LENGTH_SHORT).show();
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
                    map.put("username", username);
                    map.put("password", password);
                    return map;
                }
            };

            requestQueue.add(request);
        }
    }

    private boolean check() {
        return mUserNameView.getText().toString() != "" && mPasswordAgainView.getText().toString().equals(mPasswordView.getText().toString()) && mPasswordAgainView.getText().toString() != "";
    }

    private void initView() {
        mUserNameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        mRegisterButton = findViewById(R.id.register_button);
        mPasswordAgainView = findViewById(R.id.password_again);
    }
}

