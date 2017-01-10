package com.bluemapletech.hippatextapp.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.activity.NotAcceptedUser;
import com.bluemapletech.hippatextapp.activity.ViewUserDetailTabActivity;
import com.bluemapletech.hippatextapp.adapter.PageEmployeeBaseAdpter;
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

/**
 * Created by Win7v5 on 10/24/2016.
 */

public class InterChatEmployeeTabActivity extends Fragment {
    private ListView listview;
    ArrayList empList = new ArrayList();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private String loggedINCompany;
    private String loggedINEmail;
    private String loggedINChatPin;
    private String userFirstName;
    private String userLastName;
    private String fName, lName, userEmailName;
    public static final String userEmails = "userEmails";
    HashMap<String, String> onlineHash = new HashMap<String, String>();
    List<User> userObj;
    private static final String TAG = IntraChatEmployeeTabActivity.class.getCanonicalName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.accepted_admin_tab_fragment, container, false);

        listview = (ListView) rootView.findViewById(R.id.accepted_admin_tab_fragment);

        fireBaseDatabase = FirebaseDatabase.getInstance();
        final User user = new User();
        checkCompanyExistence();
        checkOnlineUser();
        loadingUserDetail();
       /* listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent redirect = new Intent(getActivity(), ViewUserDetailTabActivity.class);
                redirect.putExtra(userEmails, userObj.get(position).getUserName());
                startActivity(redirect);
        }
        });*/
        return rootView;
    }


    public void checkCompanyExistence() {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkOnlineUser() {
        Log.d(TAG, "checkOnliecalling");
        fireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("onlineUser");
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "checkOnliecallingdatasnapshot");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String onlineUser = dataSnapshot.child("onlineUser").getValue(String.class);
                    Log.d("dfdfdfdfdf", "insidecalling" + onlineUser);
                    onlineHash.put(onlineUser, onlineUser);
                }
                loadingUserDetail();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadingUserDetail() {
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("userDetails");
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                userObj = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Snapshot value: " + snapshot.toString());
                    user = new User();
                    user.setCompanyName(snapshot.child("companyName").getValue(String.class));
                    user.setEmpId(snapshot.child("employeeId").getValue(String.class));
                    user.setPassword(snapshot.child("password").getValue(String.class));
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    user.setSenderId(snapshot.child("senderId").getValue(String.class));
                    user.setProfilePjhoto(snapshot.child("profilePhoto").getValue(String.class));
                    user.setPushNotificationId(snapshot.child("pushNotificationId").getValue(String.class));
                    user.setFirstName(snapshot.child("firstName").getValue(String.class));
                    user.setLastName(snapshot.child("lastName").getValue(String.class));
                    user.setTINorEIN(snapshot.child("companyCINNumber").getValue(String.class));
                    user.setProviderNPIId(snapshot.child("providerNPIId").getValue(String.class));
                    Log.d(TAG, "userFirstName and LastName.." + user.getFirstName() + user.getLastName());
                    if (!user.getLastName().matches("") && !user.getFirstName().matches("")) {
                        String[] valueuserName = user.getUserName().split("@");
                        user.setFirstName(valueuserName[0]);
                    }
                    Log.d(TAG,"loggedINCompanyloggedINCompany"+loggedINCompany +loggedINEmail);
                    if(loggedINCompany!=null && loggedINEmail!=null) {
                        if (!user.getRole().matches("root") && user.getAuth().matches("1") && !loggedINCompany.matches(user.getCompanyName()) && !loggedINEmail.matches(user.getUserName())) {
                            userObj.add(user);
                        }
                    }
                }
                if (getActivity() != null) {

                    listview.setAdapter(new PageEmployeeBaseAdpter(getActivity(), userObj, loggedINEmail, loggedINChatPin, onlineHash));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
