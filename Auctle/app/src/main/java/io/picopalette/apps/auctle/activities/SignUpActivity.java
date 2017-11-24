package io.picopalette.apps.auctle.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.Helpers;
import io.picopalette.apps.auctle.utilities.VolleySingleton;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText nameEditText = (EditText) findViewById(R.id.nameSignupEditText);
        final EditText emailEditText = (EditText) findViewById(R.id.emailSignupEditText);
        final EditText phoneEditText = (EditText) findViewById(R.id.phoneSignupEditText);
        final EditText addressEditText = (EditText) findViewById(R.id.addressSignupEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordSignupEditText);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);

        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                JSONObject signUpData = new JSONObject();
                try {
                    signUpData.put("email", email);
                    signUpData.put("name", name);
                    signUpData.put("phone", phone);
                    signUpData.put("address", address);
                    signUpData.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = "http://" + sharedPreferences.getString(Constants.KEY_IP, "") + "/auth/register";

                JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, signUpData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject userObject = response.getJSONObject("saved");
                            Toast.makeText(getApplicationContext(), "Welcome " + userObject.getString("name"), Toast.LENGTH_LONG).show();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Helpers.handleNetworkError(error, SignUpActivity.this);
                    }
                });
                VolleySingleton.getInstance(SignUpActivity.this).getRequestQueue().add(loginRequest);
            }
        });

    }
}
