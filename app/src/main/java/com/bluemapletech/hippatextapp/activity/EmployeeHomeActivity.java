package com.bluemapletech.hippatextapp.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.ViewPageAdapter;
import com.bluemapletech.hippatextapp.widgets.GroupChatEmployeeTabActivity;
import com.bluemapletech.hippatextapp.widgets.InterChatEmployeeTabActivity;
import com.bluemapletech.hippatextapp.widgets.IntraChatEmployeeTabActivity;


public class EmployeeHomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);



        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());

        // Creating tabs
        viewPagerAdapter.addFragments(new InterChatEmployeeTabActivity(),"InterChat");
        viewPagerAdapter.addFragments(new IntraChatEmployeeTabActivity(),"IntraChat");
        viewPagerAdapter.addFragments(new GroupChatEmployeeTabActivity(),"Group");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
