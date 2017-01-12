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
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.MailSender;
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

public class ViewUserDetails extends AppCompatActivity {
    private static final String TAG = ViewUserDetails.class.getCanonicalName();
    String empMailId = null;
    String userId;
    String adminMailId = null;
    String reArrangeEmail;
    String userAuths,loginsenderId;
    SharedPreferences preflogin;
    SharedPreferences.Editor editorlogin;
    String isOnline;
    String role,roleValue,loginChatPin;
    private ListView iv;
    private FirebaseDatabase fireBaseDatabase;
    private FirebaseAuth firebaseAuth;
    HashMap<String,String> isUserChecking = new HashMap<String, String>();
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
    private AlertDialog.Builder alertDialog;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        roleValue =  pref.getString("role", "");
        loginChatPin= pref.getString("chatPin", "");
        loginsenderId = pref.getString("senderId","");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkUserDelete();
        init();
    }
    public void init() {
        empMailId = getIntent().getStringExtra(PageAdminBaseAdapter.userEmails);
        adminMailId = getIntent().getStringExtra(PageBaseAdapter.userEmail);
        userAuths = getIntent().getStringExtra(PageBaseAdapter.userAuth);
        acceptBtn = (Button) findViewById(R.id.accept_user_btn);
        pendingBtn = (Button) findViewById(R.id.pending_user_btn);
        deleteBtn = (Button) findViewById(R.id.delete_user_btn);
        chatBtn  = (Button) findViewById(R.id.chat_user_btn);

        acceptBtn.setVisibility(View.INVISIBLE);
        pendingBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setVisibility(View.INVISIBLE);
        chatBtn.setVisibility(View.INVISIBLE);
        if(userAuths.matches("0")){
            acceptBtn.setVisibility(View.VISIBLE);
            pendingBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
            chatBtn.setVisibility(View.VISIBLE);
        }
        if(userAuths.matches("2")){
            acceptBtn.setVisibility(View.VISIBLE);
            acceptBtn.setText("Chat");
            acceptBtn.setBackgroundColor(getResources().getColor(R.color.color_primary));

        }
        if(userAuths.matches("3")){
            acceptBtn.setVisibility(View.VISIBLE);

        }
        userEmail = (TextView) findViewById(R.id.user_email);
        compName = (TextView) findViewById(R.id.comp_name);
        empId = (TextView) findViewById(R.id.employee_id);
        providerNPI = (TextView) findViewById(R.id.provider_npi_text);
        providerName = (TextView) findViewById(R.id.provider_name_text);
        providerNpiLabel = (TextView) findViewById(R.id.provider_npi);
        providerNameLabel = (TextView) findViewById(R.id.provider_name);


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
                    Map<String, String> map = (Map) dataSnapshot.getValue();
                if(map!=null) {
                    user1.setUserName(map.get("emailAddress"));
                    user1.setPushNotificationId(map.get("pushNotificationId"));
                    user1.setSenderId(map.get("senderId"));
                    user1.setFirstName(map.get("firstName"));
                    user1.setLastName(map.get("lastName"));
                    user1.setEmpId(map.get("employeeId"));
                    if (user1.getFirstName().matches("") && user1.getLastName().matches("")) {
                        String[] valueuserName = user1.getUserName().split("@");
                        user1.setFirstName(valueuserName[0]);
                    }
                    String comNames = map.get("companyName");
                    String emailAddress = map.get("emailAddress");
                    String providerNpi = map.get("providerNPIId");
                    String providerNames = map.get("providerName");
                    role = map.get("role");
                    String auth = map.get("auth");
                    if (role.equals("user")) {
                        providerNPI.setVisibility(View.INVISIBLE);
                        providerName.setVisibility(View.INVISIBLE);
                        providerNpiLabel.setVisibility(View.INVISIBLE);
                        providerNameLabel.setVisibility(View.INVISIBLE);
                    }
                    if (adminMailId != null) {
                        userId = map.get("companyCINNumber");
                    } else if (empMailId != null) {
                        userId = map.get("employeeId");

                    }
                    if (userAuths.matches("1") && role.matches("admin")) {
                        acceptBtn.setVisibility(View.VISIBLE);
                        Button btns = (Button) findViewById(R.id.accept_user_btn);
                        btns.setText("Employee List");
                        btns.setBackgroundColor(getResources().getColor(R.color.navigationBarColor));
                    }
                    user.setAuth(auth);
                    user.setCompanyName(comNames);
                    user.setRole(role);
                    empId.setText(userId);
                    compName.setText(comNames);
                    userEmail.setText(emailAddress);
                    providerNPI.setText(providerNpi);
                    providerName.setText(providerNames);
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
                if(!userAuths.matches("2")){
                    if(user.getRole().matches("admin")&& !user.getAuth().matches("1")){
                        user.setAuth("1");
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        // alert.setTitle("");
                        alert.setMessage("Do you want to accept '"+user.getCompanyName()+"' Company!");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                acceptedCompany(user);
                            }
                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();

                    } else if(user.getRole().matches("user")&& !user.getAuth().matches("1")){
                        user.setAuth("1");
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        //alert.setTitle("");
                        alert.setMessage("Do you want to accept '"+user1.getEmpId()+"' employee!");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                acceptedEmployee(user);
                            }
                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                    } else if(user.getRole().matches("admin")&& user.getAuth().matches("1")){

                        getUserDetails(user.getCompanyName());
                    }

                }else {
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
                                intent.putExtra(toEmail,user1.getUserName());
                                intent.putExtra(fromEmail, logged.getEmail());
                                intent.putExtra(sendId, loginsenderId);
                                intent.putExtra(notificationId,user1.getPushNotificationId());
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
            }


        });

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setAuth("2");
                String reArrangeDeleUserMail = user.getUserName().replace("-", ".");
                if (isUserChecking.get(reArrangeDeleUserMail) == null) {
                    if (user.getRole().matches("admin")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        // alert.setTitle("");
                        alert.setMessage("Do you want to put pending '" + user.getCompanyName() + "' Company!");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pendingCompany(user);
                            }
                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                    } else if (user.getRole().matches("user")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        //alert.setTitle("");
                        alert.setMessage("Do you want to put pending '" + user1.getEmpId() + "' employee!");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pendingEmployee(user);
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

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setAuth("3");
                String reArrangeDeleUserMail = user.getUserName().replace("-", ".");
                if (isUserChecking.get(reArrangeDeleUserMail) == null) {
                    if (user.getRole().matches("admin")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        // alert.setTitle("");
                        alert.setMessage("Do you want to reject '" + user.getCompanyName() + "' Company!");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteCompany(user);
                            }
                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                    } else if (user.getRole().matches("user")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        // alert.setTitle("");
                        alert.setMessage("Do you want to reject '" + user1.getEmpId() + "' employee!");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteEmployee(user);
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

                ///
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reArrangeDeleUserMail = user.getUserName().replace("-", ".");
                if (isUserChecking.get(reArrangeDeleUserMail) == null) {
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
                }else{
                    showAlert();
                }

                //
            }
        });

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
                Log.d(TAG,"onChildRemoveddataSnapshot"+dataSnapshot);
                Map<String, String> dataValue = (Map)dataSnapshot.getValue();
                Log.d(TAG,"snapshot.child.child1111111"+dataValue.get("emailAddress"));
                String deleteEmail = dataValue.get("emailAddress");
                isUserChecking.put(deleteEmail,deleteEmail);
                Log.d("isUserChecking","isUserChecking"+isUserChecking.get(deleteEmail));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
                userObj  = new ArrayList<User>();
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
    public void acceptedCompany(User user) {
        user.setAuth("1");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.acceptedCompany(user);
        Log.d(TAG,"company mail id.."+user.getUserName());
        if (result) {
            try {
                //  new MyAsyncClass().execute();
                MailSender runners = new MailSender();
                String  value = "Company is accepted by root.";
                runners.execute("Company is accepted successfully!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            startActivity(new Intent(getActivity(), RootHomeActivity.class));
            Toast.makeText(getActivity(), "Company is accepted successfully!", Toast.LENGTH_LONG).show();

        } else {
            Log.d(TAG, "Error while accepted the company, please try again!");
        }

    }
    public void pendingCompany(User user) {
        user.setAuth("2");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.pendingCompany(user);
        Log.d(TAG,"company pending employee id.."+user.getUserName());
        if (result) {
            try {
                //  new MyAsyncClass().execute();
                MailSender runners = new MailSender();
                String  value = "Company is put to pendind .";
                runners.execute("Company is requested to pending!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Toast.makeText(getActivity(), "Company is requested to pending!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), RootHomeActivity.class));

        } else {
            Log.d(TAG, "Error while pending the company, please try again!");
        }
    }
    public void deleteCompany(User user) {
        user.setAuth("3");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.deleteCompany(user);
        Log.d(TAG,"deleted company"+user.getUserName());
        if (result) {
            try {
                //  new MyAsyncClass().execute();
                MailSender runners = new MailSender();
                String  value = "Company is put to pendind .";
                runners.execute("Company is requested to pending!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Toast.makeText(getActivity(), "Company is rejected successfully!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), RootHomeActivity.class));

        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }
    public void acceptedEmployee(User user) {
        user.setAuth("1");
        final UserDao userDao = new UserDao();
        boolean result = userDao.acceptedEmployee(user);
        Log.d(TAG,"employeee email id.."+user.getUserName());
        if (result) {
            try {
                //  new MyAsyncClass().execute();
                MailSender runners = new MailSender();
                String  value = "Employee accepted successfully!";
                runners.execute("Employee accepted successfully!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            startActivity(new Intent(getActivity(),AdminHomeActivity.class));
            Toast.makeText(getActivity(), "Employee is accepted successfully!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while accepted the company, please try again!");
        }

    }

    public void pendingEmployee(User user) {
        user.setAuth("2");
        final UserDao userDao = new UserDao();
        boolean result = userDao.pendingEmployee(user);
        if (result) {
            try {
                //  new MyAsyncClass().execute();
                MailSender runners = new MailSender();
                String  value = "Employee is requested to pending!";
                runners.execute("Employee is requested to pending!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Toast.makeText(getActivity(), "Employee is requested to pending!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(),AdminHomeActivity.class));
        } else {
            Log.d(TAG, "Error while pending the company, please try again!");
        }
    }

    public void deleteEmployee(User user) {
        user.setAuth("3");
        final UserDao userDao = new UserDao();
        boolean result = userDao.deleteEmployee(user);
        if (result) {
            try {
                //  new MyAsyncClass().execute();
                MailSender runners = new MailSender();
                String  value = "Employee is rejected successfully!";
                runners.execute("Employee is rejected successfully!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Toast.makeText(getActivity(), "Employee is rejected successfully!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(),AdminHomeActivity.class));
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG,"roleValue....roleValue...."+roleValue);
        if(role.equals("user")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageAdmin();
                    return true;
            }
        }
        if(role.equals("admin") && roleValue.matches("root")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageRoot();
                    return true;
            }
        }
        if(role.equals("admin") && roleValue.matches("admin")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageAdmins();
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


    public ViewUserDetails getActivity() {
        return this;
    }
}
