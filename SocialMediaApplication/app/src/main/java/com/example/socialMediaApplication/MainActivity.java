package com.example.socialMediaApplication;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //loading the default fragment
        loadFragment(new DiscoveryFragment());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.page_1:
                fragment = new DiscoveryFragment();
                break;

            case R.id.page_2:
                fragment = new MessagesFragment();
                break;

            case R.id.page_3:
                fragment = new ProfileFragment();
                String sending_UID = getIntent().getStringExtra("sending_uid");
                Bundle bundle = new Bundle();
                bundle.putString("sending_uid",sending_UID);
                fragment.setArguments(bundle);
                break;
        }

        return loadFragment(fragment);
    }
}
