package com.bluemapletech.hippatextapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bluemapletech.hippatextapp.R;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    private ListView iv;
    ArrayList<String> arrlist;
    private String[] lv_arr = {"Profile","Change Password","Delete An Acount","Notification Settings"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        iv = (ListView) findViewById(R.id.list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        iv.setAdapter(new ArrayAdapter<String>(Settings.this,
                android.R.layout.simple_list_item_1, lv_arr));

        iv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Settings","profilesSettings"+position);
                int positionValue = position;
                if(positionValue == 0){
                    Intent editProfile = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(editProfile);
               } else if(positionValue == 1){
                    Intent changePassword = new Intent(getActivity(), ChangePassword.class);
                    startActivity(changePassword);
               } else if(positionValue == 2){

                } else if(positionValue == 3){

                }
            }
        });
    }
    public Settings getActivity() {
        return this;
    }
}
