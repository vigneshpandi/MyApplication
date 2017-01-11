package com.bluemapletech.hippatextapp.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class Settings extends AppCompatActivity {
    private static final String TAG = Settings.class.getCanonicalName();
    private ListView iv;
    private String roleValue,isOnline;
    ArrayList<String> arrlist;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String emailValue;
    String role_Value,auth;
    private Switch mySwitch;
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private FirebaseDatabase firebaseDatabaseRef;
    public static final String roleValues = "roleValues";
    private String[] lv_arr = {"Profile","Change Secure Chat Pin","Change Password","Delete An Acount","Notification Settings"," Show Online"};
    private String[] iv_arr_root = {"Change Password","Delete An Acount","Notification Settings"};
    private String login_mail;
    Switch showOnline;
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
    private FirebaseDatabase fireBaseDatabase; private FirebaseAuth firebaseAuth;
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
        login_mail = pref.getString("loginMail","");
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref.getString("isOnline", "");

        TextView profile = (TextView) findViewById(R.id.profile);
        TextView chatPin = (TextView) findViewById(R.id.change_secure_chat_pin);
        TextView password = (TextView) findViewById(R.id.change_password);
        TextView deleteAcount = (TextView) findViewById(R.id.delete_an_account);
        TextView notificationSetting = (TextView) findViewById(R.id.notification_setting);
        showOnline = (Switch) findViewById(R.id.show_online);
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
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setMessage("Do you want to delete the account!");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteAcount();
                        }
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();

                }
            });
            notificationSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Empty coding....
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
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
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setMessage("Do you want to delete the account!");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteAcount();
                        }
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }
            });
            notificationSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Empty coding....
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
            });
            showOnline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"show online is clicked....");
                    //  showOnline.setChecked(true);
                    showOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if(isChecked){
                                //   switchStatus.setText("Switch is currently ON");
                                pref = getApplicationContext().getSharedPreferences("loginUserDetails", MODE_PRIVATE);
                                editor = pref.edit();
                                editor.putString("isOnline", "true");
                                editor.commit();
                                Log.d("checked","dfdjfkdlfkldfjkldjf"+login_mail);
                                User u = new User();
                                u.setUserName(login_mail);
                                u.setIsOnlie("true");

                                showOnline(u);
                            }else{
                                pref = getApplicationContext().getSharedPreferences("loginUserDetails", MODE_PRIVATE);
                                editor = pref.edit();
                                editor.putString("isOnline", "false");
                                editor.commit();
                                Log.d("not checked",login_mail);
                                User u1 = new User();
                                u1.setUserName(login_mail);
                                u1.setIsOnlie("false");
                                Log.d("nottchecked","dfdjfkdlfcccckldfjkldjf"+login_mail);
                                showOnline(u1);
                                //  switchStatus.setText("Switch is currently OFF");
                            }

                        }
                    });
                }
            });

        }
        if(isOnline.matches("true")){
            showOnline.setChecked(true);

        }else if(isOnline.matches("false")){
            showOnline.setChecked(false);

        }
    }

    /* if(isOnline.matches("true")) {*/
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
                    Toast.makeText(getActivity(),"Your acount is deleted successfully!",Toast.LENGTH_LONG).show();
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
    public void showOnline(User user) {
        final UserDao userDao = new UserDao();
        boolean result = userDao.isOnline(user);
        if (result) {
            Log.d(TAG,"user.getIsOnline..."+user.getIsOnlie());
            if(user.getIsOnlie()== "true") {
                // showOnline.setChecked(true);
                Toast.makeText(getActivity(), "show online is  enabled!", Toast.LENGTH_LONG).show();
            }else{
                // showOnline.setChecked(false);
                Toast.makeText(getActivity(), "show online is  disabled!", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Error while show Online, please try again!");
        }
    }

    @Override
    public void onPause()
    {
        pref1 = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref1.getString("isOnline", "");
        if(isOnline.matches("true")) {
            fireBaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser logged = firebaseAuth.getCurrentUser();
            String reArrangeEmail = logged.getEmail().replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            dataReferences.removeValue();
        }
        super.onPause();
        //Do whatever you want to do when the application stops.
    }


    @Override
    protected  void onResume(){
        pref1 = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref1.getString("isOnline", "");
        if(isOnline.matches("true")) {
            HashMap<String, Object> onlineReenter = new HashMap<>();
            fireBaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser logged = firebaseAuth.getCurrentUser();
            String reArrangeEmail = logged.getEmail().replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            onlineReenter.put("onlineUser", logged.getEmail());
            dataReferences.setValue(onlineReenter);
        }
        super.onResume();
    }


    public Settings getActivity() {
        return this;
    }
}
