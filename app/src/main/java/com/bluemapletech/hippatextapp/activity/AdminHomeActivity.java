package com.bluemapletech.hippatextapp.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.ViewPageAdapter;
import com.bluemapletech.hippatextapp.widgets.AcceptedAdminTabActivity;
import com.bluemapletech.hippatextapp.widgets.ChatAdminActivity;
import com.bluemapletech.hippatextapp.widgets.PendingAdminTabActivity;
import com.bluemapletech.hippatextapp.widgets.RequestedAdminTabActivity;

public class AdminHomeActivity extends AppCompatActivity {

    private static final String TAG = AdminHomeActivity.class.getCanonicalName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPagerAdapter;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());

        // Creating tabs
        viewPagerAdapter.addFragments(new AcceptedAdminTabActivity(),"Accepted");
        viewPagerAdapter.addFragments(new RequestedAdminTabActivity(),"Requested");
        viewPagerAdapter.addFragments(new PendingAdminTabActivity(),"Pending");
        viewPagerAdapter.addFragments(new ChatAdminActivity(),"Chat");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
