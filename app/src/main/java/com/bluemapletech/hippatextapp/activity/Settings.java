package com.bluemapletech.hippatextapp.activity;

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
import android.widget.ListView;

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
    private FirebaseDatabase firebaseDatabaseRef;
    public static final String roleValues = "roleValues";
    private String[] lv_arr = {"Profile","Change Secure Chat Pin","Change Password","Delete An Acount","Notification Settings"};
    private String[] iv_arr_root = {"Change Password","Delete An Acount","Notification Settings"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        iv = (ListView) findViewById(R.id.list_view);
        roleValue = getIntent().getStringExtra(RootHomeActivity.role);
        //roleValue = getIntent().getStringExtra(ChangePassword.roleValuesReturn);

        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        role_Value =  pref.getString("role", "");
        auth = pref.getString("auth","");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(role_Value.matches("root")) {
            iv.setAdapter(new ArrayAdapter<String>(Settings.this,
                    android.R.layout.simple_list_item_1, iv_arr_root));
        }else{
            iv.setAdapter(new ArrayAdapter<String>(Settings.this,
                    android.R.layout.simple_list_item_1, lv_arr));
        }
        iv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Settings","profilesSettings"+position);
                int positionValue = position;
                if(positionValue == 0){
                    if(role_Value.matches("root")){
                        Intent changePassword = new Intent(getActivity(), ChangePassword.class);
                        changePassword.putExtra(roleValues,roleValue);
                        startActivity(changePassword);
                    }else  if(role_Value.matches("admin") || role_Value.matches("user")){
                        Intent editProfile = new Intent(getActivity(), EditProfileActivity.class);
                        startActivity(editProfile);
                    }
               } else if(positionValue == 1){
                    if(role_Value.matches("root")){
                        deleteAcount();
                    }else if(role_Value.matches("admin") || role_Value.matches("user")){
                        Intent redirect = new Intent(getActivity(), ChangeSecureChatPinActivity.class);
                        startActivity(redirect);
                        Log.d(TAG, "Change chat pin has called!");
                    }
               } else if(positionValue == 2){
                    if(role_Value.matches("admin") || role_Value.matches("user")) {
                        Intent changePassword = new Intent(getActivity(), ChangePassword.class);
                        startActivity(changePassword);
                    } else if(role_Value.matches("root")) {
                        deleteAcount();
                    }
                } else if(positionValue == 3){
                    if(role_Value.matches("admin") || role_Value.matches("user")){
                        deleteAcount();
                    }else if(role_Value.matches("root")){

                    }
                }else if(positionValue == 4){
                    if(role_Value.matches("admin") || role_Value.matches("user")){

                    }
                }
            }
        });
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
                    DatabaseReference databaseRefs = firebaseDatabaseRef.getReference().child("userDetails").child(emailValue);
                    databaseRefs.removeValue();
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
