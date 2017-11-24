package io.picopalette.apps.auctle.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.Helpers;
import io.picopalette.apps.auctle.utilities.VolleySingleton;

public class NewSellActivity extends AppCompatActivity {

    private EditText nameEditText, categoryEditText, priceEditText, bidPriceEditText, dateEditText, timeEditText, descEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sell);
        nameEditText = (EditText) findViewById(R.id.productNameEditText);
        categoryEditText = (EditText) findViewById(R.id.productCategoryEditText);
        priceEditText = (EditText) findViewById(R.id.productPriceEditText);
        bidPriceEditText = (EditText) findViewById(R.id.bidPriceEditText);
        dateEditText = (EditText) findViewById(R.id.sellDateEditText);
        timeEditText = (EditText) findViewById(R.id.sellTimeEditText);
        descEditText = (EditText) findViewById(R.id.productDescEditText);
        Button createBtn = (Button) findViewById(R.id.newSellButton);

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateEditText.setText(String.format(Locale.ENGLISH, "%02d/%02d/%d", dayOfMonth, monthOfYear+1, year));
            }

        };
        final TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                timeEditText.setText(String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay, minute));
            }
        };
        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewSellActivity.this, dateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(NewSellActivity.this, timeListener, myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), false).show();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String name = nameEditText.getText().toString();
               String category = categoryEditText.getText().toString();
               String price = priceEditText.getText().toString();
               String bidPrice = bidPriceEditText.getText().toString();
               String date = dateEditText.getText().toString();
               String time = timeEditText.getText().toString();
               String desc = descEditText.getText().toString();
               Date startDate = new Date(myCalendar.get(Calendar.YEAR)-1900, myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH), myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE));
               if(name.matches("") || category.matches("") || price.matches("") || date.matches("") || time.matches("") || desc.matches("")) {
                   Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
               } else {
                   JSONObject requestData = new JSONObject();
                   JSONObject product = new JSONObject();
                   try {
                       requestData.put("startTime", Helpers.getDateFormatter().format(startDate));
                       requestData.put("startPrice", Integer.valueOf(bidPrice));
                       product.put("name", name);
                       product.put("desc", desc);
                       product.put("category", category);
                       product.put("price", Integer.valueOf(price));
                       requestData.put("product", product);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
                   JsonObjectRequest newBidRequest = new JsonObjectRequest(Request.Method.POST, "http://" + getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE).getString(Constants.KEY_IP, "") + "/bidding", requestData,
                           new Response.Listener<JSONObject>() {
                               @Override
                               public void onResponse(JSONObject response) {
                                    finish();
                               }
                           }, new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {
                           if(Helpers.handleNetworkError(error, NewSellActivity.this)) {
                               finish();
                           }
                       }
                   });
                   VolleySingleton.getInstance(NewSellActivity.this).getRequestQueue().add(newBidRequest);
               }
           }
       });

    }
}
