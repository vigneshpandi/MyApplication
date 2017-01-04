package com.bluemapletech.hippatextapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bluemapletech.hippatextapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    private static final String TAG = Settings.class.getCanonicalName();
    private ListView iv;
    private String roleValue;
    ArrayList<String> arrlist;
    public static final String roleValues = "roleValues";
    private String[] lv_arr = {"Profile","Change Password","Delete An Acount","Notification Settings"};
    private String[] iv_arr_root = {"Change Password","Delete An Acount","Notification Settings"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        iv = (ListView) findViewById(R.id.list_view);
        roleValue = getIntent().getStringExtra(RootHomeActivity.role);
        roleValue = getIntent().getStringExtra(ChangePassword.roleValuesReturn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(roleValue.matches("root")) {
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
                    if(roleValue.matches("root")){
                        Intent changePassword = new Intent(getActivity(), ChangePassword.class);
                        changePassword.putExtra(roleValues,roleValue);
                        startActivity(changePassword);
                    }else {
                        Intent editProfile = new Intent(getActivity(), EditProfileActivity.class);
                        startActivity(editProfile);
                    }
               } else if(positionValue == 1){
                    if(roleValue.matches("root")){
                        deleteAcount();
                    }
                    Intent changePassword = new Intent(getActivity(), ChangePassword.class);
                    startActivity(changePassword);
               } else if(positionValue == 2){
                    if(!roleValue.matches("root")) {
                        deleteAcount();
                    }
                } else if(positionValue == 3){

                }
            }
        });
    }

    public void deleteAcount(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User account deleted.");

                    Intent list = new Intent(getActivity(), HomeActivity.class);
                    startActivity(list);
                }
            }
        });
    }
    public Settings getActivity() {
        return this;
    }
}
