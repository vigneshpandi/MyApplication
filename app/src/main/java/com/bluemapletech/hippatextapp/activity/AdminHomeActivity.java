package com.bluemapletech.hippatextapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.ViewPageAdapter;
import com.bluemapletech.hippatextapp.widgets.AcceptedAdminTabActivity;
import com.bluemapletech.hippatextapp.widgets.ChatAdminActivity;
import com.bluemapletech.hippatextapp.widgets.GroupChatEmployeeTabActivity;
import com.bluemapletech.hippatextapp.widgets.PendingAdminTabActivity;
import com.bluemapletech.hippatextapp.widgets.RequestedAdminTabActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminHomeActivity extends AppCompatActivity {

    private static final String TAG = AdminHomeActivity.class.getCanonicalName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPagerAdapter;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());

        // Creating tabs
        viewPagerAdapter.addFragments(new AcceptedAdminTabActivity(),"Accepted");
        viewPagerAdapter.addFragments(new RequestedAdminTabActivity(),"Requested");
        viewPagerAdapter.addFragments(new PendingAdminTabActivity(),"Pending");
        viewPagerAdapter.addFragments(new ChatAdminActivity(),"Chat");
        viewPagerAdapter.addFragments(new GroupChatEmployeeTabActivity(),"Group");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("menu selected", "menu selected");
        //noinspection SimplifiableIfStatement
        if (id == R.id.new_group) {
            Intent redirect = new Intent(getActivity(), CreateGroup.class);
            startActivity(redirect);
            return true;
        }

        if (id == R.id.add_admin) {
            Intent redirect = new Intent(getActivity(), AddAdminActivity.class);
            startActivity(redirect);
            return true;
        }
        if (id == R.id.admin_list) {
            Intent redirect = new Intent(getActivity(), ListOfAdminActivity.class);
            startActivity(redirect);
            return true;
        }
        if (id == R.id.re_admin_list) {
            Intent redirect = new Intent(getActivity(), RejectedAdminList.class);
            startActivity(redirect);
            return true;
        }
        if (id == R.id.add_employee) {
            Intent redirect = new Intent(getActivity(), AddEmployeeActivity.class);
            startActivity(redirect);
            return true;
        }

        if (id == R.id.rejected_employee) {
            Intent redirect = new Intent(getActivity(), RejectedEmployeeListActivity.class);
            startActivity(redirect);
            return true;
        }
        if (id == R.id.settings) {
            Intent redirect = new Intent(getActivity(), Settings.class);
            startActivity(redirect);
            return true;
        }


        if (id == R.id.log_out) {
            Intent logOut = new Intent(getActivity(), HomeActivity.class);
            startActivity(logOut);
            SharedPreferences preferences = getSharedPreferences("myBackgroundImage", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            onStop();
            finish();
            return true;
        }

       /* if (id == R.id.profile) {
            Intent logOut = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(logOut);
            onStop();
            finish();
            Log.d("menu selected", "menu New group selected");
            return true;
        }*/
       /* if (id == R.id.change_pin) {
            Intent logOut = new Intent(getActivity(), ChangeSecureChatPinActivity.class);
            startActivity(logOut);
            onStop();
            finish();
            Log.d(TAG, "Change chat pin has called!");
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public AdminHomeActivity getActivity() {
        return this;
    }
}
