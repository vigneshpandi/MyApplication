package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Map;

/**
 * Created by Poornima on 11/16/2016.
 */

public class ChangeSecureChatPinActivity extends AppCompatActivity {

    private static final String TAG = ChangeSecureChatPinActivity.class.getCanonicalName();
    private FirebaseAuth firebaseAuthRef;
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private SecureRandom random;
    private ProgressDialog progressDialog;

    private String chatpin;
    User user;
    private String auth;
    private String role;
    private String securePin;
    private EditText oldChatPin, newChatPin, confirmChatPin;
    private Button resetPinBtn;
   private  String reArrangeEmail;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_secure_chat_pin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
        resetPinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateOldPin()) {
                    Toast.makeText(getActivity(), "Please enter the Chat Pin", Toast.LENGTH_LONG).show();
                    return;
                } else if (newChatPin.getText().toString().matches(confirmChatPin.getText().toString())) {
                    securePin = newChatPin.getText().toString();
                    Log.d(TAG, "Secure pin text: " + securePin);
                    user = new User();
                    byte[] data = new byte[0];
                    try {
                        data = securePin.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String enCode = Base64.encodeToString(data, Base64.NO_WRAP);
                    user.setChatPin(enCode);
                    user.setUserName(reArrangeEmail);
                    resetPinBtn(user);

                }
            }
        });
    }

    private void resetPinBtn(User user) {
       /* Log.d(TAG, "Add invited company method has been called!");*/
        final UserDao userDao = new UserDao();
        boolean result = userDao.saveSecurePin(user);
        Log.d("result", String.valueOf(result));
        if (result) {
            Log.d(TAG, "Changed Pin successfully!");
            Intent adminHome = new Intent(getActivity(), AdminHomeActivity.class);
            startActivity(adminHome);
        } else {
            Log.d(TAG, "Unable to process, please try again!");
        }
    }

    private void init() {
        oldChatPin = (EditText) findViewById(R.id.old_chat_pin);
        newChatPin = (EditText) findViewById(R.id.new_chat_pin);
        confirmChatPin = (EditText) findViewById(R.id.confirm_chat_pin);
        resetPinBtn = (Button) findViewById(R.id.reset_pin_btn);
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuthRef = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuthRef.getCurrentUser();
        reArrangeEmail = logged.getEmail().replace(".", "-");
        DatabaseReference dataReference = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                chatpin = (map.get("chatPin"));
                role = map.get("role");
                Log.d(TAG, "Old chat pin: " + chatpin);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private boolean validateOldPin() {
        byte[] data1 = Base64.decode(chatpin, Base64.NO_WRAP);
        String text = null;
        try {
            text = new String(data1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (text.equals(oldChatPin.getText().toString()) && text.matches(oldChatPin.getText().toString())) {
            Log.d(TAG, "Old chat pin has been called!");
            validate();
            oldChatPin.setError(null);
            return true;
        } else {
            oldChatPin.setError("Old Chat Pin is invalid");
            return false;
        }
    }

    private boolean validate() {
        //  String oldChatPins = oldChatPin.getText().toString().trim();
        String newChatPins = newChatPin.getText().toString().trim();
        String confirmChatPins = confirmChatPin.getText().toString();
        boolean valid = true;
       /* if (oldChatPins.isEmpty() || oldChatPins.length() < 4) {
            oldChatPin.setError("Old Chat Pin is invalid");
            valid = false;
        } else {
            oldChatPin.setError(null);
        }*/

        if (newChatPins.isEmpty() || newChatPins.length() < 4) {
            newChatPin.setError("New Chat Pin is invalid");
            valid = false;
        } else {
            newChatPin.setError(null);
        }

        if (confirmChatPins.isEmpty() || confirmChatPins.length() < 4) {
            confirmChatPin.setError("Confirm Chat Pin is invalid");
            valid = false;
        } else {
            confirmChatPin.setError(null);
        }
        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(role.equals("admin")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageAdmin();
                    return true;
            }
        }
        if(role.equals("user")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageEmp();
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void backPageEmp() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),EmployeeHomeActivity.class));
    }

    private void backPageAdmin() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
    }


    public ChangeSecureChatPinActivity getActivity() {
        return this;
    }

}



