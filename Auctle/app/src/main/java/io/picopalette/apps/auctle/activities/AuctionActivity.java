
package io.picopalette.apps.auctle.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.adapters.LiveBidderAdapter;
import io.picopalette.apps.auctle.models.Bid;
import io.picopalette.apps.auctle.models.Bidding;
import io.picopalette.apps.auctle.utilities.Constants;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AuctionActivity extends AppCompatActivity {

    private Bid topBid;
    private LiveBidderAdapter liveBidderAdapter;
    private SharedPreferences sharedPreferences;
    private Socket socket;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction);

        sharedPreferences = getSharedPreferences(Constants.PRES_FILE, MODE_PRIVATE);
        final Bidding bidding = (Bidding) getIntent().getSerializableExtra("bidding");

        TextView productTextView = (TextView) findViewById(R.id.auctionProductTextView);
        final TextView soldToTextView = (TextView) findViewById(R.id.auctionSoldToTextView);
        final TextView soldForTextView = (TextView) findViewById(R.id.auctionSoldForTextView);
        RecyclerView bidsRecyclerView = (RecyclerView) findViewById(R.id.bidsRecyclerView);
        final EditText newBidEditText = (EditText) findViewById(R.id.newBidEditText);
        final CardView newBidCard = (CardView) findViewById(R.id.newBidCard);
        ImageView newBidSubmitButton = (ImageView) findViewById(R.id.newBidSubmitButton);


        productTextView.setText(bidding.getProduct().getName());
        soldForTextView.setText(String.format(Locale.ENGLISH, "Rs.%d", bidding.getBidSoldFor()));
        if(bidding.getBidSoldTo() == null) {
            soldToTextView.setText("No bids yet");
        } else {
            soldToTextView.setText(bidding.getBidSoldTo());
            Collections.reverse(bidding.getBids());
            topBid = bidding.getBids().get(0);
            bidding.getBids().remove(0);
        }

        if(bidding.getStatus().matches("live")) {
            newBidCard.setVisibility(View.VISIBLE);
        }

        liveBidderAdapter = new LiveBidderAdapter(bidding.getBids());

        newBidSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newBidEditText.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer bidValue = Integer.valueOf(newBidEditText.getText().toString());
                String userEmail = sharedPreferences.getString(Constants.USER_EMAIL, "");
                JSONObject bidRequest = new JSONObject();
                try {
                    bidRequest.put("bidder", userEmail);
                    bidRequest.put("price", bidValue);
                    if(socket.connected()) {
                        socket.emit("bid", bidRequest);
                    } else {
                        Toast.makeText(getApplicationContext(), "Not connected", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        bidsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bidsRecyclerView.setAdapter(liveBidderAdapter);

        if(bidding.getStatus().matches("live")) {
            try {
                socket = IO.socket("http://" + sharedPreferences.getString(Constants.KEY_IP, "") + "?biddingId=" + bidding.getId());
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d("Socket", "Connected");
                    }
                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d("Socket", "Disconnected");
                    }
                }).on("err", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final String msg = (String) args[0];
                        AuctionActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                if(msg.matches("Bidding Ended")) {
                                    newBidCard.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }).on("ack", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final String msg = (String) args[0];
                        AuctionActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).on("update", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject bidJson = (JSONObject) args[0];
                        final Bid bid = new Gson().fromJson(bidJson.toString(), Bid.class);
                        AuctionActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(topBid != null) {
                                    liveBidderAdapter.addBid(topBid);
                                }
                                soldForTextView.setText(String.format(Locale.ENGLISH, "Rs.%d", bid.getPrice()));
                                soldToTextView.setText(bid.getBidder());
                                topBid = bid;
                            }
                        });
                    }
                });
                socket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }
}
