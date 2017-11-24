package io.picopalette.apps.auctle.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.fragments.BuyFragment;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.PersistentCookieStore;
import io.picopalette.apps.auctle.utilities.VolleySingleton;

public class ConfigureActivity extends AppCompatActivity {

    private ImageView send;
    private EditText ipET;
    private TextView ipTV;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ProgressDialog dialog;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        queue = VolleySingleton.getInstance(this).getRequestQueue();
        mSharedPreferences = getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE);
        proceed();
        send = (ImageView) findViewById(R.id.sendImage);
        ipET = (EditText) findViewById(R.id.ipEditText);
        ipTV = (TextView) findViewById(R.id.ipTextView);

        mEditor = mSharedPreferences.edit();

        ipTV.setText(mSharedPreferences.getString(Constants.KEY_IP,""));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip;
                ip = ipET.getText().toString();
                if(ip.equals(""))
                {
                    Toast.makeText(ConfigureActivity.this, "IP cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ipTV.setText(ip);
                    mEditor.putString(Constants.KEY_IP,ip);
                    ipET.setText("");
                    mEditor.apply();
                    proceed();
                }
            }
        });
    }

    private void proceed() {
        dialog = ProgressDialog.show(ConfigureActivity.this, "Connecting", "Please wait while we connect", true, false);
        String url = "http://" + mSharedPreferences.getString(Constants.KEY_IP, "") + "/";
        JsonObjectRequest checkRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        dialog.dismiss();
                        Log.d("Response", response.toString());
                        Intent intent = null;
                        try {
                            JSONObject resJson = new JSONObject(response.toString());
                            Toast.makeText(getApplicationContext(), "Logged in as " + resJson.getString("welcome"), Toast.LENGTH_LONG).show();
                            if(resJson.getString("welcome").matches("admin")) {
                                intent = new Intent(ConfigureActivity.this, AdminActivity.class);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(intent == null) {
                            intent = new Intent(ConfigureActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                dialog.dismiss();
                if(error.networkResponse == null) {
                    Toast.makeText(getApplicationContext(), "Check your IP", Toast.LENGTH_SHORT).show();
                } else if (error.networkResponse.statusCode == 401) {
                    try {
                        JSONObject response = new JSONObject(new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers)));
                        Log.d(String.valueOf(error.networkResponse.statusCode), response.getString("error"));
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } catch(UnsupportedEncodingException | JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        queue.add(checkRequest);
    }
}
