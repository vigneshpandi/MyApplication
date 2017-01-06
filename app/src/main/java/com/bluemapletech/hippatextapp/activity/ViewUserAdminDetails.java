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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageAdminBaseAdapter;
import com.bluemapletech.hippatextapp.adapter.PageBaseAdapter;
import com.bluemapletech.hippatextapp.dao.CompanyDao;
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

public class ViewUserAdminDetails extends AppCompatActivity {
    private static final String TAG = ViewUserDetails.class.getCanonicalName();
    String empMailId = null;
    String userId;
    String adminMailId = null;
    String reArrangeEmail;
    String userAuths;
    String userEmails;
    String role,roleValue;
    private ListView iv;
    private FirebaseAuth firebaseAuth;
    List<User> userObj;
    User user = new User();
    User user1 = new User();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    public static final String toEmail = "toEmail";
    public static final String fromEmail = "fromEmail";
    public static final String sendId = "sendId";
    public static final String notificationId = "notificationId";
    public static final String firstName = "firstName";
    public static final String lastName = "lastName";
    private TextView userEmail, compName, empId, providerName, providerNPI, providerNpiLabel, providerNameLabel;
    private Button acceptBtn, pendingBtn, deleteBtn,chatBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_admin_details);
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        roleValue =  pref.getString("role", "");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }
    public void init() {
        acceptBtn = (Button) findViewById(R.id.accept_user_btn);
        userAuths =  getIntent().getStringExtra(RejectedAdminList.userAuth);
        userEmails = getIntent().getStringExtra(RejectedAdminList.userEmails);

        userEmail = (TextView) findViewById(R.id.user_email);
        compName = (TextView) findViewById(R.id.comp_name);
        empId = (TextView) findViewById(R.id.employee_id);
        providerNPI = (TextView) findViewById(R.id.provider_npi_text);
        providerName = (TextView) findViewById(R.id.provider_name_text);
        providerNpiLabel = (TextView) findViewById(R.id.provider_npi);
        providerNameLabel = (TextView) findViewById(R.id.provider_name);

        Log.d(TAG,"userAuths..."+userAuths);
        /*if(userAuths.matches("1")&& roleValue.matches("admin")){
            Log.d(TAG,"inside admin rejected list....");
            acceptBtn.setText("Accept");
            acceptBtn.setBackgroundColor(getResources().getColor(R.color.accept_btn));
        }*/
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        reArrangeEmail = userEmails.replace(".", "-");
        databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                Map<String, String> map = (Map) dataSnapshot.getValue();
                user1.setUserName(map.get("emailAddress"));
                user1.setPushNotificationId(map.get("pushNotificationId"));
                user1.setSenderId(map.get("senderId"));
                user1.setFirstName(map.get("firstName"));
                user1.setLastName(map.get("lastName"));
                user1.setCompanyName(map.get("companyName"));
                user1.setProviderNPIId( map.get("providerNPIId"));
                user1.setProviderName(map.get("providerName"));
                user1.setRole( map.get("role"));
                user1.setAuth(map.get("auth"));
                user1.setEmpId(map.get("employeeId"));
                user1.setTINorEIN(map.get("companyCINNumber"));
                if(user1.getRole().equals("user")){
                    providerNPI.setVisibility(View.INVISIBLE);
                    providerName.setVisibility(View.INVISIBLE);
                    providerNpiLabel.setVisibility(View.INVISIBLE);
                    providerNameLabel.setVisibility(View.INVISIBLE);
                }
               /*
                if (adminMailId != null) {
                    userId = map.get("companyCINNumber");
                } else if(empMailId!=null) {
                    userId = map.get("employeeId");

                }*/

                if (user1.getFirstName().matches("") && user1.getLastName().matches("")) {
                    String[] valueuserName = user1.getUserName().split("@");
                    user1.setFirstName(valueuserName[0]);
                }
                Log.d(TAG,"userAuth...."+user1.getAuth());
                if(user1.getAuth().matches("3")){
                    Log.d(TAG,"zzzzzzzzzz");
                  acceptBtn.setText("Accept");
                    acceptBtn.setBackgroundColor(getResources().getColor(R.color.accept_btn));
                }

// set the value for textFields
                if(user1.getEmpId()!=null){
                    userId = user1.getEmpId();
                }else if(user1.getTINorEIN()!=null){
                    userId = user1.getTINorEIN();
                }
                userEmail.setText(user1.getUserName());
                empId.setText(userId);
                compName.setText(user1.getCompanyName());
                providerNPI.setText(user1.getProviderNPIId());
                providerName.setText(user1.getProviderName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userAuths.matches("3")){
                    acceptedCompany(user1);

                }else{
                    firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser logged = firebaseAuth.getCurrentUser();
                    Log.d(TAG, "Logged in user information's: " + logged.getEmail());
                    Intent intent = new Intent(getActivity(), ChatEmployeeActivity.class);
                    intent.putExtra(toEmail, user1.getUserName());
                    intent.putExtra(fromEmail, logged.getEmail());
                    intent.putExtra(sendId, user1.getSenderId());
                    intent.putExtra(notificationId, user1.getPushNotificationId());
                    intent.putExtra(firstName, user1.getFirstName());
                    intent.putExtra(lastName, user1.getLastName());
                    startActivity(intent);
                }
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG,"roleValue....roleValue...."+roleValue);
        if(user1.getRole().equals("user")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageAdmin();
                    return true;
            }
        }
        if(user1.getRole().equals("admin") && roleValue.matches("root")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageRoot();
                    return true;
            }
        }
        if(user1.getRole().equals("admin") && roleValue.matches("admin")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageAdmins();
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void acceptedCompany(User user) {
        user.setAuth("1");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.acceptedCompany(user);
        if (result) {
            startActivity(new Intent(getActivity(),AdminHomeActivity.class));
            Toast.makeText(getActivity(), "Company has been accepted by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while accepted the company, please try again!");
        }

    }

    private void backPageAdmin() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
    }

    private void backPageRoot() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),RootHomeActivity.class));
    }
    private void backPageAdmins() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
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

            viewUserAdapter.MyViewHolder mViewHolder  = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_of_user_under_admin, parent, false);
                mViewHolder = new viewUserAdapter.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (viewUserAdapter.MyViewHolder) convertView.getTag();
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
    public ViewUserAdminDetails getActivity() {
        return this;
    }
}