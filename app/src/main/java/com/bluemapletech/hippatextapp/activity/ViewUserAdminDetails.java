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
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewUserAdminDetails extends AppCompatActivity {
    private static final String TAG = ViewUserAdminDetails.class.getCanonicalName();
    String empMailId = null;
    String userId,loginsenderId;
    String adminMailId = null;
    String reArrangeEmail;
    String userAuths;
    HashMap<String,String> isUserChecking = new HashMap<String, String>();
    String userEmails;
    String role,roleValue,loginChatPin;
    private ListView iv;
    private FirebaseAuth firebaseAuth;
    List<User> userObj;

    SharedPreferences preflogin;
    SharedPreferences.Editor editorlogin;
    String isOnline;

    private FirebaseDatabase fireBaseDatabase;
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
        loginChatPin= pref.getString("chatPin", "");
        loginsenderId = pref.getString("senderId","");
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
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        checkUserDelete();
        reArrangeEmail = userEmails.replace(".", "-");
        databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                Map<String, String> map = (Map) dataSnapshot.getValue();
                if(map !=null) {
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
                    user1.setEmpId(map.get("employeeId"));
                    Log.d(TAG, "user get empaid" + user1.getEmpId());
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
                    if (user1.getAuth().matches("3")) {
                        acceptBtn.setText("Accept");
                        acceptBtn.setBackgroundColor(getResources().getColor(R.color.accept_btn));
                    }

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
                }else{
                    showAlert();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reArrangeDeleUserMail = user.getUserName().replace("-", ".");
                if (isUserChecking.get(reArrangeDeleUserMail) == null) {
                    if (userAuths.matches("3")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        //alert.setTitle("");
                        Log.d(TAG, "user get empaid" + user1.getEmpId());
                        if (user1.getRole().matches("admin")) {
                            alert.setMessage("Do you want to accept  '" + user1.getCompanyName() + "' Company");
                        } else if (user1.getRole().matches("user")) {
                            alert.setMessage("Do you want to accept  '" + user1.getEmpId() + "' Emplpoyee!");
                        }
                        alert.setCancelable(false);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                acceptedCompany(user1);
                            }
                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();

                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Security check");
                        final EditText chatPinn = new EditText(getActivity());
                        chatPinn.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        chatPinn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        chatPinn.setHint("Enter your chat pin");
                        alert.setView(chatPinn);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String srt = chatPinn.getEditableText().toString();
                                if (srt.matches(loginChatPin)) {
                                    firebaseAuth = FirebaseAuth.getInstance();
                                    FirebaseUser logged = firebaseAuth.getCurrentUser();
                                    Log.d(TAG, "Logged in user information's: " + logged.getEmail());
                                    Intent intent = new Intent(getActivity(), ChatEmployeeActivity.class);
                                    intent.putExtra(toEmail, user1.getUserName());
                                    intent.putExtra(fromEmail, logged.getEmail());
                                    intent.putExtra(sendId, loginsenderId);
                                    intent.putExtra(notificationId, user1.getPushNotificationId());
                                    intent.putExtra(firstName, user1.getFirstName());
                                    intent.putExtra(lastName, user1.getLastName());
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getActivity(), "Chat pin is not match!", Toast.LENGTH_LONG).show();
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
                }else{
                    showAlert();
                }
                //
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
            if(user1.getRole().matches("admin")){
                Toast.makeText(getActivity(), "Company is accepted successfully!", Toast.LENGTH_LONG).show();
            }else if(user1.getRole().matches("user")){
                Toast.makeText(getActivity(), "Employee is accepted successfully!", Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(getActivity(), "Company is accepted successfully!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while accepted the company, please try again!");
        }

    }
    public void showAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        // alert.setTitle("");
        alert.setMessage("Error this account please try again!");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(roleValue.matches("admin")){
                    startActivity(new Intent(getActivity(), AdminHomeActivity.class));
                }else if(roleValue.matches("root")){
                    startActivity(new Intent(getActivity(), RootHomeActivity.class));
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
  /*  private class viewUserAdapter extends BaseAdapter {

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
    }*/

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

    public void checkUserDelete(){
        databaseRef = firebaseDatabaseRef.getReference().child("userDetails");
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Map<String, String> dataValue = (Map)dataSnapshot.getValue();
                String deleteEmail = dataValue.get("emailAddress");
                isUserChecking.put(deleteEmail,deleteEmail);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public ViewUserAdminDetails getActivity() {
        return this;
    }
}
