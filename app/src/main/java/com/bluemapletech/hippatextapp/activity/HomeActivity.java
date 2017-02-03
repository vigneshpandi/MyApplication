package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.utils.ExamplesService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
boolean dummy=false;
    private Button loginBtn, compBtn, empBtn;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String  loginRole,loginKey,loginAuth,loginMail;
    public static final String userLogiMailId = "userLogiMailId";
    private static final String TAG = HomeActivity.class.getCanonicalName();
    public static final String createUser = "createUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //getActivity().startService(new Intent(HomeActivity.this, ExamplesService.class));
        Log.d(TAG,"homeControlle has been called");
        //login user details
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginRole =  pref.getString("role", "");
        loginKey = pref.getString("loginKey","");
        loginAuth = pref.getString("auth","");
        loginMail = pref.getString("loginMail","");

        if(!loginKey.matches("") && !loginRole.matches("")){
            Log.d("login","BeforeLoginKeyCalled"+loginKey);
            if(loginRole.matches("root") && loginAuth.matches("1") && !loginMail.matches("")) {
                onlineUser();
                Intent rootHome = new Intent(getActivity(), RootHomeActivity.class);
                startActivity(rootHome);
            }else if(loginRole.matches("admin") && loginAuth.matches("1") ) {
                onlineUser();
                Intent adminHome = new Intent(getActivity(), AdminHomeActivity.class);
                startActivity(adminHome);
            }else if(loginRole.matches("user") && loginAuth.matches("1") ) {
               onlineUser();
                Intent employeeHome = new Intent(getActivity(), EmployeeHomeActivity.class);
                startActivity(employeeHome);
            }else if(!loginAuth.matches("1") ) {
              onlineUser();
                Intent redirect = new Intent(getActivity(), NotAcceptedUser.class);
                redirect.putExtra(userLogiMailId, loginMail);
                startActivity(redirect);
            }
        }else{
            init();
        }
    }

    public void init(){
        Log.d(TAG, "Init method has been called!");
        loginBtn = (Button) findViewById(R.id.login_button);
        compBtn = (Button) findViewById(R.id.register_as_comp_button);
        empBtn = (Button) findViewById(R.id.register_as_emp_button);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login activity has been called!");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        compBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Employee registration has been clicked!");
                Intent intent = new Intent(getActivity(), TermsCondition.class);
                intent.putExtra(createUser,"admin");
                startActivity(intent);
            }
        });

        empBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Employee registration has been clicked!");

                Intent intent = new Intent(getActivity(), TermsCondition.class);
                intent.putExtra(createUser,"user");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(dummy)
        stopService(new Intent(HomeActivity.this, ExamplesService.class));
        super.onDestroy();
    }

    private void onlineUser() {
        HashMap<String, Object> onlineUser = new HashMap<>();
        onlineUser.put("onlineUser",loginMail);
        String reArrangeEmail = loginMail.replace(".", "-");
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
        dataReferences.setValue(onlineUser);
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
    public HomeActivity getActivity(){
        return this;
    }
}
