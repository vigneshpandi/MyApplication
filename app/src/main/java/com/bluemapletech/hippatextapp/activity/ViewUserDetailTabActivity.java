package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageEmployeeBaseAdpter;
import com.bluemapletech.hippatextapp.dao.CompanyDao;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.widgets.InterChatEmployeeTabActivity;
import com.bluemapletech.hippatextapp.widgets.IntraChatEmployeeTabActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewUserDetailTabActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    User user1 = new User();


    SharedPreferences preflogin;
    SharedPreferences.Editor editorlogin;
    String isOnline;

    private FirebaseDatabase fireBaseDatabase; private FirebaseAuth firebaseAuth;
    String logi_role_value,reArrangeEmail,userAuths,userEmails,userId;
    private TextView userEmail,compName,empId,providerNPI,providerName,providerNpiLabel,providerNameLabel;
    private static final String TAG = ViewUserDetailTabActivity.class.getCanonicalName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_detail_tab);
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        logi_role_value =  pref.getString("role", "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }
    public void init() {


        userEmails = getIntent().getStringExtra(PageEmployeeBaseAdpter.userEmails);

        if(userEmails==null || userEmails.matches("")){
            userEmails = getIntent().getStringExtra(Inter_chat_admin_activity.userEmails);
        }
       /* if(userEmails ==null || userEmails.matches("")) {
            userEmails = getIntent().getStringExtra(IntraChatEmployeeTabActivity.userEmails);
        }*/
        userEmail = (TextView) findViewById(R.id.user_email);
        compName = (TextView) findViewById(R.id.comp_name);
        empId = (TextView) findViewById(R.id.employee_id);
        providerNPI = (TextView) findViewById(R.id.provider_npi_text);
        providerName = (TextView) findViewById(R.id.provider_name_text);
        providerNpiLabel = (TextView) findViewById(R.id.provider_npi);
        providerNameLabel = (TextView) findViewById(R.id.provider_name);


        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        reArrangeEmail = userEmails.replace(".", "-");
        databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                Map<String, String> map = (Map) dataSnapshot.getValue();
                if(map!=null) {
                    user1.setUserName(map.get("emailAddress"));
                    user1.setPushNotificationId(map.get("pushNotificationId"));
                    user1.setSenderId(map.get("senderId"));
                    user1.setFirstName(map.get("firstName"));
                    user1.setLastName(map.get("lastName"));
                    user1.setCompanyName(map.get("companyName"));
                    user1.setProviderNPIId(map.get("providerNPIId"));
                    user1.setProviderName(map.get("providerName"));
                    user1.setRole(map.get("role"));
                    user1.setAuth(map.get("auth"));
                    user1.setEmpId(map.get("employeeId"));
                    user1.setTINorEIN(map.get("companyCINNumber"));
                    if (user1.getRole().equals("user")) {
                        providerNPI.setVisibility(View.INVISIBLE);
                        providerName.setVisibility(View.INVISIBLE);
                        providerNpiLabel.setVisibility(View.INVISIBLE);
                        providerNameLabel.setVisibility(View.INVISIBLE);
                    }
                    if (user1.getFirstName().matches("") && user1.getLastName().matches("")) {
                        String[] valueuserName = user1.getUserName().split("@");
                        user1.setFirstName(valueuserName[0]);
                    }
                    Log.d(TAG, "userAuth...." + user1.getAuth());

// set the value for textFields
                    if (user1.getEmpId() != null) {
                        userId = user1.getEmpId();
                    } else if (user1.getTINorEIN() != null) {
                        userId = user1.getTINorEIN();
                    }
                    userEmail.setText(user1.getUserName());
                    empId.setText(userId);
                    compName.setText(user1.getCompanyName());
                    providerNPI.setText(user1.getProviderNPIId());
                    providerName.setText(user1.getProviderName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        if(logi_role_value.matches("user")) {
            startActivity(new Intent(getActivity(), EmployeeHomeActivity.class));
        }else if(logi_role_value.matches("admin")){
            startActivity(new Intent(getActivity(), Inter_chat_admin_activity.class));
        }
    }

    @Override
    public void onPause()
    {
        preflogin = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  preflogin.getString("isOnline", "");
        if(isOnline.matches("true")) {
            fireBaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser logged = firebaseAuth.getCurrentUser();
            String reArrangeEmail = logged.getEmail().replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            dataReferences.removeValue();
        }
        super.onPause();
        //Do whatever you want to do when the application stops.
    }


    @Override
    protected  void onResume(){
        preflogin = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  preflogin.getString("isOnline", "");
        if(isOnline.matches("true")) {
            HashMap<String, Object> onlineReenter = new HashMap<>();
            fireBaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser logged = firebaseAuth.getCurrentUser();
            String reArrangeEmail = logged.getEmail().replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            onlineReenter.put("onlineUser", logged.getEmail());
            dataReferences.setValue(onlineReenter);
        }
        super.onResume();
    }
    public ViewUserDetailTabActivity getActivity() {
        return this;
    }
}

