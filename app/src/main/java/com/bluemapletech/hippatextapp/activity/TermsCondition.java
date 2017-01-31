package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bluemapletech.hippatextapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TermsCondition extends AppCompatActivity {
    Button agreeBtn;
    String createRole,isOnline;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_condition);
        createRole = getIntent().getStringExtra(HomeActivity.createUser);
        Log.d("createRole","valuees"+createRole);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Terms And Condition");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            TextView header = (TextView) findViewById(R.id.header);
            header.setText("Terms And Condition");

        }
        init();
    }

    public void init(){

        //initialize the fields

         agreeBtn = (Button) findViewById(R.id.aggre_btn);


        //click the action

        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(createRole.matches("admin")){
                    Intent intent = new Intent(getActivity(), CompanyRegistrationActivity.class);
                    startActivity(intent);
                }else if(createRole.matches("user")){
                    Intent intent = new Intent(getActivity(), EmployeeRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });
    }



    public TermsCondition getActivity(){
        return this;
    }
}
