package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageAdminBaseAdapter;
import com.bluemapletech.hippatextapp.dao.CompanyDao;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class SecurePin extends AppCompatActivity {
    private static final String TAG = SecurePin.class.getCanonicalName();
    String userEmail;
    User user;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private EditText chatPin, conformChatPin;
    private Button loginSecureBtn;
    private String auth;
    private String role;
    private String securePin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_pin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
        loginSecureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Log.d("user",auth);
                if(!validate()){
                    Toast.makeText(getActivity(),"Please enter the Chat Pin",Toast.LENGTH_LONG).show();
                    return;
                }else if(chatPin.getText().toString().matches(conformChatPin.getText().toString())){
                    securePin = chatPin.getText().toString();
                    pref = getApplicationContext().getSharedPreferences("loginUserDetails", MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putString("chatPin",securePin);
                    String loginKey = "loginKey";
                    editor.putString("loginKey",loginKey);
                    editor.commit();
                    Log.d("userChatPin", pref.getString("chatPin", ""));
                    byte[] data = new byte[0];
                    try {
                        data = securePin.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String enCode = Base64.encodeToString(data, Base64.NO_WRAP);
                    user.setChatPin(enCode);
                    user.setStatus("login");
                if (user.getAuth().matches("1") && user.getRole().matches("root")) {
                    Log.d(TAG, "Redirected to root admin dash board");
                    Intent rootHome = new Intent(getActivity(), RootHomeActivity.class);
                    startActivity(rootHome);
                } else if (user.getAuth().matches("1") && user.getRole().matches("admin")) {
                    Log.d("secureactivity","admin");
                    saveChatPinCompany(user);
                } else if (user.getAuth().matches("1") && user.getRole().matches("user")) {
                    saveChatPinEmp(user);
                }else if(!user.getAuth().matches("1")){
         if(user.getRole().matches("user")){
             saveChatPinUser(user);
        }else if(user.getRole().matches("admin")){
             saveChatPinUser(user);
        }if(user.getRole().matches("root")){
                        saveChatPinUser(user);
                    }
                }
                }else{
                    Toast.makeText(getActivity(), "Sorry, chatPin Not Match!", Toast.LENGTH_LONG).show();
                    Log.d("securePin", "chat pin  not match");
                }
            }
        });
    }

    private boolean validate() {
        String chatPins = chatPin.getText().toString().trim();
        String confirmChatPins = conformChatPin.getText().toString();
        boolean valid = true;
        if(chatPins.isEmpty()||chatPins.length()<4){
            chatPin.setError("Chat Pin is invalid");
            valid = false;
        }else{
            chatPin.setError(null);
        }

        if(confirmChatPins.isEmpty()||confirmChatPins.length()<4){
            conformChatPin.setError("Conform Chat Pin is invalid");
            valid= false;
        }else{
            conformChatPin.setError(null);
        }
        return valid;
    }

    public void init(){
        chatPin = (EditText) findViewById(R.id.chat_pin);
        conformChatPin = (EditText) findViewById(R.id.conform_chatPin);
        loginSecureBtn = (Button) findViewById(R.id.login_secure_btn);

        //get UserEmail from loginPage
        userEmail = getIntent().getStringExtra(LoginActivity.userLogiMailId);


        String reArrangeEmail = userEmail.replace(".", "-");

        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);


        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                 auth = map.get("auth");
                 role = map.get("role");
                user = new User();
                user.setAuth(map.get("auth"));
                user.setChatPin(map.get("chatPin"));
                user.setTINorEIN(map.get("companyCINNumber"));
                user.setCompanyName(map.get("companyName"));
                user.setDesignation(map.get("designation"));
                user.setUserName(map.get("emailAddress"));
                user.setEmpId(map.get("employeeId"));
                user.setFirstName(map.get("firstName"));
                user.setLastName(map.get("lastName"));
                user.setPassword(map.get("password"));
                user.setProfilePjhoto(map.get("profilePhoto"));
                user.setProviderNPIId(map.get("providerNPIId"));
                user.setProviderName(map.get("providerName"));
                user.setRole(map.get("role"));
                user.setSenderId(map.get("senderId"));
                user.setCreateDate(map.get("createdDate"));
                user.setUpdateDate(map.get("updatedDate"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveChatPinCompany(User user) {
        Log.d(TAG, "saveChatPinCompany");
        final UserDao userDao = new UserDao();
        boolean result = userDao.saveSecure(user);
        if (result) {
            addNotificationId();
            Log.d(TAG, "Company secure pin added successfully!");
            Intent adminHome = new Intent(getActivity(), AdminHomeActivity.class);
            startActivity(adminHome);
        } else {
            Log.d(TAG, "Error while adding the company, please try again!");
        }
    }

    public void saveChatPinEmp(User user) {
        Log.d(TAG, "saveChatPinEmp!");
        final UserDao userDao = new UserDao();
        boolean result = userDao.saveSecure(user);
        Log.d("resullttt", String.valueOf(result));
        if (result) {
            addNotificationId();
            Log.d(TAG, "Company accepted successfully!");
            Intent employeeHome = new Intent(getActivity(), EmployeeHomeActivity.class);
            startActivity(employeeHome);
        } else {
            Log.d(TAG, "Error while adding the company, please try again!");
        }
    }

    public void saveChatPinUser(User user) {
        Log.d("dsdsdsdsd","dfdfdfdfd");
        final UserDao userDao = new UserDao();
        boolean result = userDao.saveSecure(user);
        if (result) {
            addNotificationId();
            Log.d(TAG, "Company accepted successfully!");
            Intent redirect = new Intent(getActivity(), NotAcceptedUser.class);
            startActivity(redirect);
        } else {
            Log.d(TAG, "Error while adding the company, please try again!");
        }
    }


    private void addNotificationId() {
        String reArrangeEmail = userEmail.replace(".", "-");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"refreshedToken After login" + refreshedToken);
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail).child("pushNotificationId");
        dataReferences.setValue(refreshedToken);
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
        startActivity(new Intent(getActivity(),LoginActivity.class));
    }

    public SecurePin getActivity() {
        return this;
    }
}
