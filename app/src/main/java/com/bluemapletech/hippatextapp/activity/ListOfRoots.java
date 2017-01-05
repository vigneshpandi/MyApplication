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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.EmployeeListOfRootBaseAdapter;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
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
import java.util.Set;

public class ListOfRoots extends AppCompatActivity {

    private static final String TAG = ListOfRoots.class.getCanonicalName();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private String loggedINCompany;
    private String loggedINEmail;
    private String loggedINChatPin;
    private ListView iv;
    private ArrayList<String> data = new ArrayList<>();
    // private   List<User> userObj;

    ImageView selection;
    String groupMail;
    private HashMap<String, String> hm = new HashMap<String, String>();
    public int listPosition;
    private String groupName = "";
    private String rootValue;
    private String not_acp_user;
    private String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_roots);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        rootValue = getIntent().getStringExtra(RootHomeActivity.rootValue);
        role = getIntent().getStringExtra(RootHomeActivity.role);
        not_acp_user = getIntent().getStringExtra(RootHomeActivity.NotAcceptUser);
        rootValue = getIntent().getStringExtra(NotAcceptedUser.rootValue);
        role = getIntent().getStringExtra(NotAcceptedUser.role);
        not_acp_user = getIntent().getStringExtra(NotAcceptedUser.NotAcceptUser);
        checkUserExistence();
        fireBaseDatabase = FirebaseDatabase.getInstance();
        final User user = new User();
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("userDetails");
        iv = (ListView) findViewById(R.id.all_roots);
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
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setSenderId(snapshot.child("senderId").getValue(String.class));
                    user.setPushNotificationId(snapshot.child("pushNotificationId").getValue(String.class));
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    if (user.getFirstName()==null && user.getLastName()==null) {
                        String[] valueuserName = user.getUserName().split("@");
                        user.setFirstName(valueuserName[0]);
                    }
                    if (user.getRole().matches(role) && user.getAuth().matches(rootValue) && !loggedINEmail.matches(user.getUserName())) {
                        userObj.add(user);
                        Log.d("rootDetails","rootDetails"+user);
                    }

                    iv.setAdapter(new EmployeeListOfRootBaseAdapter(getActivity(), userObj,loggedINEmail,not_acp_user,loggedINChatPin,role));
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
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        String reArrangeEmail = logged.getEmail().replace(".", "-");
        Log.d(TAG,"reArrangeEmailIdd"+reArrangeEmail);
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedINEmail = (String) dataSnapshot.child("emailAddress").getValue();
                loggedINChatPin = (String) dataSnapshot.child("chatPin").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        startActivity(new Intent(getActivity(),RootHomeActivity.class));
    }

    public ListOfRoots getActivity() {
        return this;
    }
}
