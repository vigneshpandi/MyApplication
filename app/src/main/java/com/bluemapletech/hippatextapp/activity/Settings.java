package com.bluemapletech.hippatextapp.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.bluemapletech.hippatextapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    private static final String TAG = Settings.class.getCanonicalName();
    private ListView iv;
    private String roleValue;
    ArrayList<String> arrlist;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String emailValue;
    String role_Value,auth;
    private Switch mySwitch;
    private FirebaseDatabase firebaseDatabaseRef;
    public static final String roleValues = "roleValues";
    private String[] lv_arr = {"Profile","Change Secure Chat Pin","Change Password","Delete An Acount","Notification Settings"," Show Online"};
    private String[] iv_arr_root = {"Change Password","Delete An Acount","Notification Settings"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        roleValue = getIntent().getStringExtra(RootHomeActivity.role);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        role_Value =  pref.getString("role", "");
        auth = pref.getString("auth","");

        TextView profile = (TextView) findViewById(R.id.profile);
        TextView chatPin = (TextView) findViewById(R.id.change_secure_chat_pin);
        TextView password = (TextView) findViewById(R.id.change_password);
        TextView deleteAcount = (TextView) findViewById(R.id.delete_an_account);
        TextView notificationSetting = (TextView) findViewById(R.id.notification_setting);
        final Switch showOnline = (Switch) findViewById(R.id.show_online);
        TableRow hr = (TableRow) findViewById(R.id.hr);
        TableRow hr1 = (TableRow) findViewById(R.id.hr1);
        TableRow hr2 = (TableRow) findViewById(R.id.hr2);
        TableRow hr3 = (TableRow) findViewById(R.id.hr3);
        TableRow hr4 = (TableRow) findViewById(R.id.hr4);
        TableRow hr5 = (TableRow) findViewById(R.id.hr5);

        if(role_Value.matches("root")){
             profile.setVisibility(View.GONE);
             chatPin.setVisibility(View.GONE);
             password.setVisibility(View.VISIBLE);
             deleteAcount.setVisibility(View.VISIBLE);
             notificationSetting.setVisibility(View.VISIBLE);
             showOnline.setVisibility(View.GONE);
             hr.setVisibility(View.GONE);
             hr1.setVisibility(View.GONE);
             hr2.setVisibility(View.VISIBLE);
             hr3.setVisibility(View.VISIBLE);
             hr4.setVisibility(View.VISIBLE);
             hr5.setVisibility(View.GONE);
            password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent changePassword = new Intent(getActivity(), ChangePassword.class);
                    changePassword.putExtra(roleValues,roleValue);
                    startActivity(changePassword);
                }
            });
            deleteAcount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAcount();
                }
            });
            notificationSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Empty coding....
                }
            });

        }else  if(role_Value.matches("admin") || role_Value.matches("user")){

            profile.setVisibility(View.VISIBLE);
            chatPin.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            deleteAcount.setVisibility(View.VISIBLE);
            notificationSetting.setVisibility(View.VISIBLE);
            showOnline.setVisibility(View.VISIBLE);
            hr.setVisibility(View.VISIBLE);
            hr1.setVisibility(View.VISIBLE);
            hr2.setVisibility(View.VISIBLE);
            hr3.setVisibility(View.VISIBLE);
            hr4.setVisibility(View.VISIBLE);
            hr5.setVisibility(View.VISIBLE);
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent editProfile = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(editProfile);
                }
            });
            chatPin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent redirect = new Intent(getActivity(), ChangeSecureChatPinActivity.class);
                    startActivity(redirect);
                }
            });
            password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent changePassword = new Intent(getActivity(), ChangePassword.class);
                    startActivity(changePassword);
                }
            });
            deleteAcount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAcount();
                }
            });
            notificationSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Empty coding....
                }
            });
            showOnline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOnline.setChecked(true);
                    showOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if(isChecked){
                                //   switchStatus.setText("Switch is currently ON");
                            }else{
                                //  switchStatus.setText("Switch is currently OFF");
                            }

                        }
                    });
                }
            });

        }



    }

    public void deleteAcount(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG,"deleted userdetails...."+user.getEmail());
        emailValue = user.getEmail().replace(".","-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User account deleted.");

                    DatabaseReference dataReference2= firebaseDatabaseRef.getReference().child("onlineUser").child(emailValue);
                    dataReference2.removeValue();

                    DatabaseReference databaseRefs = firebaseDatabaseRef.getReference().child("userDetails").child(emailValue);
                    databaseRefs.removeValue();
                    DatabaseReference databaseRefse = firebaseDatabaseRef.getReference().child("group").child(emailValue);
                    databaseRefse.removeValue();
                    SharedPreferences preferencess = getSharedPreferences("loginUserDetails", 0);
                    SharedPreferences.Editor editors = preferencess.edit();
                    editors.clear();
                    editors.commit();
                    Intent list = new Intent(getActivity(), HomeActivity.class);
                    startActivity(list);
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(role_Value.matches("root") && auth.matches("1")){
                    startActivity(new Intent(getActivity(),RootHomeActivity.class));
                }else
                if(role_Value.matches("admin") && auth.matches("1")){
                    startActivity(new Intent(getActivity(),AdminHomeActivity.class));
                }else
                if(role_Value.matches("user") && auth.matches("1")){
                    startActivity(new Intent(getActivity(),EmployeeHomeActivity.class));
                }else{
                    Intent user = new Intent(getActivity(),NotAcceptedUser.class);
                    startActivity(user);
                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public Settings getActivity() {
        return this;
    }
}
