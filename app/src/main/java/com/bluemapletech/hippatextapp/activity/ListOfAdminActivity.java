package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.MailSender;
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

public class ListOfAdminActivity extends AppCompatActivity {

    private static final String TAG = ListOfAdminActivity.class.getCanonicalName();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private String loggedInCompanyValue;
    private String loggedINEmail;
    private String loggedINChatPin,isOnline;
    private ListView iv;
    private ArrayList<String> data = new ArrayList<>();
    List<User> userObj ;
    ImageView selection;
    String groupMail;
    private HashMap<String, String> hm = new HashMap<String, String>();
    public int listPosition;
    private String groupName = "";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static final String userEmails = "userEmails";
    public static final String userAuth = "userAuth";
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Admin List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView header = (TextView) findViewById(R.id.header);
            header.setText("Admin List");
        }
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        String loginMail =  pref.getString("loginMail", "");
        String chatPin =  pref.getString("chatPin", "");
        String companyName =  pref.getString("loginCompanyName", "");
        loggedINEmail = loginMail;
        loggedInCompanyValue = companyName;
        Log.d("loggedInCoInCom",loggedInCompanyValue);
        fireBaseDatabase = FirebaseDatabase.getInstance();
        final User user = new User();
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("userDetails");
        iv = (ListView) findViewById(R.id.all_admins);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                userObj = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = new User();
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setCompanyName(snapshot.child("companyName").getValue(String.class));
                    user.setProviderNPIId(snapshot.child("providerNPIId").getValue(String.class));
                    if (user.getRole().matches("admin") && user.getAuth().matches("1") && !loggedINEmail.matches(user.getUserName())&& loggedInCompanyValue.matches(user.getCompanyName())) {
                        userObj.add(user);
                        Log.d("adminDetails","adminDetails"+user);
                    }
                    iv.setAdapter(new PageAdminBaseAdapters(getActivity(), userObj,loggedINEmail));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    /*public void checkUserExistence() {
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
    }*/
    private class PageAdminBaseAdapters extends BaseAdapter {

        List<User> userInfo = new ArrayList<User>();
        LayoutInflater inflater;
        Context context;
        private String loginMail;
        public PageAdminBaseAdapters(Context context, List<User> user , String loginMail) {
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

            ListOfAdminActivity.PageAdminBaseAdapters.MyViewHolder mViewHolder  = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.view_admin_list, parent, false);
                mViewHolder = new ListOfAdminActivity.PageAdminBaseAdapters.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ListOfAdminActivity.PageAdminBaseAdapters.MyViewHolder) convertView.getTag();
            }

            final User info = getItem(position);
            mViewHolder.fieldName.setText(info.getUserName());
            mViewHolder.fieldId.setText(info.getProviderNPIId());
            convertView.findViewById(R.id.reject_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    // alert.setTitle("");
                    alert.setMessage("Do you want to reject '"+userInfo.get(position).getUserName()+"'!");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteUser(userInfo.get(position).getUserName());
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
            convertView.findViewById(R.id.root_name).setOnClickListener(new View.OnClickListener() {
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

        public void deleteUser(String userMail) {
            final UserDao userDao = new UserDao();
            boolean result = userDao.deleteUser(userMail);
            Log.d(TAG,"Company is rejected successfully!"+userMail);
            if (result) {
                try {
                    //  new MyAsyncClass().execute();
                    MailSender runners = new MailSender();
                    String  value = "Company is rejected successfully!";
                    runners.execute("Company is rejected successfully!",value,"hipaatext123@gmail.com",userMail);

                } catch (Exception ex) {
                    // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
                }
                Toast.makeText(this.context, "Company is rejected successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.context, "Error while delete the company, please try again!", Toast.LENGTH_LONG).show();
            }
        }

        private class MyViewHolder {
            private TextView fieldName;
            private TextView fieldId;
            public MyViewHolder(View item) {
                fieldName = (TextView) item.findViewById(R.id.root_mail);
                fieldId = (TextView) item.findViewById(R.id.root_name);
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
    public void onPause()
    {
        pref1 = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref1.getString("isOnline", "");
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
        pref1 = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref1.getString("isOnline", "");
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
    public ListOfAdminActivity getActivity() {
        return this;
    }
}
