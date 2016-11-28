package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.EmployeeGroupsAdapter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewGroupDetails extends AppCompatActivity {
    private static final String TAG = ViewGroupDetails.class.getCanonicalName();
    private String groupName;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private ListView iv;
    private  String[] separated;
    Groups group = new Groups();
    List<Groups> groupObj = new ArrayList<Groups>();
    private String loggedEmail;
    ImageView viewImage;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_details);
        iv = (ListView) findViewById(R.id.group_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar_header_menu);
        if (toolbar != null) {
           setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        viewImage = (ImageView) findViewById(R.id.view_group_image);
        groupName = getIntent().getStringExtra(GroupMessageEmployeeActivity.groupNames);
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        loggedEmail = logged.getEmail().replace(".", "-");
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("group").child(loggedEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  String groupValue = snapshot.child("groupName").getValue(String.class);
                   String groupEmailId = snapshot.child("groupEmailId").getValue(String.class);
                    String randomName = snapshot.child("randomName").getValue(String.class);

                    if(groupValue.matches(groupName)){
                        group.setGroupEmailId(groupEmailId);
                        group.setRandomName(randomName);
                        group.setGroupImage(snapshot.child("groupImage").getValue(String.class));
                        Picasso.with(ViewGroupDetails.this).load(group.getGroupImage()).fit().centerCrop().into(viewImage);
                    }
                }
                separated = group.getGroupEmailId().split(";");
                for(int i=0; i<separated.length;i++){
                    getGroupUser(separated[i],group.getRandomName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_group_user_menu, menu);
//        menu.findItem(R.id.delete).setVisible(false);
        return true;
    }
    private void getGroupUser(String userMail, String randomValue) {
      final Groups  groupValue = new Groups();
        groupValue.setUserMail(userMail);
        Log.d(TAG, " userMail for before insert: " + userMail);
        String reArrangeEmail = userMail.replace(".", "-");
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmail).child(randomValue);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                groupValue.setStatus(map.get("status"));
                groupObj.add(groupValue);
                if(getActivity()!=null) {
                    iv.setAdapter(new GroupUserAdapter(getActivity(), groupObj));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class GroupUserAdapter extends BaseAdapter {

        List<Groups> groupInfo = new ArrayList<Groups>();
        LayoutInflater inflater;
        Context context;
        public GroupUserAdapter(Context context, List<Groups> group) {
            this.context = context;
            this.groupInfo = group;
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
if(info.getStatus().matches("admin")){
    /*View btn = (Button) convertView.findViewById(R.id.btn_admin_view);
    btn.setVisibility(btn.GONE);*/
}else if(info.getStatus().matches("user")){
    View btn = convertView.findViewById(R.id.btn_admin_view);
    btn.setVisibility(View.GONE);
}
            mViewHolder.fieldName.setText(info.getUserMail());
            mViewHolder.btnName.setText("admin");
            return convertView;
        }



        private class MyViewHolder {
            private TextView  fieldName;
            private Button btnName;
            public MyViewHolder(View item) {
                fieldName = (TextView) item.findViewById(R.id.user_email);
                btnName = (Button) item.findViewById(R.id.btn_admin_view);

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
        startActivity(new Intent(getActivity(),EmployeeHomeActivity.class));
    }
    public ViewGroupDetails getActivity() {
        return this;
    }
}
