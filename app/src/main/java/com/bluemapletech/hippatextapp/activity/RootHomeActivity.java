package com.bluemapletech.hippatextapp.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.ViewPageAdapter;
import com.bluemapletech.hippatextapp.widgets.AcceptedTabActivity;
import com.bluemapletech.hippatextapp.widgets.PendingTabActivity;
import com.bluemapletech.hippatextapp.widgets.RequestedTabActivity;

public class RootHomeActivity extends AppCompatActivity {

    private static final String TAG = RootHomeActivity.class.getCanonicalName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPagerAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_root_home);


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());

        // Creating tabs
        viewPagerAdapter.addFragments(new AcceptedTabActivity(),"Accepted");
        viewPagerAdapter.addFragments(new RequestedTabActivity(),"Requested");
        viewPagerAdapter.addFragments(new PendingTabActivity(),"Pending");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
