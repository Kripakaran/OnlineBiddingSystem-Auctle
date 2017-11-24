package io.picopalette.apps.auctle.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.activities.LoginActivity;
import io.picopalette.apps.auctle.activities.MainActivity;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.VolleySingleton;

public class BuyFragment extends Fragment {

    public static BuyFragment newInstance() {
        return new BuyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_buy, container, false);

        final OngoingFragment ongoingFragment = new OngoingFragment();
        final UpcomingFragment upcomingFragment = new UpcomingFragment();

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.buyViewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return position==0 ? ongoingFragment : upcomingFragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position==0 ? "Ongoing" : "Upcoming";
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabL);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}
