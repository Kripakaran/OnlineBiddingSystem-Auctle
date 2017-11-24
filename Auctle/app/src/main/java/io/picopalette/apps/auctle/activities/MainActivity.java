package io.picopalette.apps.auctle.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.picopalette.apps.auctle.fragments.BuyFragment;
import io.picopalette.apps.auctle.fragments.ProfileFragment;
import io.picopalette.apps.auctle.fragments.SellFragment;
import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.utilities.VolleySingleton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private boolean isInBuy = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, BuyFragment.newInstance());
        transaction.commit();
        bottomNavigationView.setSelectedItemId(R.id.navigation_buy);


        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        getSupportActionBar().show();
                        switch (item.getItemId()) {
                            case R.id.navigation_buy:
                                selectedFragment = BuyFragment.newInstance();
                                isInBuy = true;
                                break;
                            case R.id.navigation_sell:
                                selectedFragment = SellFragment.newInstance();
                                isInBuy = false;
                                break;
                            case R.id.navigation_profile:
                                getSupportActionBar().hide();
                                selectedFragment = ProfileFragment.newInstance();
                                isInBuy = false;
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, selectedFragment);
                        transaction.commit();
                        return true;
                    }

                });


    }

    @Override
    public void onBackPressed() {
        if (isInBuy)
            super.onBackPressed();
        else {
            getSupportActionBar().show();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, BuyFragment.newInstance());
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_buy);
            isInBuy = true;
        }
    }

}
