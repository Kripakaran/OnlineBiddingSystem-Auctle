package io.picopalette.apps.auctle.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.picopalette.apps.auctle.activities.NewSellActivity;
import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.adapters.BidFeedAdapter;
import io.picopalette.apps.auctle.models.Bidding;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.Helpers;
import io.picopalette.apps.auctle.utilities.VolleySingleton;


public class SellFragment extends Fragment {

    FloatingActionButton mSellFAB;
    BidFeedAdapter sellOngoingAdapter;
    BidFeedAdapter sellUpcomingAdapter;
    BidFeedAdapter sellHistoryAdapter;
    ArrayList<Bidding> sellOngoingBiddings;
    ArrayList<Bidding> sellUpcomingBiddings;
    ArrayList<Bidding> sellHistoryBiddings;
    TextView sellOngoingTextView;
    TextView sellUpcomingTextView;
    TextView sellHistoryTextView;

    public static SellFragment newInstance() {
        return new SellFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sellOngoingBiddings = new ArrayList<>();
        sellUpcomingBiddings = new ArrayList<>();
        sellHistoryBiddings = new ArrayList<>();
        sellOngoingAdapter = new BidFeedAdapter(sellOngoingBiddings, getActivity());
        sellUpcomingAdapter = new BidFeedAdapter(sellUpcomingBiddings, getActivity());
        sellHistoryAdapter = new BidFeedAdapter(sellHistoryBiddings, getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        sellOngoingTextView = (TextView) view.findViewById(R.id.sellOngoingTextView);
        RecyclerView sellOngoingRecyclerView = (RecyclerView) view.findViewById(R.id.sellOngoingRecyclerView);
        sellUpcomingTextView = (TextView) view.findViewById(R.id.sellUpcomingTextView);
        RecyclerView sellUpcomingRecyclerView = (RecyclerView) view.findViewById(R.id.sellUpcomingRecyclerView);
        sellHistoryTextView = (TextView) view.findViewById(R.id.sellHistoryTextView);
        RecyclerView sellHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.sellHistoryRecyclerView);

        sellOngoingRecyclerView.setAdapter(sellOngoingAdapter);
        sellOngoingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sellUpcomingRecyclerView.setAdapter(sellUpcomingAdapter);
        sellUpcomingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sellHistoryRecyclerView.setAdapter(sellHistoryAdapter);
        sellHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSellFAB = (FloatingActionButton) view.findViewById(R.id.sellFAB);

        mSellFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewSellActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchBids();
    }

    private void fetchBids() {
        sellOngoingBiddings.clear();
        sellUpcomingBiddings.clear();
        sellHistoryBiddings.clear();
        String domain = getActivity().getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE).getString(Constants.KEY_IP, "");
        String sellOngoingUrl = "http://" + domain + "/bidding/soldbids/live";
        String sellUpcomingUrl = "http://" + domain + "/bidding/soldbids/upcoming";
        final String sellHistoryUrl = "http://" + domain + "/bidding/soldbids/history";
        JsonArrayRequest sellOngoingFeedRequest = new JsonArrayRequest(Request.Method.GET, sellOngoingUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("FeedResponse", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject biddingJson = response.getJSONObject(i);
                                Bidding bidding = new Gson().fromJson(biddingJson.toString(), Bidding.class);
                                sellOngoingBiddings.add(bidding);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(sellOngoingBiddings.isEmpty()) {
                            sellOngoingTextView.setText("None of your bids are live!");
                        } else {
                            sellOngoingTextView.setText("Sale going on");
                        }
                        sellOngoingAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(Helpers.handleNetworkError(error, getActivity())) {
                    getActivity().finish();
                }
            }
        });
        JsonArrayRequest sellUpcomingFeedRequest = new JsonArrayRequest(Request.Method.GET, sellUpcomingUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("FeedResponse", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject biddingJson = response.getJSONObject(i);
                                Bidding bidding = new Gson().fromJson(biddingJson.toString(), Bidding.class);
                                sellUpcomingBiddings.add(bidding);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(sellUpcomingBiddings.isEmpty()) {
                            sellUpcomingTextView.setText("You don't have any upcoming bids to sell");
                        } else {
                            sellUpcomingTextView.setText("To be sold");
                        }
                        sellUpcomingAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(Helpers.handleNetworkError(error, getActivity())) {
                    getActivity().finish();
                }
            }
        });
        JsonArrayRequest sellHistoryFeedRequest = new JsonArrayRequest(Request.Method.GET, sellHistoryUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("FeedResponse", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject biddingJson = response.getJSONObject(i);
                                Bidding bidding = new Gson().fromJson(biddingJson.toString(), Bidding.class);
                                sellHistoryBiddings.add(bidding);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(sellHistoryBiddings.isEmpty()) {
                            sellHistoryTextView.setText("You haven't sold any bids");
                        } else {
                            sellHistoryTextView.setText("Sold");
                        }
                        sellHistoryAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(Helpers.handleNetworkError(error, getActivity())) {
                    getActivity().finish();
                }
            }
        });
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(sellOngoingFeedRequest);
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(sellUpcomingFeedRequest);
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(sellHistoryFeedRequest);
    }
}
