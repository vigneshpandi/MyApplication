package com.bluemapletech.hippatextapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bluemapletech.hippatextapp.R;
public class TermsCondition extends AppCompatActivity {
    Button agreeBtn;
    String createRole;
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
