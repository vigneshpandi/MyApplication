package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private EditText usernameTxt, passwordTxt;
    private Button loginBtn;

    private ProgressDialog progressDialog;

    private static final String TAG = LoginActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameTxt.getText().toString().isEmpty() && passwordTxt.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Invalid email address or password!", Toast.LENGTH_LONG).show();
                } else {
                    if (!validate()) {

                    } else {
                        Log.d(TAG, "Login method has been called!");
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser userin = firebaseAuth.getCurrentUser();
                        firebaseAuth.signInWithEmailAndPassword(usernameTxt.getText().toString(),
                                passwordTxt.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //  Toast.makeText(getActivity(), "You are logged in successfully!", Toast.LENGTH_LONG).show();
                                    getUserInformation();
                                } else {
                                    Toast.makeText(getActivity(), "Sorry, Please try again later!", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private boolean validate() {
        String usernameTxts = usernameTxt.getText().toString().trim();
        String passwordTxts = passwordTxt.getText().toString().trim();
        boolean valid = true;
        Log.d(TAG, "Validate method has been called!");

        if (!isValidEmail(usernameTxts)) {
            usernameTxt.setError("Invalid email address");
            valid = false;
        }
        if (passwordTxts.isEmpty() || passwordTxts.length() < 8 || passwordTxts.length() > 16) {
            passwordTxt.setError("Password should be between 8 to 16 alphanumerics");
            valid = false;
        } else {
            passwordTxt.setError(null);
        }
        return valid;
    }

    private boolean isValidEmail(String usernameTxts) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(usernameTxts);
        return matcher.matches();
    }


    public void init() {
        usernameTxt = (EditText) findViewById(R.id.user_name);
        passwordTxt = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login_btn);
    }

    public void getUserInformation() {
        String reArrangeEmail = usernameTxt.getText().toString().replace(".", "-");

        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        Log.d(TAG, "Logged in user information's:");
        DatabaseReference dataReference = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                String auth = map.get("auth");
                String role = map.get("role");
                if (auth.matches("1") && role.matches("root")) {
                    Log.d(TAG, "Redirected to root admin dash board");
                    Intent rootHome = new Intent(getActivity(), RootHomeActivity.class);
                    startActivity(rootHome);
                    progressDialog.dismiss();
                } else if (auth.matches("1") && role.matches("admin")) {
                    Intent adminHome = new Intent(getActivity(), AdminHomeActivity.class);
                    startActivity(adminHome);
                    progressDialog.dismiss();
                } else if (auth == "1" && role == "user") {
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Sorry, under working!", Toast.LENGTH_LONG).show();
                    Log.d("loginActivity", "under working");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public LoginActivity getActivity() {
        return this;
    }
}
