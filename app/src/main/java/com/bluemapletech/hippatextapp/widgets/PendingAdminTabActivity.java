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
import com.bluemapletech.hippatextapp.adapter.PageAdminBaseAdapter;
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
import java.util.List;

/**
 * Created by Kumaresan on 20-10-2016.
 */

public class PendingAdminTabActivity extends Fragment {
    private static final String TAG = PendingAdminTabActivity.class.getCanonicalName();
    SharedPreferences preflogin;
    SharedPreferences.Editor editorlogin;
    private ListView listview;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;

    private String loggedINCompany,loginsenderId,loginMail;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pending_admin_tab_fragment, container, false);
//login user details
        preflogin = this.getActivity().getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginMail =   preflogin.getString("loginMail","");

        listview = (ListView) rootView.findViewById(R.id.pending_admin_tab_fragment);

        fireBaseDatabase = FirebaseDatabase.getInstance();
        final User user = new User();
        checkCompanyExistence();

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
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    if (user.getRole().matches("user") && user.getAuth().matches("2")
                            && loggedINCompany.matches(user.getCompanyName())) {
                        userObj.add(user);
                    }
                }
                UserDetailDto userDetailDto = new UserDetailDto();
                userDetailDto.setLoginSenderId(loginsenderId);
if(getActivity()!=null) {
    listview.setAdapter(new PageAdminBaseAdapter(getActivity(), userObj,userDetailDto));
}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }

    public void checkCompanyExistence() {
        fireBaseDatabase = FirebaseDatabase.getInstance();
        String reArrangeEmail = loginMail.replace(".", "-");
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedINCompany = (String) dataSnapshot.child("companyName").getValue();
                loginsenderId = (String) dataSnapshot.child("senderId").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
