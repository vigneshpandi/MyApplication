package com.bluemapletech.hippatextapp.dao;

import android.util.Log;

import com.bluemapletech.hippatextapp.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by Kumaresan on 20-10-2016.
 */

public class CompanyDao {

    private static final String TAG = CompanyDao.class.getCanonicalName();

    private FirebaseDatabase firebaseDatabaseRef;

    private DatabaseReference databaseRef;

    public boolean addInvitedCompany(User user) {

        Log.d(TAG, "Add invited company dao method has been called!");
        HashMap<String, Object> invite = new HashMap<>();
        String reArrangeEmail = user.getUserName().replace(".", "-");
        invite.put("auth", "1");
        invite.put("emailAddress", user.getUserName());
        invite.put("password", user.getPassword());
        invite.put("compId", user.getTINorEIN());
        invite.put("companyName", user.getCompanyName());
        invite.put("role", user.getRole());
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(invite);
        return true;

    }

    public boolean cancelCompany(User user) {

        Log.d(TAG, "Add invited company dao method has been called!");
        HashMap<String, Object> invite = new HashMap<>();
        String reArrangeEmail = user.getUserName().replace(".", "-");
        invite.put("auth", "2");
        invite.put("emailAddress", user.getUserName());
        invite.put("password", user.getPassword());
        invite.put("compId", user.getTINorEIN());
        invite.put("companyName", user.getCompanyName());
        invite.put("role", user.getRole());
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(invite);
        return true;

    }

    public boolean deleteCompany(User user) {

        Log.d(TAG, "Add invited company dao method has been called!");
        HashMap<String, Object> invite = new HashMap<>();
        String reArrangeEmail = user.getUserName().replace(".", "-");
        invite.put("auth", "2");
        invite.put("emailAddress", user.getUserName());
        invite.put("password", user.getPassword());
        invite.put("compId", user.getTINorEIN());
        invite.put("companyName", user.getCompanyName());
        invite.put("role", user.getRole());
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(invite);
        return true;

    }
}
