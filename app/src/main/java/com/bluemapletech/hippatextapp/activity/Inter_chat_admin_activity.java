package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
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
import java.util.HashMap;
import java.util.List;

public class Inter_chat_admin_activity extends AppCompatActivity {
    private static final String TAG = ListOfRoots.class.getCanonicalName();
    private FirebaseDatabase fireBaseDatabase;
    private String loggedINEmail;
    private ListView iv;
    private ArrayList<String> data = new ArrayList<>();
    SharedPreferences pref;
    String loginMail;
    SharedPreferences.Editor editor;
    private String rootValue,loginRole,loginAuth,not_acp_user;
    private String loginCompanyName,isOnline,loginsenderId;
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
    private FirebaseAuth firebaseAuth;
    public static final String toEmail = "toEmail";
    public static final String fromEmail = "fromEmail";
    public static final String sendId = "sendId";
    public static final String notificationId = "notificationId";
    public static final String firstName = "firstName";
    public static final String lastName = "lastName";
    public static final String role = "userRole";
    public static final String userEmails = "userEmails";
    UserDetailDto userDetailDto = new UserDetailDto();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_chat_admin_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        iv = (ListView) findViewById(R.id.all_user);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Inter Chat List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView header = (TextView) findViewById(R.id.header);
            header.setText("Inter Chat List");
        }
//login user details
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginMail =  pref.getString("loginMail", "");
        String chatPin =  pref.getString("chatPin", "");
        loginRole = pref.getString("role","");
        loginAuth = pref.getString("auth","");
        isOnline =  pref.getString("isOnline", "");
        loginCompanyName = pref.getString("loginCompanyName","");
        loginsenderId = pref.getString("senderId","");
        loggedINEmail = loginMail;
        userDetailDto.setLoggedINChatPin(chatPin);
        userDetailDto.setLoggedINEmail(loginMail);
        userDetailDto.setLoginSenderId(loginsenderId);
        fireBaseDatabase = FirebaseDatabase.getInstance();
        final User user = new User();
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("userDetails");

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                List<User> userObj = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = new User();
                    user.setLastName(snapshot.child("lastName").getValue(String.class));
                    user.setFirstName(snapshot.child("firstName").getValue(String.class));
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setCompanyName(snapshot.child("companyName").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setSenderId(snapshot.child("senderId").getValue(String.class));
                    user.setEmpId(snapshot.child("employeeId").getValue(String.class));
                    user.setPushNotificationId(snapshot.child("pushNotificationId").getValue(String.class));
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    user.setProviderNPIId(snapshot.child("providerNPIId").getValue(String.class));
                    if (user.getFirstName()==null && user.getLastName()==null) {
                        String[] valueuserName = user.getUserName().split("@");
                        user.setFirstName(valueuserName[0]);
                    }
                    if (user.getAuth().matches("1") && !loggedINEmail.matches(user.getUserName()) && !loginCompanyName.matches(user.getCompanyName())) {
                        userObj.add(user);
                    }

                    iv.setAdapter(new adminInterChatBaseAdapter(getActivity(), userObj,userDetailDto));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }





    public class adminInterChatBaseAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        private String fromMAil;
        private AlertDialog.Builder alertDialog;
        List<User> userInfo = new ArrayList<User>();
        UserDetailDto userDetailDtos = new UserDetailDto();
        public adminInterChatBaseAdapter(Context context, List<User> user,UserDetailDto userDetailDto) {
            this.context = context;
            this.userInfo = user;
            this.fromMAil = fromEmail;
            this.userDetailDtos = userDetailDto;
            inflater = LayoutInflater.from(this.context);
        }

        @Override
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
        public View getView(final int position, View convertView, final ViewGroup parent) {

            adminInterChatBaseAdapter.MyViewHolder mViewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.inter_chat_admin_custom_layout, parent, false);
                mViewHolder = new adminInterChatBaseAdapter.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (adminInterChatBaseAdapter.MyViewHolder) convertView.getTag();
            }


            final User info = getItem(position);

            if(info.getRole().matches("user")) {
                mViewHolder.fieldId.setText(info.getEmpId());
            }else if(info.getRole().matches("admin")){
                mViewHolder.fieldId.setText(info.getProviderNPIId());
            } else if(info.getRole().matches("root")){
                mViewHolder.fieldId.setText("");
            }
            mViewHolder.fieldName.setText(info.getUserName());

            convertView.findViewById(R.id.layout_field_id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewUserDetailTabActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(userEmails, userInfo.get(position).getUserName());
                    context.startActivity(intent);
                }
            });

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
                            if (srt.matches(userDetailDtos.getLoggedINChatPin())) {
                                Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(toEmail, userInfo.get(position).getUserName());
                                intent.putExtra(fromEmail, userDetailDtos.getLoggedINEmail());
                                intent.putExtra(sendId, userDetailDtos.getLoginSenderId());
                                intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                                intent.putExtra(firstName, userInfo.get(position).getFirstName());
                                intent.putExtra(lastName, userInfo.get(position).getLastName());
                                intent.putExtra(role,userInfo.get(position).getRole());
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, " Sorry! Chat pin does not match!", Toast.LENGTH_LONG).show();
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


            return convertView;
        }

        private class MyViewHolder {
            private TextView fieldId, fieldName;

            public MyViewHolder(View item) {
                fieldId = (TextView) item.findViewById(R.id.layout_field_id);
                fieldName = (TextView) item.findViewById(R.id.layout_field_name);
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startActivity(new Intent(getActivity(), AdminHomeActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onPause()
    {
        if(isOnline.matches("true")) {
            fireBaseDatabase = FirebaseDatabase.getInstance();
            String reArrangeEmail = loginMail.replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            dataReferences.removeValue();
        }
        super.onPause();
        //Do whatever you want to do when the application stops.
    }


    @Override
    protected  void onResume(){
        if(isOnline.matches("true")) {
            HashMap<String, Object> onlineReenter = new HashMap<>();
            fireBaseDatabase = FirebaseDatabase.getInstance();
            String reArrangeEmail = loginMail.replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            onlineReenter.put("onlineUser", loginMail);
            dataReferences.setValue(onlineReenter);
        }
        super.onResume();
    }
    public Inter_chat_admin_activity getActivity() {
        return this;
    }
}

