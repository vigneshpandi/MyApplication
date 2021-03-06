package com.bluemapletech.hippatextapp.widgets;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageAdminChatAdapter;
import com.bluemapletech.hippatextapp.adapter.PageEmployeeBaseAdpter;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.model.UserDetailDto;
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
 * Created by Kumaresan on 20-10-2016.
 */

public class ChatAdminActivity extends Fragment{

    private ListView listview;
    ArrayList empList = new ArrayList();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private String loggedINCompany;
    private String loggedINEmail;
    private String loggedINChatPin,loginMail;
    SharedPreferences preflogin;
    SharedPreferences.Editor editorlogin;
    private String userFirstName;
    private String userLastName,loginsenderId;
    HashMap<String,String> onlineHashing;
    private static final String TAG = ChatAdminActivity.class.getCanonicalName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.accepted_admin_tab_fragment, container, false);
        //login user details
        preflogin = this.getActivity().getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginMail =   preflogin.getString("loginMail","");

        listview = (ListView) rootView.findViewById(R.id.accepted_admin_tab_fragment);

        fireBaseDatabase = FirebaseDatabase.getInstance();
        final User user = new User();
        checkOnlineUser();
        checkCompanyExistence();
        loadingUserDetail();


        return rootView;
    }


    public void checkCompanyExistence() {
        fireBaseDatabase = FirebaseDatabase.getInstance();
        /*firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();*/
        String reArrangeEmail = loginMail.replace(".", "-");
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedINCompany = (String) dataSnapshot.child("companyName").getValue();
                loggedINEmail = (String) dataSnapshot.child("emailAddress").getValue();
                loggedINChatPin = (String) dataSnapshot.child("chatPin").getValue();
                loginsenderId = (String) dataSnapshot.child("senderId").getValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkOnlineUser(){
        Log.d(TAG,"checkOnlineUser");
        fireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("onlineUser");
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onlineHashing = new HashMap<String, String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String onlineUser = snapshot.child("onlineUser").getValue(String.class);
                    onlineHashing.put(onlineUser, onlineUser);
                }
                loadingUserDetail();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadingUserDetail(){
        Log.d(TAG,"loadingUserDetail");
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("userDetails");
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                List<User> userObj = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = new User();
                    user.setCompanyName(snapshot.child("companyName").getValue(String.class));
                    user.setEmpId(snapshot.child("employeeId").getValue(String.class));
                    user.setPassword(snapshot.child("password").getValue(String.class));
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    user.setSenderId(snapshot.child("senderId").getValue(String.class));
                    user.setPushNotificationId(snapshot.child("pushNotificationId").getValue(String.class));
                    user.setProfilePjhoto(snapshot.child("profilePhoto").getValue(String.class));
                    user.setFirstName(snapshot.child("firstName").getValue(String.class));
                    user.setLastName(snapshot.child("lastName").getValue(String.class));
                    if(!user.getLastName().matches("") && !user.getFirstName().matches("")){
                        String[] valueuserName = user.getUserName().split("@");
                        user.setFirstName(valueuserName[0]);
                    }
                    if(loggedINCompany!=null && loggedINEmail!=null ){
                    if (user.getRole().matches("user") && user.getAuth().matches("1")&& loggedINCompany.matches(user.getCompanyName()) && !loggedINEmail.matches(user.getUserName())) {
                        userObj.add(user);
                    }}
                }
                UserDetailDto userDetailDto = new UserDetailDto();
                userDetailDto.setLoginSenderId(loginsenderId);
                userDetailDto.setLoggedINEmail(loggedINEmail);
                if(getActivity() !=null) {
                    listview.setAdapter(new PageAdminChatAdapter(getActivity(), userObj,userDetailDto , loggedINChatPin,onlineHashing));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
