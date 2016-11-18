package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageAdminBaseAdapter;
import com.bluemapletech.hippatextapp.adapter.PageBaseAdapter;
import com.bluemapletech.hippatextapp.dao.CompanyDao;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewUserDetails extends AppCompatActivity {
    private static final String TAG = ViewUserDetails.class.getCanonicalName();
    String empMailId = null;
    String userId;
    String adminMailId = null;
    String reArrangeEmail;
    String userAuths;
    String role;
    private ListView iv;
    private FirebaseAuth firebaseAuth;
    List<User> userObj = new ArrayList<User>();
    User user = new User();
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private TextView userEmail, compName, empId, providerName, providerNPI, providerNpiLabel, providerNameLabel;
    private Button acceptBtn, pendingBtn, deleteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }
    public void init() {
        empMailId = getIntent().getStringExtra(PageAdminBaseAdapter.userEmails);
        adminMailId = getIntent().getStringExtra(PageBaseAdapter.userEmail);
        userAuths = getIntent().getStringExtra(PageBaseAdapter.userAuth);
        acceptBtn = (Button) findViewById(R.id.accept_user_btn);
        pendingBtn = (Button) findViewById(R.id.pending_user_btn);
        deleteBtn = (Button) findViewById(R.id.delete_user_btn);


        acceptBtn.setVisibility(View.INVISIBLE);
        pendingBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setVisibility(View.INVISIBLE);

        if(userAuths.matches("0")){
            acceptBtn.setVisibility(View.VISIBLE);
            pendingBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
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
            reArrangeEmail = adminMailId.replace(".", "-");
            user.setUserName(reArrangeEmail);
        } else if(empMailId!=null){
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
                String emailAddress = map.get("emailAddress");
                String providerNpi = map.get("providerNPIId");
                String providerNames = map.get("providerName");
                role =  map.get("role");
                String auth = map.get("auth");
                if(role.equals("user")){
                    providerNPI.setVisibility(View.INVISIBLE);
                    providerName.setVisibility(View.INVISIBLE);
                    providerNpiLabel.setVisibility(View.INVISIBLE);
                    providerNameLabel.setVisibility(View.INVISIBLE);
                }
                if (adminMailId != null) {
                    userId = map.get("companyCINNumber");
                } else if(empMailId!=null) {
                    userId = map.get("employeeId");

                }
                if(userAuths.matches("1") && role.matches("admin")){
                    acceptBtn.setVisibility(View.VISIBLE);
                   Button btns = (Button) findViewById(R.id.accept_user_btn);
                    btns.setText("Employee List");
                }
                user.setAuth(auth);
                user.setCompanyName(comNames);
                user.setRole(role);
                empId.setText(userId);
                compName.setText(comNames);
                userEmail.setText(emailAddress);
                providerNPI.setText(providerNpi);
                providerName.setText(providerNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

acceptBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(user.getRole().matches("admin")&& !user.getAuth().matches("1")){
            user.setAuth("1");
            acceptedCompany(user);
        } else if(user.getRole().matches("user")&& !user.getAuth().matches("1")){
            user.setAuth("1");
            acceptedEmployee(user);
        } else if(user.getRole().matches("admin")&& user.getAuth().matches("1")){
           getUserDetails(user.getCompanyName());
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

    private void getUserDetails(final String companyName) {
        iv = (ListView) findViewById(R.id.list_of_user);
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        final User user = new User();
        DatabaseReference dataReference = firebaseDatabaseRef.getReference().child("userDetails");
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = new User();

                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setCompanyName(snapshot.child("companyName").getValue(String.class));
                    if (user.getRole().matches("user") && user.getAuth().matches("1") && user.getCompanyName().matches(companyName)) {
                        userObj.add(user);
                        Log.d("adminDetails","adminDetails"+user);
                    }
                    iv.setAdapter(new viewUserAdapter(getActivity(), userObj));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(role.equals("user")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageAdmin();
                    return true;
            }
        }
        if(role.equals("admin")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageRoot();
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void backPageAdmin() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
    }

    private void backPageRoot() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),RootHomeActivity.class));
    }
    private class viewUserAdapter extends BaseAdapter {

        List<User> userInfo = new ArrayList<User>();
        LayoutInflater inflater;
        Context context;
        public viewUserAdapter(Context context, List<User> user) {
            this.context = context;
            this.userInfo = user;
            inflater = LayoutInflater.from(this.context);
        }


        public int getCount() {
            return userInfo.size();
        }

        @Override
        public User getItem(int position) {
            return userInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewUserDetails.viewUserAdapter.MyViewHolder mViewHolder  = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_of_user_under_admin, parent, false);
                mViewHolder = new ViewUserDetails.viewUserAdapter.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewUserDetails.viewUserAdapter.MyViewHolder) convertView.getTag();
            }

            final User info = getItem(position);

                mViewHolder.mailId.setText(info.getUserName());


            return convertView;
        }


        private class MyViewHolder {
            private TextView mailId;
            public MyViewHolder(View item) {
                mailId = (TextView) item.findViewById(R.id.user_mail);
            }
        }
    }
    public ViewUserDetails getActivity() {
        return this;
    }
}
