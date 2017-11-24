package io.picopalette.apps.auctle.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.activities.LoginActivity;
import io.picopalette.apps.auctle.activities.NewSellActivity;
import io.picopalette.apps.auctle.adapters.BidFeedAdapter;
import io.picopalette.apps.auctle.models.Bidding;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.Helpers;
import io.picopalette.apps.auctle.utilities.VolleySingleton;

public class UpcomingFragment extends Fragment {

    private ArrayList<Bidding> upcomingBiddings;
    private BidFeedAdapter bidFeedAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upcomingBiddings = new ArrayList<>();
        bidFeedAdapter = new BidFeedAdapter(upcomingBiddings, getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        RecyclerView feedRecyclerView = (RecyclerView) view.findViewById(R.id.upcomingRecyclerView);
        feedRecyclerView.setAdapter(bidFeedAdapter);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchBids();
    }

    private void fetchBids() {
        upcomingBiddings.clear();
        String upcomingUrl = "http://" + getActivity().getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE).getString(Constants.KEY_IP, "") + "/bidding/upcoming";
        JsonArrayRequest feedRequest = new JsonArrayRequest(Request.Method.GET, upcomingUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("FeedResponse", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject biddingJson = response.getJSONObject(i);
                                Bidding bidding = new Gson().fromJson(biddingJson.toString(), Bidding.class);
                                upcomingBiddings.add(bidding);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bidFeedAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(Helpers.handleNetworkError(error, getActivity())) {
                    getActivity().finish();
                }
            }
        });
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(feedRequest);
    }
}
