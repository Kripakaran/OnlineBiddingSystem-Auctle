package io.picopalette.apps.auctle.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.models.Bidding;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.Helpers;
import io.picopalette.apps.auctle.utilities.VolleySingleton;

public class SellDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_display);
        getSupportActionBar().hide();

        final Bidding bidding = (Bidding) getIntent().getSerializableExtra("bidding");

        final Toolbar nameToolbar = (Toolbar) findViewById(R.id.nameToolBar);
        TextView categoryTextView = (TextView) findViewById(R.id.productCategoryTV);
        TextView descTextView = (TextView) findViewById(R.id.productDescTV);
        TextView bidPriceTextView = (TextView) findViewById(R.id.bidPriceTV);
        TextView dateTextView = (TextView) findViewById(R.id.bidDateTV);
        TextView timeTextView = (TextView) findViewById(R.id.bidTimeTV);
        TextView sellerTextView = (TextView) findViewById(R.id.bidSellerTV);
        TextView statusTextView = (TextView) findViewById(R.id.bidStatusTV);
        TextView priceTextView = (TextView) findViewById(R.id.productPriceTV);
        Button enterAuctionButton = (Button) findViewById(R.id.enterAuctionButton);
        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE);

        nameToolbar.setTitle(bidding.getProduct().getName());
        categoryTextView.setText(bidding.getProduct().getCategory());
        descTextView.setText(bidding.getProduct().getDesc());
        bidPriceTextView.setText(String.format(Locale.ENGLISH, "Rs. %d", bidding.getBidSoldFor()));

        final String seller;
        if(bidding.getSellerId().matches(sharedPreferences.getString(Constants.USER_EMAIL, ""))) {
            seller = "you";
        } else {
            seller = bidding.getSellerId();
        }

        sellerTextView.setText(String.format("sold by %s", seller));
        try {
            Date date = Helpers.getDateFormatter().parse(bidding.getStartTime());
            dateTextView.setText(String.format(Locale.ENGLISH, "%02d/%02d/%d", date.getDate(), date.getMonth()+1, date.getYear()+1900));
            timeTextView.setText(String.format(Locale.ENGLISH, "%02d:%02d", date.getHours(), date.getMinutes()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        statusTextView.setText(bidding.getStatus());
        priceTextView.setText(String.format(Locale.ENGLISH, "Actual Price: Rs.%d", bidding.getProduct().getPrice()));

        if(bidding.getStatus().matches("live")) {
            enterAuctionButton.setVisibility(View.VISIBLE);
            enterAuctionButton.setText("Enter Auction");
        } else if (bidding.getStatus().matches("ended")) {
            enterAuctionButton.setVisibility(View.VISIBLE);
            enterAuctionButton.setText("View Bids");
        } else if (seller.matches("you")) {
            enterAuctionButton.setText("Delete Auction");
            enterAuctionButton.setVisibility(View.VISIBLE);
        }
        if(sharedPreferences.getString(Constants.USER_EMAIL, "").matches("admin")) {
            enterAuctionButton.setText("Delete Auction");
            enterAuctionButton.setVisibility(View.VISIBLE);
        }

        enterAuctionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((seller.matches("you") && bidding.getStatus().matches("upcoming")) || sharedPreferences.getString(Constants.USER_EMAIL, "").matches("admin")) {
                    String deleteUrl = "http://" + sharedPreferences.getString(Constants.KEY_IP, "") + "/bidding/bid/" + bidding.getId();
                    JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, deleteUrl, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Toast.makeText(getApplicationContext(), response.getString("success"), Toast.LENGTH_SHORT).show();
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Helpers.handleNetworkError(error, SellDisplayActivity.this);
                        }
                    });
                    VolleySingleton.getInstance(SellDisplayActivity.this).getRequestQueue().add(deleteRequest);
                } else {
                    Intent intent = new Intent(SellDisplayActivity.this, AuctionActivity.class);
                    intent.putExtra("bidding", bidding);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
