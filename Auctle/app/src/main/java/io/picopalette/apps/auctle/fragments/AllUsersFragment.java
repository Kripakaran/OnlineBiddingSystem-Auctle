package io.picopalette.apps.auctle.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.adapters.BidFeedAdapter;
import io.picopalette.apps.auctle.adapters.UsersAdapter;
import io.picopalette.apps.auctle.models.Bidding;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.Helpers;
import io.picopalette.apps.auctle.utilities.VolleySingleton;

/**
 * Created by ramkumar on 22/10/17.
 */

public class AllUsersFragment extends Fragment {
    private ArrayList<Pair<String, String>> allUsers;
    private UsersAdapter allusersAdapter;

    public static AllUsersFragment newInstance() {
        return new AllUsersFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allUsers = new ArrayList<>();
        allusersAdapter = new UsersAdapter(allUsers);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ongoing, container, false);
        RecyclerView feedRecyclerView = (RecyclerView) view.findViewById(R.id.ongoingRecyclerView);
        feedRecyclerView.setAdapter(allusersAdapter);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUsers();
    }

    private void fetchUsers() {
        allUsers.clear();
        String liveUrl = "http://" + getActivity().getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE).getString(Constants.KEY_IP, "") + "/auth/users";
        JsonArrayRequest feedRequest = new JsonArrayRequest(Request.Method.GET, liveUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("UsersResponse", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userJson = response.getJSONObject(i);
                                allUsers.add(new Pair<String, String>(userJson.getString("name"), userJson.getString("email")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        allusersAdapter.notifyDataSetChanged();
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
