package com.bluemapletech.hippatextapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bluemapletech.hippatextapp.R;

public class HomeActivity extends AppCompatActivity {

    private Button loginBtn, compBtn, empBtn;

    private static final String TAG = HomeActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();

        /*This listener called when ever the login button clicks*/
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login activity has been called!");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        /*This listener called when ever the company login button clicks*/
        compBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Employee registration has been clicked!");
                Intent intent = new Intent(getActivity(), CompanyRegistrationActivity.class);
                startActivity(intent);
            }
        });

        /*This listener called when ever the employee login button clicks*/
        empBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Employee registration has been clicked!");
                Intent intent = new Intent(getActivity(), EmployeeRegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public void init(){
        Log.d(TAG, "Init method has been called!");
        loginBtn = (Button) findViewById(R.id.login_button);
        compBtn = (Button) findViewById(R.id.register_as_comp_button);
        empBtn = (Button) findViewById(R.id.register_as_emp_button);
    }

    public HomeActivity getActivity(){
        return this;
    }
}
