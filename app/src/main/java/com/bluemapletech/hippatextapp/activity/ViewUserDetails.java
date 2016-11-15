package com.bluemapletech.hippatextapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageAdminBaseAdapter;
import com.bluemapletech.hippatextapp.adapter.PageBaseAdapter;
import com.bluemapletech.hippatextapp.dao.CompanyDao;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ViewUserDetails extends AppCompatActivity {
    private static final String TAG = ViewUserDetails.class.getCanonicalName();
    String empMailId = null;
    String userId;
    String adminMailId = null;
    String reArrangeEmail;
    String userAuths;
    User user = new User();
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private TextView userEmail, compName, empId, providerName, providerNPI, providerNpiLabel, providerNameLabel;
    private Button acceptBtn, pendingBtn, deleteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);
        init();
    }
    public void init() {
        empMailId = getIntent().getStringExtra(PageAdminBaseAdapter.userEmails);
        adminMailId = getIntent().getStringExtra(PageBaseAdapter.userEmail);
        userAuths = getIntent().getStringExtra(PageBaseAdapter.userAuth);
        acceptBtn = (Button) findViewById(R.id.accept_user_btn);
        pendingBtn = (Button) findViewById(R.id.pending_user_btn);
        deleteBtn = (Button) findViewById(R.id.delete_user_btn);
if(!userAuths.matches("0")){
    acceptBtn.setVisibility(View.INVISIBLE);
    pendingBtn.setVisibility(View.INVISIBLE);
    deleteBtn.setVisibility(View.INVISIBLE);
}
        userEmail = (TextView) findViewById(R.id.user_email);
        compName = (TextView) findViewById(R.id.comp_name);
        empId = (TextView) findViewById(R.id.employee_id);
        providerNPI = (TextView) findViewById(R.id.provider_npi_text);
        providerName = (TextView) findViewById(R.id.provider_name_text);
        providerNpiLabel = (TextView) findViewById(R.id.provider_npi);
        providerNameLabel = (TextView) findViewById(R.id.provider_name);

        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        if (adminMailId != null) {
            Log.d("adminMailId",adminMailId);
            reArrangeEmail = adminMailId.replace(".", "-");
            user.setUserName(reArrangeEmail);
        } else if(empMailId!=null){
            Log.d("empMailId",empMailId);
            reArrangeEmail = empMailId.replace(".", "-");
            user.setUserName(reArrangeEmail);
        }
        databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                Map<String, String> map = (Map) dataSnapshot.getValue();
                String comNames = map.get("companyName");
                user.setCompanyName(comNames);
               String role =  map.get("role");
                Log.d("role.....","role........"+role);
                if(role.equals("user")){
                    Log.d("inside","inside");
                    providerNPI.setVisibility(View.INVISIBLE);
                    providerName.setVisibility(View.INVISIBLE);
                    providerNpiLabel.setVisibility(View.INVISIBLE);
                    providerNameLabel.setVisibility(View.INVISIBLE);
                }
                user.setRole(role);
                String emailAddress = map.get("emailAddress");
                if (adminMailId != null) {
                    userId = map.get("companyCINNumber");
                } else if(empMailId!=null) {
                    userId = map.get("employeeId");

                }
                String providerNpi = map.get("providerNPIId");
                String providerNames = map.get("providerName");
                empId.setText(userId);
                compName.setText(comNames);
                userEmail.setText(emailAddress);
                providerNPI.setText(providerNpi);
                providerName.setText(providerNames);
                if(role == "user"){
                    Log.d("inside","inside");
                    providerNPI.setVisibility(View.INVISIBLE);
                    providerName.setVisibility(View.INVISIBLE);
                    providerNpiLabel.setVisibility(View.INVISIBLE);
                    providerNameLabel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

acceptBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Log.d("user.get",user.getUserName());
        user.setAuth("1");
        if(user.getRole().matches("admin")){
            acceptedCompany(user);
        } else if(user.getRole().matches("user")){
            acceptedEmployee(user);
        }

    }
});

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setAuth("2");

                if(user.getRole().matches("admin")){
                    pendingCompany(user);
                } else if(user.getRole().matches("user")){
                    pendingEmployee(user);
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setAuth("3");

                if(user.getRole().matches("admin")){
                    deleteCompany(user);
                } else if(user.getRole().matches("user")){
                    deleteEmployee(user);
                }
            }
        });
    }

    public void acceptedCompany(User user) {
        user.setAuth("1");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.acceptedCompany(user);
        if (result) {
            Toast.makeText(getActivity(), "Company has been accepted by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while accepted the company, please try again!");
        }

    }
    public void pendingCompany(User user) {
        user.setAuth("2");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.pendingCompany(user);
        if (result) {
            Toast.makeText(getActivity(), "Company has been pending by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while pending the company, please try again!");
        }
    }
    public void deleteCompany(User user) {
        user.setAuth("3");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.deleteCompany(user);
        if (result) {
            Toast.makeText(getActivity(), "Company has been deleted by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }
    public void acceptedEmployee(User user) {
        user.setAuth("1");
        final UserDao userDao = new UserDao();
        boolean result = userDao.acceptedEmployee(user);
        if (result) {
            Log.d(TAG, "Company canceled successfully!");
            Toast.makeText(getActivity(), "Company has been accepted by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while accepted the company, please try again!");
        }

    }

    public void pendingEmployee(User user) {
        user.setAuth("2");
        final UserDao userDao = new UserDao();
        boolean result = userDao.pendingEmployee(user);
        if (result) {
            Toast.makeText(getActivity(), "Company has been pending by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while pending the company, please try again!");
        }
    }

    public void deleteEmployee(User user) {
        user.setAuth("3");
        final UserDao userDao = new UserDao();
        boolean result = userDao.deleteEmployee(user);
        if (result) {
            Toast.makeText(getActivity(), "Company has been deleted by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }


    public ViewUserDetails getActivity() {
        return this;
    }
}
