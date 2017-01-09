package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.model.UserDetailDto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RejectedEmployeeListActivity extends AppCompatActivity {
    private static final String TAG = ListOfAdminActivity.class.getCanonicalName();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private String loggedInCompanyValue;
    private String loggedINEmail;
    private ListView iv;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
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
        setContentView(R.layout.activity_rejected_employee_list);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Employee List");
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
                    Log.d("adminDetails","adminDetails"+user.getCompanyName());
                    if(!user.getLastName().matches("") && !user.getFirstName().matches("")){
                        String[] valueuserName = user.getUserName().split("@");
                        user.setFirstName(valueuserName[0]);
                    }
                    if (user.getRole().matches("user") && user.getAuth().matches("3") && !loggedINEmail.matches(user.getUserName())&& loggedInCompanyValue.matches(user.getCompanyName())) {
                        userObj.add(user);
                        Log.d("adminDetails","adminDetails"+user);
                    }
                    UserDetailDto userDto = new UserDetailDto();
                    pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
                    userDto.setLoggedINChatPin(pref.getString("chatPin", ""));
                    iv.setAdapter(new RejectedEmployeeListActivity.PageAdminBaseAdaptersEmployee(getActivity(), userObj,loggedINEmail,userDto));
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
    private class PageAdminBaseAdaptersEmployee extends BaseAdapter {

        List<User> userInfo = new ArrayList<User>();
        LayoutInflater inflater;
        Context context;
        private String loginMail;
        UserDetailDto InfouserDto = new UserDetailDto();
        public PageAdminBaseAdaptersEmployee(Context context, List<User> user , String loginMail,UserDetailDto userDto) {
            this.context = context;
            this.userInfo = user;
            this.loginMail = loginMail;
            this.InfouserDto = userDto;
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

            RejectedEmployeeListActivity.PageAdminBaseAdaptersEmployee.MyViewHolder mViewHolder  = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.rejected_employee_custom_layout, parent, false);
                mViewHolder = new RejectedEmployeeListActivity.PageAdminBaseAdaptersEmployee.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (RejectedEmployeeListActivity.PageAdminBaseAdaptersEmployee.MyViewHolder) convertView.getTag();
            }

            final User info = getItem(position);
            mViewHolder.fieldName.setText(info.getUserName());
            mViewHolder.fieldId.setText(info.getEmpId());
            convertView.findViewById(R.id.chat_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Security check");
                    final EditText chatPinn = new EditText(context);
                    chatPinn.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    chatPinn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    chatPinn.setHint("Enter your chat pin");
                    alert.setView(chatPinn);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String srt = chatPinn.getEditableText().toString();
                            if (srt.matches(InfouserDto.getLoggedINChatPin())) {
                                Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(toEmail, userInfo.get(position).getUserName());
                                intent.putExtra(fromEmail, loginMail);
                                intent.putExtra(sendId, userInfo.get(position).getSenderId());
                                intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                                intent.putExtra(firstName, userInfo.get(position).getFirstName());
                                intent.putExtra(lastName, userInfo.get(position).getLastName());
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Chat pin is not match!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();


                }
            });
            convertView.findViewById(R.id.user_id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewUserAdminDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public RejectedEmployeeListActivity getActivity() {
        return this;
    }
}
