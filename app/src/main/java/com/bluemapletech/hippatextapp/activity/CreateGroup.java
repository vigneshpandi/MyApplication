package com.bluemapletech.hippatextapp.activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.EmployeeDao;
import com.bluemapletech.hippatextapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CreateGroup extends AppCompatActivity {

    private static final String TAG = CreateGroup.class.getCanonicalName();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private String loggedINEmail,loggedINCompany,loggegINRole,loggedINChatPin,role;
    private Uri downloadUrl;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private StorageReference mStorage;
    private ListView iv;
    private ArrayList<String> data = new ArrayList<>();
    List<User> userObj = new ArrayList<User>();
    ImageView selection;
    private HashMap<String, String> hm = new HashMap<String, String>();
    private HashMap<String, String>  groupNotificationId = new HashMap<String, String>();
    public int listPosition;
    private String groupName = "";
    private String storeMail,groupMail;
    private String storePushNotiId,groupPushNotiId;
    private SecureRandom random;
    private String senderID,roleValue,isOnline;
    private ProgressDialog progressDialog;
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
    private String loginMail; // loginDetail string declare
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        TextView header = (TextView) findViewById(R.id.header);
        header.setText("Select Member");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Select Member");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //login user details
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        roleValue =  pref.getString("role", "");
        loginMail =  pref.getString("loginMail", "");
        isOnline =  pref.getString("isOnline", "");

        fireBaseDatabase = FirebaseDatabase.getInstance();
        final User user = new User();
        checkCompanyExistence();
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("userDetails");
        iv = (ListView) findViewById(R.id.all_employee);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = new User();
                    user.setCompanyName(snapshot.child("companyName").getValue(String.class));
                    user.setEmpId(snapshot.child("employeeId").getValue(String.class));
                    user.setPassword(snapshot.child("password").getValue(String.class));
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    user.setProfilePjhoto(snapshot.child("profilePhoto").getValue(String.class));
                    user.setPushNotificationId(snapshot.child("pushNotificationId").getValue(String.class));
                    if (!user.getRole().matches("root") && user.getAuth().matches("1") && !loggedINEmail.matches(user.getUserName())) {
                        userObj.add(user);
                    }
                    iv.setAdapter(new EmployeeCreateGroupBaseAdapter(getActivity(), userObj,loggedINEmail));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        iv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPosition = position - iv.getFirstVisiblePosition();
                if (iv.getChildAt(listPosition).findViewById(R.id.tickIcon).getVisibility() == View.INVISIBLE){
                    iv.getChildAt(listPosition).findViewById(R.id.tickIcon).setVisibility(View.VISIBLE);
                    hm.put(userObj.get(position).getUserName(),userObj.get(position).getUserName());
                    groupNotificationId.put(userObj.get(position).getPushNotificationId(),userObj.get(position).getPushNotificationId());
                }else{
                    hm.remove(userObj.get(position).getUserName());
                    groupNotificationId.remove(userObj.get(position).getPushNotificationId());
                    iv.getChildAt(listPosition).findViewById(R.id.tickIcon).setVisibility(View.INVISIBLE);
                }
               iv.getChildAt(listPosition).setSelected(true);
            }
        });

    }


    public void checkCompanyExistence() {
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        String reArrangeEmail = loginMail.replace(".", "-");
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                role = (String) dataSnapshot.child("role").getValue();
                loggedINCompany = (String) dataSnapshot.child("companyName").getValue();
                loggedINEmail = (String) dataSnapshot.child("emailAddress").getValue();
                loggedINChatPin = (String) dataSnapshot.child("chatPin").getValue();
                loggegINRole = (String) dataSnapshot.child("role").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_create, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("menu selected","menu  selected");
        //noinspection SimplifiableIfStatement
        if(role.equals("admin")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageAdmin();
                    return true;
            }
        }
        if(role.equals("user")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageEmp();
                    return true;
            }
        }
        if (id == R.id.group_create) {
            Log.d("menu selected","menu group create selected");
            Log.d("loggedINEmail",loggedINEmail);
            Set<String> keys = hm.keySet();
            Set<String>  groupNotKey = groupNotificationId.keySet();
            int i=0;
            for(String key: keys){
                storeMail = hm.get(key);
                if(i==0){
                    i++;
                    groupMail = loggedINEmail +";"+storeMail;
                }else if(i>0){
                    groupMail = groupMail +";"+storeMail;
                }

            }
            int j =0;
            for(String key: groupNotKey){
                storePushNotiId = groupNotificationId.get(key);
                if(j==0){
                    j++;
                    groupPushNotiId = storePushNotiId;
                }else if(j>0){
                    groupPushNotiId = groupPushNotiId +";"+storePushNotiId;
                }

            }



            if(i>0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Group Name");
                // Set up the input
                final EditText input = new EditText(this);
                alert.setView(input);
                // Set up the buttons
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Creating group...");
                        progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);
                        groupName = input.getText().toString();
                        Log.d(TAG,"gropName outside is"+groupName);
                        if(groupName.length() != 0){
                            Log.d(TAG,"gropName length is"+groupName);
                            saveImage();
                        }else {
                            Log.d(TAG,"group name is empty");
                            Toast.makeText(getActivity(),"Group name is empty!",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alert.show();
                return true;
            }else{
                Log.d(TAG,"employee not selected...");
                Toast.makeText(getActivity(),"Please select the user..!",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveImage() {
        final  EmployeeDao empDao = new EmployeeDao();
        random = new SecureRandom();
        senderID = new BigInteger(130, random).toString(32);
        String randomValue = senderID.substring(0, 7);
        Log.d("randomValue",randomValue);
        mStorage = FirebaseStorage.getInstance().getReference();
        Uri uri = Uri.parse("android.resource://com.bluemapletech.hippatextapp/" + R.drawable.groupimage);
        StorageReference filePath = mStorage.child(groupName+senderID);
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                Log.d(TAG,"downloadUrl " + downloadUrl);
                Log.d(TAG,"uservalie"+loggedINEmail + groupMail + groupName + downloadUrl);
                boolean success = empDao.createGroup(loggedINEmail, groupMail, groupName , downloadUrl,groupPushNotiId);
                if(roleValue.matches("admin")){
                    Log.d(TAG, "admin has been called!");
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Group is created successfully!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
                    startActivity(intent);
                }else if(roleValue.matches("user")){
                    Log.d(TAG, "user has been called!");
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Group is created successfully!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), EmployeeHomeActivity.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void backPageEmp() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),EmployeeHomeActivity.class));
    }

    private void backPageAdmin() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
    }


    private class EmployeeCreateGroupBaseAdapter extends BaseAdapter {

        List<User> userInfo = new ArrayList<User>();
        LayoutInflater inflater;
        Context context;
        private String loginMail;
        public EmployeeCreateGroupBaseAdapter(Context context, List<User> user , String loginMail) {
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
            MyViewHolder mViewHolder  = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.employee_group_items, parent, false);
                mViewHolder = new EmployeeCreateGroupBaseAdapter.MyViewHolder(convertView);
                mViewHolder.tickMark=(ImageView) convertView.findViewById(R.id.tickIcon);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (EmployeeCreateGroupBaseAdapter.MyViewHolder) convertView.getTag();
            }
            final User info = getItem(position);
            Set<String> keys1 = hm.keySet();
        if(hm.get(info.getUserName()) != null){
             View ticks = convertView.findViewById(R.id.tickIcon);
            ticks.setVisibility(View.VISIBLE);
        }else{
            View ticks = convertView.findViewById(R.id.tickIcon);
            ticks.setVisibility(View.INVISIBLE);
        }


            mViewHolder.fieldName.setText(info.getUserName());
            final View finalConvertView = convertView;
            selection = (ImageView)convertView.findViewById(R.id.tickIcon);
            if(info.getProfilePjhoto()!= null && !info.getProfilePjhoto().matches("")){
                Picasso.with(context).load(info.getProfilePjhoto()).fit().centerCrop().into(mViewHolder.userImage);
            }

            return convertView;
        }


        private class MyViewHolder {
            private TextView fieldId, fieldName;
            private ImageView tickMark, userImage;
            public MyViewHolder(View item) {
                fieldName = (TextView) item.findViewById(R.id.employee_mail);
                tickMark = (ImageView) item.findViewById(R.id.tickIcon);
                userImage = (ImageView) item.findViewById(R.id.user_image);
            }
        }
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
    public CreateGroup getActivity() {
        return this;
    }
}
