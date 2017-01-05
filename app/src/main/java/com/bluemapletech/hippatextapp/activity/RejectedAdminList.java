package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bluemapletech.hippatextapp.R;
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

public class RejectedAdminList extends AppCompatActivity {
    private static final String TAG = RejectedAdminList.class.getCanonicalName();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private String loggedInCompanyValue;
    private String loggedINEmail;
    private ListView iv;
    public static final String toEmail = "toEmail";
    public static final String fromEmail = "fromEmail";
    public static final String sendId = "sendId";
    public static final String notificationId = "notificationId";
    public static final String firstName = "firstName";
    public static final String lastName = "lastName";
    public static final String userEmails = "userEmails";
    public static final String userAuth = "userAuth";
    List<User> userObj = new ArrayList<User>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_admin_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        checkUserExistence();
        fireBaseDatabase = FirebaseDatabase.getInstance();
        final User user = new User();
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("userDetails");
        iv = (ListView) findViewById(R.id.all_users);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = new User();
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setEmpId(snapshot.child("employeeId").getValue(String.class));
                    user.setCompanyName(snapshot.child("companyName").getValue(String.class));
                    user.setFirstName(snapshot.child("firstName").getValue(String.class));
                    user.setLastName(snapshot.child("lastName").getValue(String.class));
                    user.setProviderNPIId(snapshot.child("providerNPIId").getValue(String.class));
                    Log.d("adminDetails","adminDetails"+user.getCompanyName());
                    if(!user.getLastName().matches("") && !user.getFirstName().matches("")){
                        String[] valueuserName = user.getUserName().split("@");
                        user.setFirstName(valueuserName[0]);
                    }
                    if (user.getRole().matches("admin") && user.getAuth().matches("1") && !loggedINEmail.matches(user.getUserName())&& loggedInCompanyValue.matches(user.getCompanyName())) {
                        userObj.add(user);
                        Log.d("adminDetails","adminDetails"+user);
                    }
                    iv.setAdapter(new RejectedAdminList.PageAdminBaseAdaptersAdmin(getActivity(), userObj,loggedINEmail));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void checkUserExistence() {
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser logged = firebaseAuth.getCurrentUser();
        String reArrangeEmail = logged.getEmail().replace(".", "-");
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedINEmail = (String) dataSnapshot.child("emailAddress").getValue();
                loggedInCompanyValue = (String) dataSnapshot.child("companyName").getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private class PageAdminBaseAdaptersAdmin extends BaseAdapter {

        List<User> userInfo = new ArrayList<User>();
        LayoutInflater inflater;
        Context context;
        private String loginMail;
        public PageAdminBaseAdaptersAdmin(Context context, List<User> user , String loginMail) {
            this.context = context;
            this.userInfo = user;
            this.loginMail = loginMail;
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

            RejectedAdminList.PageAdminBaseAdaptersAdmin.MyViewHolder mViewHolder  = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.rejected_employee_custom_layout, parent, false);
                mViewHolder = new RejectedAdminList.PageAdminBaseAdaptersAdmin.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (RejectedAdminList.PageAdminBaseAdaptersAdmin.MyViewHolder) convertView.getTag();
            }

            final User info = getItem(position);
            mViewHolder.fieldName.setText(info.getUserName());
            mViewHolder.fieldId.setText(info.getProviderNPIId());
            convertView.findViewById(R.id.chat_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(toEmail, userInfo.get(position).getUserName());
                    intent.putExtra(fromEmail, loginMail);
                    intent.putExtra(sendId, userInfo.get(position).getSenderId());
                    intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                    intent.putExtra(firstName, userInfo.get(position).getFirstName());
                    intent.putExtra(lastName, userInfo.get(position).getLastName());
                    context.startActivity(intent);

                }
            });
            convertView.findViewById(R.id.user_id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewUserDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(userEmails, userInfo.get(position).getUserName());
                    intent.putExtra(userAuth, userInfo.get(position).getAuth());
                    context.startActivity(intent);
                }
            });
            return convertView;
        }


        private class MyViewHolder {
            private TextView fieldName;
            private TextView fieldId;
            public MyViewHolder(View item) {
                fieldName = (TextView) item.findViewById(R.id.user_mail);
                fieldId = (TextView) item.findViewById(R.id.user_id);
            }
        }
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
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
    }

    public RejectedAdminList getActivity() {
        return this;
    }



    }
