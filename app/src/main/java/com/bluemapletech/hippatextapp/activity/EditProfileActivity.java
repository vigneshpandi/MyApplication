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
import com.bluemapletech.hippatextapp.adapter.PageBaseAdapter;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
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

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getCanonicalName();
    private EditText editFirstName, editLastName, editEmail, editCompanyName, editEmployeeId, editDesignation;
    private Button updateProfileBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private DatabaseReference databaseRef;
    String reArrangeEmail;
    User user = new User();
    private ProgressDialog progressDialog;
    private String comNames, emailAddress, firstName,designation, lastName, userId, auth, chatPin, companyCin, password, profile;
    private String providerNPI,providerName, notification,role, senderId, status;
    private String compFirstName, compLastName, compEmail, compCompany, compEmployee, compDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        fireBaseDatabase = FirebaseDatabase.getInstance();
        init();
    }

    private void init() {
        Log.d(TAG,"init method called");
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        editFirstName = (EditText) findViewById(R.id.com_first_name);
        editLastName = (EditText) findViewById(R.id.com_last_name);
        editEmail = (EditText) findViewById(R.id.edit_com_email);
        editCompanyName = (EditText) findViewById(R.id.edit_com_companyname);
        editEmployeeId = (EditText) findViewById(R.id.edit_emp_id);
        editDesignation = (EditText) findViewById(R.id.edit_designation);
        updateProfileBtn = (Button) findViewById(R.id.update_profile);

        // fireBaseDatabase = FirebaseDatabase.getInstance();
        Log.d(TAG,"logged....."+logged);
        if (logged != null) {
            Log.d("logged",logged.toString());
            reArrangeEmail = logged.getEmail().replace(".", "-");;
        }
        databaseRef = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                comNames = map.get("companyName");
                emailAddress = map.get("emailAddress");
                designation = map.get("designation");
                firstName = map.get("firstName");
                lastName = map.get("lastName");
                userId = map.get("employeeId");
                auth = map.get("auth");
                chatPin = map.get("chatPin");
                companyCin = map.get("companyCINNumber");
                password = map.get("password");
                profile = map.get("profilePhoto");
                providerNPI = map.get("providerNPIId");
                providerName = map.get("providerName");
                notification = map.get("pushNotificationId");
                role = map.get("role");
                senderId = map.get("senderId");
                status = map.get("status");
                editFirstName.setText(firstName);
                editLastName.setText(lastName);
                editEmployeeId.setText(userId);
                editCompanyName.setText(comNames);
                editEmail.setText(emailAddress);
                editDesignation.setText(designation);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    updateProfileBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!validate()){
                Toast.makeText(getActivity(),"Update failed",Toast.LENGTH_LONG).show();
            }else{
                Log.d(TAG, "update profile successfully!");
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Update...");
                progressDialog.show();
                final UserDao userDao = new UserDao();
                user.setAuth(auth);
                user.setChatPin(chatPin);
                user.setTINorEIN(companyCin);
                user.setCompanyName(compCompany);
                user.setDesignation(compDesignation);
                user.setUserName(compEmail);
                user.setEmpId(compEmployee);
                user.setFirstName(compFirstName);
                user.setLastName(compLastName);
                user.setPassword(password);
                user.setProfilePjhoto(profile);
                user.setProviderNPIId(providerNPI);
                user.setProviderName(providerName);
                user.setPushNotificationId(notification);
                user.setRole(role);
                user.setSenderId(senderId);
                user.setStatus(status);
                Log.d(TAG,"userObj........"+user);
                boolean data = userDao.createCompany(user);
                if (data){
                    progressDialog.dismiss();
                    Log.d(TAG, "Update Profile successfuly!");
                    Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "profile not successfuly updated!");
                    Toast.makeText(getActivity(), "profile not successfuly updated!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(intent);
                }
            }
        }
        private boolean validate() {
            compFirstName = editFirstName.getText().toString().trim();
            compLastName = editLastName.getText().toString().trim();
            compEmail = editEmail.getText().toString().trim();
            compCompany = editCompanyName.getText().toString().trim();
            compEmployee = editEmployeeId.getText().toString().trim();
            compDesignation = editDesignation.getText().toString().trim();
            boolean valid = true;
            if(!isValidEmail(compEmail)){
                editEmail.setError("Invalid Email");
                valid = false;
            }

            if(compFirstName.isEmpty()||compFirstName.length()<2){
                editFirstName.setError("Provider NPI Id is invalid");
                valid = false;
            }else{
                editFirstName.setError(null);
            }

            if(compLastName.isEmpty()||compLastName.length()<2){
                editLastName.setError("Provider Name is invalid");
                valid = false;
            }else{
                editLastName.setError(null);
            }

            if(compCompany.isEmpty()||compCompany.length()<2){
                editCompanyName.setError("Provider Name is invalid");
                valid = false;
            }else{
                editCompanyName.setError(null);
            }

            if(compEmployee.isEmpty()||compEmployee.length()<2){
                editEmployeeId.setError("Provider Name is invalid");
                valid = false;
            }else{
                editEmployeeId.setError(null);
            }

            if(compDesignation.isEmpty()||compDesignation.length()<2){
                editDesignation.setError("Provider Name is invalid");
                valid = false;
            }else{
                editDesignation.setError(null);
            }
            return valid;
        }
    });

    }


    private boolean isValidEmail(String compEmailtxts) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(compEmailtxts);
        return  matcher.matches();
    }




    public EditProfileActivity getActivity() {
        return this;
    }
}
