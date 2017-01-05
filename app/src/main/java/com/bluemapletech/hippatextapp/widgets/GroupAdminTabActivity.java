package com.bluemapletech.hippatextapp.widgets;

/**
 * Created by BlueMaple on 1/5/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.AdminGroupBaseAdapter;
import com.bluemapletech.hippatextapp.adapter.EmployeeGroupsAdapter;
import com.bluemapletech.hippatextapp.model.Groups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Win7v5 on 10/24/2016.
 */

public class GroupAdminTabActivity extends Fragment {
    private ListView listview;
    ArrayList empList = new ArrayList();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private String loggedINCompany;
    private String loggedINEmail;
    private String loggedINChatPin;
    private String loggedINsenderId;
    private static final String TAG = GroupChatEmployeeTabActivity.class.getCanonicalName();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.accepted_admin_tab_fragment, container, false);
        listview = (ListView) rootView.findViewById(R.id.accepted_admin_tab_fragment);
        checkUserDetails();
        final Groups group = new Groups();
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        String reArrangeEmail = logged.getEmail().replace(".", "-");
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmail);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group;
                List<Groups> groupObj = new ArrayList<Groups>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Snapshot value: " + snapshot.toString());
                    group = new Groups();
                    group.setGroupName(snapshot.child("groupName").getValue(String.class));
                    group.setAdmin(snapshot.child("admin").getValue(String.class));
                    group.setStatus(snapshot.child("status").getValue(String.class));
                    group.setRandomName(snapshot.child("randomName").getValue(String.class));
                    group.setGroupEmailId(snapshot.child("groupEmailId").getValue(String.class));
                    group.setGroupImage(snapshot.child("groupImage").getValue(String.class));
                    groupObj.add(group);
                }
                if(getActivity() !=null){
                    listview.setAdapter(new AdminGroupBaseAdapter(getActivity(), groupObj ,loggedINsenderId ,loggedINChatPin,loggedINEmail));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }
    public void checkUserDetails() {
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        Log.d(TAG, "Logged in user information's: " + logged.getEmail());
        String reArrangeEmail = logged.getEmail().replace(".", "-");
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedINCompany = (String) dataSnapshot.child("companyName").getValue();
                loggedINEmail = (String) dataSnapshot.child("emailAddress").getValue();
                loggedINChatPin = (String) dataSnapshot.child("chatPin").getValue();
                loggedINsenderId = (String) dataSnapshot.child("senderId").getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
