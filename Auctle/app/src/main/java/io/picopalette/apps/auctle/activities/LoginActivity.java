package io.picopalette.apps.auctle.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.picopalette.apps.auctle.utilities.*;
import io.picopalette.apps.auctle.R;

public class LoginActivity extends AppCompatActivity {


    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private EditText mUsernameET;
    private EditText mPasswordET;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = VolleySingleton.getInstance(this).getRequestQueue();

        mUsernameET = (EditText) findViewById(R.id.usernameEditText);
        mPasswordET = (EditText) findViewById(R.id.passwordEditText);
        Button mLoginButton = (Button) findViewById(R.id.loginButton);
        TextView signUpTextView = (TextView) findViewById(R.id.signupTextView);

        mSharedPreferences = getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user = mUsernameET.getText().toString();
                String userPass = mPasswordET.getText().toString();

                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("email", user);
                    loginData.put("password", userPass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url;
                if (user.matches("admin")) {
                    //Do admin login
                    url = "http://" + mSharedPreferences.getString(Constants.KEY_IP, "") + "/auth/admin";
                } else {
                    url = "http://" + mSharedPreferences.getString(Constants.KEY_IP, "") + "/auth/login";
                }
                JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, loginData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("success").matches("authenticated")) {
                                mEditor.putString(Constants.USER_EMAIL, user);
                                mEditor.apply();
                                Intent intent;
                                if (user.matches("admin")) {
                                    intent = new Intent(LoginActivity.this, AdminActivity.class);
                                } else {
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                }
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError || error.networkResponse == null) {
                            Toast.makeText(getApplicationContext(), "can't connect to server", Toast.LENGTH_SHORT).show();
                        } else if (error.networkResponse.statusCode == 403) {
                            try {
                                JSONObject response = new JSONObject(new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers)));
                                Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                queue.add(loginRequest);
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

}
