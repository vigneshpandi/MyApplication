package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Map;

public class SecurePin extends AppCompatActivity {
    private static final String TAG = SecurePin.class.getCanonicalName();
    String userEmail;
    User user;
    private EditText chatPin, conformChatPin;
    private Button loginSecureBtn;
    private String auth;
    private String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_pin);
        init();
        loginSecureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Log.d("user",auth);
               // user = new User();
                if(chatPin.getText().toString().matches(conformChatPin.getText().toString())){
                    user.setChatPin(chatPin.getText().toString());
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
                }}else{
                    Toast.makeText(getActivity(), "Sorry, chatPin Not Match!", Toast.LENGTH_LONG).show();
                    Log.d("securePin", "chat pin  not match");
                }
            }
        });
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
                user.setTINorEIN(map.get("companyCINNumber"));
                user.setEmpId(map.get("employeeId"));
                user.setCompanyName(map.get("companyName"));
                user.setPassword(map.get("password"));
                user.setProviderNPIId(map.get("providerNPIId"));
                user.setProviderName(map.get("providerName"));
                user.setRole(map.get("role"));
                user.setUserName(map.get("emailAddress"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveChatPinCompany(User user) {
        Log.d(TAG, "Add invited company method has been called!");
        final UserDao userDao = new UserDao();
        boolean result = userDao.saveSecure(user);
        if (result) {
            Log.d(TAG, "Company accepted successfully!");
            Intent adminHome = new Intent(getActivity(), AdminHomeActivity.class);
            startActivity(adminHome);
        } else {
            Log.d(TAG, "Error while adding the company, please try again!");
        }
    }

    public void saveChatPinEmp(User user) {
        Log.d(TAG, "Add invited company method has been called!");
        final UserDao userDao = new UserDao();
        boolean result = userDao.sendInvite(user);
        if (result) {
            Log.d(TAG, "Company accepted successfully!");
            Intent employeeHome = new Intent(getActivity(), EmployeeHomeActivity.class);
            startActivity(employeeHome);
        } else {
            Log.d(TAG, "Error while adding the company, please try again!");
        }
    }


    /*public void saveData(){
        Intent adminHome = new Intent(getActivity(), AdminHomeActivity.class);
        startActivity(adminHome);
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        String reArrangeEmail = userEmail.replace(".", "-");
        DatabaseReference dataReference = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail).child("chatPin");
        dataReference.setValue(user.getChatPin());

    }
*/

    public SecurePin getActivity() {
        return this;
    }
}
