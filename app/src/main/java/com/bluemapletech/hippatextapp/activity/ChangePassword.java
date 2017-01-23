package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChangePassword extends AppCompatActivity {
    private static final String TAG = ChangePassword.class.getCanonicalName();
    private FirebaseDatabase firebaseDatabaseRef;
    private  DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private String roleValue,isOnline;
    private Button changePassword;
    private ProgressDialog progressDialog;
    public static final String roleValuesReturn = "roleValuesReturn";
    private EditText emailAddress;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String email,loginRoleValue,loginAuthValue,loginMail;
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
    SharedPreferences pref3;
    SharedPreferences.Editor editor3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        Log.d(TAG, "Logged in user information's: " + logged.getEmail());
        pref3 = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginMail =  pref3.getString("loginMail", "");
        changePassword = (Button) findViewById(R.id.send_password);
        emailAddress = (EditText) findViewById(R.id.user_email);
        emailAddress.setText(loginMail);
        roleValue = getIntent().getStringExtra(Settings.roleValues);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        TextView header = (TextView) findViewById(R.id.header);
        header.setText("Change Password");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Change Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginRoleValue =  pref.getString("role", "");
        loginAuthValue = pref.getString("auth","");
    }

    public void init(){
        final  FirebaseAuth   auth = FirebaseAuth.getInstance();
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"progrssBar is show...");
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Processing change password...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                Log.d(TAG,"clicking the button"+email);
                auth.sendPasswordResetEmail(String.valueOf(emailAddress.getText())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "reset the password link send  your mail!", Toast.LENGTH_LONG).show();
                           /* startActivity(new Intent(getActivity(),Settings.class));*/
                            progressDialog.dismiss();
                            Intent changePassword = new Intent(getActivity(), Settings.class);
                            changePassword.putExtra(roleValuesReturn,roleValue);
                            startActivity(changePassword);
                        } else{
                            Toast.makeText(getActivity(), "reset the password send again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(loginRoleValue.matches("root")&&loginAuthValue.matches("1")){
                    startActivity(new Intent(getActivity(),RootHomeActivity.class));
                }
                if(loginRoleValue.matches("admin")&&loginAuthValue.matches("1")){
                    startActivity(new Intent(getActivity(),AdminHomeActivity.class));
                }
                if(loginRoleValue.matches("user")&&loginAuthValue.matches("1")){
                    startActivity(new Intent(getActivity(),EmployeeHomeActivity.class));
                }else if(!loginAuthValue.matches("1")&& loginRoleValue.matches("root")){
                    startActivity(new Intent(getActivity(),NotAcceptedUser.class));
                }else if(!loginAuthValue.matches("1")&& loginRoleValue.matches("admin")){
                    startActivity(new Intent(getActivity(),NotAcceptedUser.class));
                }else if(!loginAuthValue.matches("1")&& loginRoleValue.matches("user")){
                    startActivity(new Intent(getActivity(),NotAcceptedUser.class));
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backPage() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),RootHomeActivity.class));
    }
    @Override
    public void onPause()
    {
        pref1 = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref1.getString("isOnline", "");
        loginMail =  pref1.getString("loginMail", "");
        if(isOnline.matches("true")) {
            fireBaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser logged = firebaseAuth.getCurrentUser();
           // String reArrangeEmail = logged.getEmail().replace(".", "-");
            String reArrangeEmail = loginMail.replace(".", "-");
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
        loginMail =  pref1.getString("loginMail", "");
        if(isOnline.matches("true")) {
            HashMap<String, Object> onlineReenter = new HashMap<>();
            fireBaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser logged = firebaseAuth.getCurrentUser();
           // String reArrangeEmail = logged.getEmail().replace(".", "-");
            String reArrangeEmail = loginMail.replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            onlineReenter.put("onlineUser", logged.getEmail());
            dataReferences.setValue(onlineReenter);
        }
        super.onResume();
    }

    public ChangePassword getActivity() { return this;  }
}
