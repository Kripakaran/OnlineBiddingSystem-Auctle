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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.activities.ConfigureActivity;
import io.picopalette.apps.auctle.activities.LoginActivity;
import io.picopalette.apps.auctle.activities.MainActivity;
import io.picopalette.apps.auctle.adapters.BidFeedAdapter;
import io.picopalette.apps.auctle.models.Bid;
import io.picopalette.apps.auctle.models.Bidding;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.Helpers;
import io.picopalette.apps.auctle.utilities.VolleySingleton;


public class ProfileFragment extends Fragment {

    ArrayList<Bidding> historyBiddings;
    BidFeedAdapter historyFeedAdapter;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyBiddings = new ArrayList<>();
        historyFeedAdapter = new BidFeedAdapter(historyBiddings, getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView nameTextView = (TextView) view.findViewById(R.id.profileNameTextView);
        final TextView emailTextView = (TextView) view.findViewById(R.id.profileEmailTextView);
        final TextView phoneTextView = (TextView) view.findViewById(R.id.profilePhoneTextView);
        final TextView addressTextView = (TextView) view.findViewById(R.id.profileAddressTextView);
        ImageView logoutImageView = (ImageView) view.findViewById(R.id.logoutImageView);
        RecyclerView historyRecyclerView = (RecyclerView) view.findViewById(R.id.profileHistoryRecyclerView);

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyRecyclerView.setAdapter(historyFeedAdapter);

        String url = "http://" + getActivity().getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE).getString(Constants.KEY_IP, "") + "/me";
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                        try {
                            nameTextView.setText(response.getString("name"));
                            emailTextView.setText(response.getString("email"));
                            phoneTextView.setText(response.getString("phone"));
                            addressTextView.setText(response.getString("address"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                Helpers.handleNetworkError(error, getActivity());
            }
        });
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(profileRequest);

        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://" + getActivity().getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE).getString(Constants.KEY_IP,"") + "/auth/logout";
                JsonObjectRequest logoutRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent i = getActivity().getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        getActivity().finish();
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Helpers.handleNetworkError(error, getActivity());
                    }
                });
                VolleySingleton.getInstance(getActivity()).getRequestQueue().add(logoutRequest);
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
        historyBiddings.clear();
        String liveUrl = "http://" + getActivity().getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE).getString(Constants.KEY_IP, "") + "/bidding/mybids";
        JsonArrayRequest feedRequest = new JsonArrayRequest(Request.Method.GET, liveUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("FeedResponse", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject biddingJson = response.getJSONObject(i);
                                Bidding bidding = new Gson().fromJson(biddingJson.toString(), Bidding.class);
                                historyBiddings.add(bidding);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        historyFeedAdapter.notifyDataSetChanged();
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
