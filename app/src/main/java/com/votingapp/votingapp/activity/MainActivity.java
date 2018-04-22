package com.votingapp.votingapp.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bigscreen.iconictabbar.view.IconicTab;
import com.bigscreen.iconictabbar.view.IconicTabBar;
import com.votingapp.votingapp.R;
import com.votingapp.votingapp.fragment.PollFragment;
import com.votingapp.votingapp.fragment.ProfileFragment;
import com.votingapp.votingapp.manager.ServerManager;
import com.votingapp.votingapp.model.Poll;
import com.votingapp.votingapp.model.PollCollection;
import com.votingapp.votingapp.server.ProxyBuilder;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final String POLLS_TAG = "Poll_Tag";
    private final String PROFILE_TAG = "Profile_Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupServerManagerProxy();

        setDefaultFragment();

        setup();
    }

    private void setDefaultFragment() {
        displayFragment(new PollFragment(), POLLS_TAG);
    }

    private void setupServerManagerProxy() {
        ServerManager.setProxy(ProxyBuilder.getProxy());
    }

    private void setup() {
        IconicTabBar iconicTabBar = findViewById(R.id.tab_bar);

        iconicTabBar.setOnTabSelectedListener(new IconicTabBar.OnTabSelectedListener() {
            @Override
            public void onSelected(IconicTab tab, int position) {
                Log.d(TAG, "selected tab on= " + position);
                int tabId = tab.getId();
                switch (tabId) {
                    case R.id.polls_view:
                        displayFragment(new PollFragment(), POLLS_TAG);
                        break;
                    case R.id.profile_view:
                        displayFragment(new ProfileFragment(), PROFILE_TAG);
                        break;
                    default:
                        throw new IllegalArgumentException("Illegal ID was selected");
                }
            }

            @Override
            public void onUnselected(IconicTab tab, int position) {
                Log.d(TAG, "unselected tab on= " + position);
            }
        });
    }

    private void displayFragment(Fragment fragmentClass, String tag ) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(tag);

        if (fragment == null) {
            transaction.replace(R.id.fragment_container, fragmentClass, tag);
        }
        else {
            transaction.replace(R.id.fragment_container, fragment);
        }

        transaction.commit();
    }
}
