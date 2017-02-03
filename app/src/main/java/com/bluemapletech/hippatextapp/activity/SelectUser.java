package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.EmployeeDao;
import com.bluemapletech.hippatextapp.model.Groups;
import com.bluemapletech.hippatextapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectUser extends AppCompatActivity {
    private static final String TAG = SelectUser.class.getCanonicalName();
    public static final String groupNames = "groupNames";
    public static final String randomValues = "randomValues";
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private ListView iv;
    public int listPosition;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
    String isOnline,login_role;
    boolean notAllowUser = true;
    int p=0;
    Groups groupVal;
    private ArrayList<String> data = new ArrayList<>();
    List<User> userObj = new ArrayList<User>();
  //  List<User> groupUserObj = new ArrayList<User>();
    String groupPushNotificationId,loginMail;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        Bundle bundle =  getIntent().getExtras();

        //login user details
        pref1 = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginMail =  pref1.getString("loginMail", "");
        isOnline =  pref1.getString("isOnline", "");
        login_role = pref1.getString("role", "");
        groupVal = (Groups) bundle.getSerializable("groupDetails");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Select contact");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView header = (TextView) findViewById(R.id.header);
            header.setText("Select Contact");
        }
        fireBaseDatabase = FirebaseDatabase.getInstance();
        final User user = new User();
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("userDetails");
        iv = (ListView) findViewById(R.id.all_user);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = new User();
                    fireBaseDatabase = FirebaseDatabase.getInstance();
                    user.setCompanyName(snapshot.child("companyName").getValue(String.class));
                    user.setEmpId(snapshot.child("employeeId").getValue(String.class));
                    user.setPassword(snapshot.child("password").getValue(String.class));
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    user.setProfilePjhoto(snapshot.child("profilePhoto").getValue(String.class));
                    user.setPushNotificationId(snapshot.child("profilePhoto").getValue(String.class));
                    user.setPushNotificationId(snapshot.child("pushNotificationId").getValue(String.class));
                    Log.d(TAG,"groupValues name "+ groupVal.getGroupEmailId());
                    String Value  = groupVal.getGroupEmailId();
                    String[] separated = groupVal.getGroupEmailId().split(";");
                    if (!user.getRole().matches("root") && user.getAuth().matches("1") && !loginMail.matches(user.getUserName())) {
                        Log.d(TAG,"separated" + separated.length);
                        int check = 0;
                        for(int j=0; j<separated.length;j++){
                            if(user.getUserName().matches(separated[j])){
                                check = 1;
                                if(p == 0){
                                    p++;
                                    groupPushNotificationId = user.getPushNotificationId();
                                } else if(p < 0){
                                    groupPushNotificationId = groupPushNotificationId +";"+ user.getPushNotificationId();
                                }

                            }
                        }
                        if(check==0) {
                            userObj.add(user);
                        }
                    }
                    iv.setAdapter(new EmployeeUserBaseAdapter(getActivity(), userObj));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        iv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(notAllowUser){
                    listPosition = position - iv.getFirstVisiblePosition();
                    Log.d("positionMAil","positionMAil"+userObj.get(position).getUserName());
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage("Add Ac to " + " ' " +groupVal.getGroupName() + " ' " + " group?");
                    // Setting Positive "Yes" Button
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("SelectUser","serializablevalue"+ groupVal);
                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage("Please wait a moment...");
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(false);
                            EmployeeDao empDao = new EmployeeDao();
                            boolean success = empDao.addMemberToGroup(userObj.get(position).getUserName(),groupVal,groupPushNotificationId,userObj.get(position).getPushNotificationId(),loginMail);
                            if(success){
                                progressDialog.dismiss();
                                Intent intent = new Intent(getActivity(), ViewGroupDetails.class);
                                intent.putExtra(groupNames,groupVal.getGroupName());
                                intent.putExtra(randomValues,groupVal.getRandomName());
                                startActivity(intent);
                            }
                        }
                    });
                    // Setting Negative "NO" Button
                    dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    // Showing Alert Message
                    dialog.show();
                }else{
                    showErrorMsg();
                }

            }
        });

        String loggedEmail = loginMail.replace(".", "-");
        //get login user groupdetais
        Log.d(TAG,"randomValues"+groupVal.getRandomName());
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("group").child(loggedEmail).child(groupVal.getRandomName());
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, String> map = (Map) dataSnapshot.getValue();
                if(map!=null) {
                    Log.d(TAG,"dataSnapshot map not null");
                }else{
                    Log.d(TAG,"dataSnapshot map  null");
                    notAllowUser = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private class EmployeeUserBaseAdapter extends BaseAdapter {

        List<User> userInfo = new ArrayList<User>();
        List<User> groupUserInfo = new ArrayList<User>();
        LayoutInflater inflater;
        Context context;
        private String loginMail;
        public EmployeeUserBaseAdapter(Context context, List<User> user) {
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

            SelectUser.EmployeeUserBaseAdapter.MyViewHolder mViewHolder  = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_select_user, parent, false);
                mViewHolder = new SelectUser.EmployeeUserBaseAdapter.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (SelectUser.EmployeeUserBaseAdapter.MyViewHolder) convertView.getTag();
            }



            final User info = getItem(position);
            mViewHolder.fieldName.setText(info.getUserName());
            if(info.getProfilePjhoto()!= null && !info.getProfilePjhoto().matches("")){
                Picasso.with(context).load(info.getProfilePjhoto()).fit().centerCrop().into(mViewHolder.userImage);
            }


            return convertView;
        }


        private class MyViewHolder {
            private TextView fieldName;
            private ImageView  userImage;
            public MyViewHolder(View item) {
                fieldName = (TextView) item.findViewById(R.id.employee_mail);
                userImage = (ImageView) item.findViewById(R.id.user_image);
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            backPage();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void backPage() {
        Log.d(TAG,"back page..");
        if(notAllowUser){
            startActivity(new Intent(getActivity(),ViewGroupDetails.class));
        }else{
            showErrorMsg();
        }
    }


    public void showErrorMsg(){
        Log.d(TAG,"showError");
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Error");
        alert.setMessage("You can't send message to this group because you're no longer a participant.");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //dialog.cancel();
                if(login_role.matches("admin")){
                    Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
                    startActivity(intent);
                } else if(login_role.matches("user")){
                    Intent intent = new Intent(getActivity(), EmployeeHomeActivity.class);
                    startActivity(intent);
                }else if(login_role.matches("root")){
                    Intent intent = new Intent(getActivity(), RootHomeActivity.class);
                    startActivity(intent);
                }

            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
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
    public SelectUser getActivity() {
        return this;
    }
}
