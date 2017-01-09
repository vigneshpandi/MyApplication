package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    boolean loginaccess = true;
    FirebaseAuth firebaseAuth;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    SharedPreferences.Editor editors;

    private static FirebaseDatabase firebaseDatabaseRef;
    private EditText usernameTxt, passwordTxt;
    private Button loginBtn , forgetPassword;
    private ProgressDialog progressDialog;
    private static final String TAG = LoginActivity.class.getCanonicalName();
    public static final String userLogiMailId = "userLogiMailId";
    User user = new User();
    private String userPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        prefs = getApplicationContext().getSharedPreferences("checkForLogin", MODE_PRIVATE);
        init();
        firebaseAuth = FirebaseAuth.getInstance();
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
                        progressDialog.setCanceledOnTouchOutside(false);
                       // firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser userin = firebaseAuth.getCurrentUser();
                        firebaseAuth.signInWithEmailAndPassword(usernameTxt.getText().toString(),
                                passwordTxt.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    editors = prefs.edit();
                                    editors.putString("checkloginstatus", "true");
                                    editors.commit();
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


        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usernameTxt.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your e-mail to continue!", Toast.LENGTH_LONG).show();
                } else{
                    firebaseAuth.fetchProvidersForEmail(usernameTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            Log.d(TAG,"task"+task);
                            if(task.isSuccessful()){
                                 FirebaseAuth   auth = FirebaseAuth.getInstance();
                                auth.sendPasswordResetEmail(usernameTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), "reset the password link send  your mail!", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }else {
                                Toast.makeText(getActivity(), "Email address is not found!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
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
        forgetPassword = (Button) findViewById(R.id.reset_button);
        pref = getApplicationContext().getSharedPreferences("loginUserDetails", MODE_PRIVATE);
    }

    public void getUserInformation() {
        String reArrangeEmail = usernameTxt.getText().toString().replace(".", "-");

        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String checkLogin = prefs.getString("checkloginstatus", "");
                if (checkLogin.matches("true")) {
                    editors = prefs.edit();
                    editors.putString("checkloginstatus", "false");
                    editors.commit();
                    Map<String, String> map = (Map) dataSnapshot.getValue();
                    String auth = map.get("auth");
                    String role = map.get("role");
                    String status = map.get("status");
                    String userName = map.get("emailAddress");
                    String userChatPin = map.get("chatPin");
                    String companyName = map.get("companyName");
                    String Password = map.get("password");
                    String text = null;
                    if (Password != null && !Password.matches("")) {
                        byte[] dataPass = Base64.decode(userChatPin, Base64.NO_WRAP);
                        try {
                            text = new String(dataPass, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    userPassword = passwordTxt.getText().toString();
                    byte[] enCode = new byte[0];
                    try {
                        enCode = userPassword.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String enCodes = Base64.encodeToString(enCode, Base64.NO_WRAP);
                    if(!text.matches(passwordTxt.getText().toString())){
                        String reArrangeEmail = userName.replace(".", "-");
                        firebaseDatabaseRef = FirebaseDatabase.getInstance();
                        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("password");
                        databaseRef.setValue(enCodes);

                    }
                    String text1 = null;
                    if (userChatPin != null && !userChatPin.matches("")) {
                        byte[] data1 = Base64.decode(userChatPin, Base64.NO_WRAP);
                        try {
                            text = new String(data1, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    editor = pref.edit();
                    editor.putString("chatPin", text);
                    editor.putString("role", role);
                    editor.putString("loginMail", userName);
                    editor.putString("loginCompanyName", companyName);
                    editor.putString("auth", auth);
                    editor.commit();

                    Log.d(TAG, "Logged in user information's:");
                    if (status.matches("login")) {
                        String loginKey = "loginKey";
                        editor.putString("loginKey", loginKey);
                        editor.commit();
                        if (auth.matches("1") && role.matches("root")) {
                            addNotificationId();
                            onlineUser();
                            Intent rootHome = new Intent(getActivity(), RootHomeActivity.class);
                            startActivity(rootHome);
                            progressDialog.dismiss();
                        } else if (auth.matches("1") && role.matches("admin")) {
                            addNotificationId();
                            onlineUser();
                            Intent adminHome = new Intent(getActivity(), AdminHomeActivity.class);
                            startActivity(adminHome);
                            progressDialog.dismiss();
                        } else if (auth.matches("1") && role.matches("user")) {
                            onlineUser();
                            addNotificationId();
                            Intent employeeHome = new Intent(getActivity(), EmployeeHomeActivity.class);
                            startActivity(employeeHome);
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            addNotificationId();
                            onlineUser();
                            Intent redirect = new Intent(getActivity(), NotAcceptedUser.class);
                            redirect.putExtra(userLogiMailId, userName);
                            startActivity(redirect);
                        /*Toast.makeText(getActivity(), "Sorry, under working!", Toast.LENGTH_LONG).show();
                        Log.d("loginActivity", "under working");*/
                        }
                    } else if (status.matches("chatPin")) {
                        progressDialog.dismiss();
                        Intent redirect = new Intent(getActivity(), SecurePin.class);
                        redirect.putExtra(userLogiMailId, userName);
                        startActivity(redirect);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Sorry, under working!", Toast.LENGTH_LONG).show();
                        Log.d("loginActivity", "under working");
                    }
                }
            }// end
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addNotificationId() {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "refreshedToken After login" + refreshedToken);
            String reArrangeEmail = usernameTxt.getText().toString().replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail).child("pushNotificationId");
            dataReferences.setValue(refreshedToken);
    }
    private void onlineUser() {
        HashMap<String, Object> onlineUser = new HashMap<>();
        onlineUser.put("onlineUser",usernameTxt.getText().toString());
        String reArrangeEmail = usernameTxt.getText().toString().replace(".", "-");
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
        dataReferences.setValue(onlineUser);
    }

    public LoginActivity getActivity() {
        return this;
    }
}
