package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
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
import com.bluemapletech.hippatextapp.widgets.AcceptedTabActivity;
import com.bluemapletech.hippatextapp.widgets.PendingTabActivity;
import com.bluemapletech.hippatextapp.widgets.RequestedTabActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RootHomeActivity extends AppCompatActivity {
    private String loginMail;
    private static final String TAG = RootHomeActivity.class.getCanonicalName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPagerAdapter;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    public static final String rootValue = "rootValue";
    public static final String role = "role";
    public static final String NotAcceptUser = "NotAcceptUser";
    private String AcceptedUser = "AcceptUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_home);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_header);
        setSupportActionBar(toolbar);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.root_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("menu selected","menu selected");
        //noinspection SimplifiableIfStatement

        if (id == R.id.log_out) {

            SharedPreferences preferences = getSharedPreferences("myBackgroundImage", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            Calendar c = Calendar.getInstance();
            String myFormat = "yyyy-MM-dd HH:mm:ss Z";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String dateValue = sdf.format(c.getTime());
            pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
            loginMail =  pref.getString("loginMail", "");

            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            String reArrangeEmail = loginMail.replace(".", "-");
            DatabaseReference dataReference = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail).child("updatedDate");
            dataReference.setValue(dateValue);


            SharedPreferences preferencess = getSharedPreferences("loginUserDetails", 0);
            SharedPreferences.Editor editors = preferencess.edit();
            editors.clear();
            editors.commit();

            Intent logOut = new Intent(getActivity(), HomeActivity.class);
            startActivity(logOut);
            onStop();
            finish();
            return true;
        }
        if (id == R.id.add_root) {
            Intent add = new Intent(getActivity(), AddRootActivity.class);
            startActivity(add);
            return true;
        }
        if (id == R.id.list_of_root) {
            Intent list = new Intent(getActivity(), ListOfRoots.class);
            String rootVal = "1";
            String roleVal = "root";
            list.putExtra(rootValue,rootVal);
            list.putExtra(role,roleVal);
            list.putExtra(NotAcceptUser,AcceptedUser);
            startActivity(list);
            return true;
        }
        if (id == R.id.rejected_root) {
            Intent list = new Intent(getActivity(), ListOfRoots.class);
            String rootVal = "3";
            String roleVal = "root";
            list.putExtra(rootValue,rootVal);
            list.putExtra(role,roleVal);
            list.putExtra(NotAcceptUser,AcceptedUser);
            startActivity(list);
            return true;
        }
        if (id == R.id.rejected_company) {
            Intent list = new Intent(getActivity(), ListOfRoots.class);
            String rootVal = "3";
            String roleVal = "admin";
            list.putExtra(NotAcceptUser,AcceptedUser);
            list.putExtra(rootValue,rootVal);
            list.putExtra(role,roleVal);
            startActivity(list);
            return true;
        }
        if (id == R.id.settings) {
            Intent list = new Intent(getActivity(), Settings.class);
            String roleVal = "root";
            list.putExtra(role,roleVal);
            startActivity(list);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public RootHomeActivity getActivity() {
        return this;
    }
}
