package com.bluemapletech.hippatextapp.activity;

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
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePassword extends AppCompatActivity {
    private static final String TAG = ChangePassword.class.getCanonicalName();
    private FirebaseDatabase firebaseDatabaseRef;
    private  DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private String roleValue;
    private Button changePassword;
    public static final String roleValuesReturn = "roleValuesReturn";
    private EditText emailAddress;

    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        Log.d(TAG, "Logged in user information's: " + logged.getEmail());
        changePassword = (Button) findViewById(R.id.send_password);
        emailAddress = (EditText) findViewById(R.id.user_email);
        emailAddress.setText(logged.getEmail());
        roleValue = getIntent().getStringExtra(Settings.roleValues);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void init(){
       final  FirebaseAuth   auth = FirebaseAuth.getInstance();
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"clicking the button"+email);
                auth.sendPasswordResetEmail(String.valueOf(emailAddress.getText())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "reset the password link send  your mail!", Toast.LENGTH_LONG).show();
                           /* startActivity(new Intent(getActivity(),Settings.class));*/
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
                backPage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backPage() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),RootHomeActivity.class));
    }
    public ChangePassword getActivity() { return this;  }
}
