package com.bluemapletech.hippatextapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.EmployeeGroupsAdapter;
import com.bluemapletech.hippatextapp.dao.EmployeeDao;
import com.bluemapletech.hippatextapp.dao.UserDao;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bluemapletech.hippatextapp.R.layout.view_groupimage_dialog;

public class ViewGroupDetails extends AppCompatActivity{
    private static final String TAG = ViewGroupDetails.class.getCanonicalName();
    private String groupName;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private ListView iv;
    private  String[] separated;
    Groups group = new Groups();
    List<Groups> groupObj = new ArrayList<Groups>();
    List<Groups> groupObjs = new ArrayList<Groups>();
    private String loggedEmail;
    ImageView viewImage;
    private Toolbar toolbar;
    private Toolbar toolbars;
    private int l=0;
    private int k=0;
    public int listPosition;
     Groups  groupInformation;
    private String userMailId;
    private String reArrangeEmails;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    FirebaseUser logged;
    Map<String,String> maps = new HashMap<String,String>();
    ImageView backPageArrow;
    String groupValues;
    String editGroupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_details);
        iv = (ListView) findViewById(R.id.group_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar_header_menu);
        firebaseAuth = FirebaseAuth.getInstance();
       logged = firebaseAuth.getCurrentUser();
        Log.d(TAG, "Logged in user information's: " + logged.getEmail());
        if (toolbar != null) {
           setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        viewImage = (ImageView) findViewById(R.id.view_group_image);
        ImageView groupNameEdit = (ImageView) findViewById(R.id.groupNameEdit);
        /*
          values comes  from GroupMessageEmployeeActivity
         */
        groupName = getIntent().getStringExtra(GroupMessageEmployeeActivity.groupNames);

        /*
          values comes from SelectUser
         */
        groupName = getIntent().getStringExtra(SelectUser.groupNames);
        if(groupName!=null) {
             pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
             editor = pref.edit();
             editor.putString("groupNameValue", groupName);
             editor.commit();
        }
        if(groupName == null){
            Log.d(TAG,"inside groupName is null");
            pref = getSharedPreferences("MyPref",MODE_PRIVATE);
            Log.d(TAG,"groupNamedialogVAlue"+pref.getString("groupNameValue",""));
            groupName =  pref.getString("groupNameValue","");
        }
        TextView name = (TextView) findViewById(R.id.group_name);
       name.setText(groupName);

        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        loggedEmail = logged.getEmail().replace(".", "-");
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("group").child(loggedEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   groupValues = snapshot.child("groupName").getValue(String.class);
                   String groupEmailId = snapshot.child("groupEmailId").getValue(String.class);
                    String randomName = snapshot.child("randomName").getValue(String.class);
                   String  admin = snapshot.child("admin").getValue(String.class);

                    Log.d(TAG,"groupName"+groupName);
                    if(groupValues.matches(groupName)){
                        group.setAdmin(admin);
                        group.setGroupEmailId(groupEmailId);
                        group.setGroupImage(snapshot.child("groupImage").getValue(String.class));
                        group.setRandomName(randomName);
                        group.setGroupName(groupValues);
                        Picasso.with(ViewGroupDetails.this).load(group.getGroupImage()).fit().centerCrop().into(viewImage);
                    }
                }
                separated = group.getGroupEmailId().split(";");
                Log.d("separated","separated"+separated);
                Log.d("separated","separated"+separated.length);
                for(int i=0; i<separated.length;i++){
                    getUserProfile(separated[i]);
                   // getGroupUser(separated[i],group.getRandomName());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        groupNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewGroupDetails.this);
                alertDialog.setMessage("Enter your group name");

                final EditText input = new EditText(ViewGroupDetails.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editGroupName = input.getText().toString();
                                Log.d(TAG,"editGroupName.."+editGroupName);
                                Log.d(TAG,"0000000000"+groupObj.get(0).getGroupName());
                                Log.d(TAG,"0000000001"+groupObj.get(1).getGroupName());
                                for(int m=0;m<groupObj.size();m++){
                                    String us_mail = groupObj.get(m).getUserMail();
                                    Log.d(TAG,"us_mail..."+us_mail);
                                    reArrangeEmails = us_mail.replace(".", "-");
                                    String r_value = groupObj.get(m).getRandomName();
                                    DatabaseReference dataReferences = fireBaseDatabase.getReference().child("group").child(reArrangeEmails).child(r_value).child("groupName");
                                    dataReferences.setValue(editGroupName);
                                    TextView name = (TextView) findViewById(R.id.group_name);
                                    Log.d(TAG,"groupValioo"+editGroupName);
                                    name.setText(editGroupName);
                                    l=0;
                                    k=0;
                                    int i=0;

                                }
                            }
                        });
                alertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
        iv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPosition = position;
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Make group admin");
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EmployeeDao empDao = new EmployeeDao();
                        boolean success =  empDao.empChangeAdmintoGroup(groupObj.get(listPosition).getRandomName(),groupObj.get(listPosition).getUserMail());
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

            }
        });

        viewImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(ViewGroupDetails.this,android.R.style.Widget_ProgressBar_Small_Inverse);
                dialog.setContentView(R.layout.view_groupimage_dialog);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
                ImageView backPageArrow = (ImageView) dialog.findViewById(R.id.backarrow);
                if (toolbar != null) {
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    dialog.show();
                }
                //dialog.addContentView();
                ImageView showImage = (ImageView) dialog.findViewById(R.id.view_group_img);
                Picasso.with(ViewGroupDetails.this).load(group.getGroupImage()).fit().centerCrop().into(showImage);
                dialog.show();
                backPageArrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "back arrow is clicked!");
                            groupName = getIntent().getStringExtra(SelectUser.groupNames);
                            Log.d(TAG, "back arrow is clicked!....."+groupName);
                            Intent intent = new Intent(getActivity(),ViewGroupDetails.class);
                            startActivity(intent);

                        }
                    });
                }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_group_user_menu, menu);
       menu.findItem(R.id.add_admin_group_menu).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(id == R.id.add_admin_group_menu){
            Log.d(TAG,"groupDetails"+group);
            Intent intent = new Intent(getActivity(), SelectUser.class);
            Bundle b = new Bundle();
            b.putSerializable("groupDetails", group);
            intent.putExtras(b);
            startActivity(intent);
        }

        switch (item.getItemId()){
            case android.R.id.home:
                backPage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getGroupUser(String userMail, String randomValue) {
        userMailId = userMail;
        final Groups  groupValue = new Groups();
        groupValue.setUserMail(userMail);
        groupValue.setUserImage(groupObjs.get(k).getUserImage());
        k++;
        reArrangeEmails = userMail.replace(".", "-");
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmails).child(randomValue);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                groupValue.setStatus(map.get("status"));
                groupValue.setRandomName(map.get("randomName"));
                groupValue.setGroupName(map.get("groupName"));
                groupValue.setGroupImage(map.get("groupImage"));
                groupValue.setGroupEmailId(map.get("groupEmailId"));
                groupValue.setAdmin(map.get("admin"));
                Log.d(TAG,"groupsssss"+groupValue);
                groupObj.add(groupValue);
                if(getActivity()!=null) {
                    iv.setAdapter(new GroupUserAdapter(getActivity(), groupObj,logged.getEmail()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserProfile(String userMail) {
        groupInformation = new Groups();
        reArrangeEmails = userMail.replace(".", "-");
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmails);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                   groupInformation.setUserImage(map.get("profilePhoto"));
              // maps.put(separated[l],groupInformation.getUserImage());
                groupObjs.add(groupInformation);
               Log.d(TAG,"separated[l]"+separated[l]);
                Log.d(TAG,"group.getRandomName()"+group.getRandomName());
                getGroupUser(separated[l],group.getRandomName());
                l++;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private class GroupUserAdapter extends BaseAdapter {

        List<Groups> groupInfo = new ArrayList<Groups>();
        LayoutInflater inflater;
        String loginMailId;
        Context context;
        public GroupUserAdapter(Context context, List<Groups> group, String loginMail) {
            this.context = context;
            this.groupInfo = group;
            this.loginMailId=loginMail;
            inflater = LayoutInflater.from(this.context);
        }


        public int getCount() {
            return groupInfo.size();
        }

        @Override
        public Groups getItem(int position) {
            return groupInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            MyViewHolder mViewHolder  = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.view_list_of_user_under_group, parent, false);
                mViewHolder = new GroupUserAdapter.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (GroupUserAdapter.MyViewHolder) convertView.getTag();
            }

            final Groups info = getItem(position);
if(info.getStatus().matches("admin") && info.getUserMail().matches(loginMailId)){
    Log.d(TAG,"valUES");
    toolbar.getMenu().findItem(R.id.add_admin_group_menu).setVisible(true);
}else if(info.getStatus().matches("user")){
    View btn = convertView.findViewById(R.id.btn_admin_view);
    btn.setVisibility(View.GONE);
}
            mViewHolder.fieldName.setText(info.getUserMail());
            mViewHolder.btnName.setText("admin");
            if(info.getUserImage()!=null) {
                Log.d(TAG,"info.getUserImage()"+info.getUserImage());
              //  Picasso.with(context).load(info.getUserImage()).fit().centerCrop().into(mViewHolder.userImage);
            }
            return convertView;
        }



        private class MyViewHolder {
            private TextView  fieldName;
            private ImageView userImage;
            private Button btnName;
            public MyViewHolder(View item) {
                fieldName = (TextView) item.findViewById(R.id.user_email);
                btnName = (Button) item.findViewById(R.id.btn_admin_view);
                userImage = (ImageView) item.findViewById(R.id.user_image);

            }
        }
    }


    private void backPage() {
        startActivity(new Intent(
                getActivity(),GroupMessageEmployeeActivity.class));
    }
    public ViewGroupDetails getActivity() {
        return this;
    }
}