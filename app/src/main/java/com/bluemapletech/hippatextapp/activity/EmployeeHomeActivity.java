package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.ViewPageAdapter;
import com.bluemapletech.hippatextapp.widgets.GroupChatEmployeeTabActivity;
import com.bluemapletech.hippatextapp.widgets.InterChatEmployeeTabActivity;
import com.bluemapletech.hippatextapp.widgets.IntraChatEmployeeTabActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class EmployeeHomeActivity extends AppCompatActivity {
   private String loginMail;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPagerAdapter;
    private FirebaseAuth firebaseAuth;
    SharedPreferences pref;
    private FirebaseDatabase fireBaseDatabase;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);

       Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_header);
        setSupportActionBar(toolbar);

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

    @Override
    protected void onDestroy() {
        Log.d("emphomeActvity","Destroy");
        SharedPreferences preferences = getSharedPreferences("myBackgroundImage", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        super.onDestroy();
    }
    @Override
    public void onPause()
    {
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        String reArrangeEmail =  logged.getEmail().replace(".", "-");
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
        dataReferences.removeValue();
        super.onPause();
        //Do whatever you want to do when the application stops.
    }


    @Override
    protected  void onResume(){

        HashMap<String, Object> onlineReenter = new HashMap<>();
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        String reArrangeEmail =  logged.getEmail().replace(".", "-");
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
        onlineReenter.put("onlineUser",logged.getEmail());
        dataReferences.setValue(onlineReenter);
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_group) {
            Intent rootHome = new Intent(getActivity(), CreateGroup.class);
            startActivity(rootHome);
            return true;
        }
        if (id == R.id.settings) {
            Intent settings = new Intent(getActivity(), Settings.class);
            startActivity(settings);
            return true;
        }
        if (id == R.id.log_out) {
            Intent logOut = new Intent(getActivity(), HomeActivity.class);
            startActivity(logOut);
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

            DatabaseReference dataReference1= mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail).child("pushNotificationId");
            dataReference1.setValue("");

            DatabaseReference dataReference2= mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            dataReference2.removeValue();

            SharedPreferences preferencess = getSharedPreferences("loginUserDetails", 0);
            SharedPreferences.Editor editors = preferencess.edit();
            editors.clear();
            editors.commit();

                onStop();
                finish();
            return true;
        }
        /*if (id == R.id.profile) {
            Intent logOut = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(logOut);
            onStop();
            finish();
            return true;
        }
        if (id == R.id.change_pin) {
            Intent logOut = new Intent(getActivity(), ChangeSecureChatPinActivity.class);
            startActivity(logOut);
            onStop();
            finish();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

   /*private boolean clearPusNotificationId() {
        Log.d("log","logoout method called");
       boolean successvalue = false;
        Intent logOut = new Intent(getActivity(), HomeActivity.class);
        startActivity(logOut);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        String reArrangeEmail = logged.getEmail().replace(".", "-");
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail).child("pushNotificationId");
       Task<Void> success = dataReferences.setValue("111");
       if(success.isSuccessful()){
           successvalue = true;
       }
        return successvalue;
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Log.d("sdsdsdsd","sdsdsdsdsd");
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public EmployeeHomeActivity getActivity() {
        return this;
    }
}
