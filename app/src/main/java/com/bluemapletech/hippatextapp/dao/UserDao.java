package com.bluemapletech.hippatextapp.dao;

import android.util.Log;

import com.bluemapletech.hippatextapp.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by Kumaresan on 19-10-2016.
 */

public class UserDao {

    private FirebaseDatabase firebaseDatabaseRef;

    DatabaseReference databaseRef;

    private static final String TAG = UserDao.class.getCanonicalName();

    public boolean createEmployee(User user){
        Log.d(TAG, "Create employee dao method has been called!");
        HashMap<String, Object> empData = new HashMap<>();
            empData.put("emailAddress", user.getUserName());
            empData.put("password", user.getPassword());
            empData.put("employeeId", user.getEmpId());
            empData.put("companyName", user.getCompanyName());
            empData.put("role", user.getRole());
            empData.put("auth","0");
            String reArrangeEmail = user.getUserName().replace(".", "-");
            firebaseDatabaseRef = FirebaseDatabase.getInstance();
            databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
            databaseRef.setValue(empData);
        return true;
    }

    public boolean createCompany(User user){
        Log.d(TAG, "Create company dao method has been called!");
        HashMap<String, Object> compData = new HashMap<>();
            compData.put("emailAddress", user.getUserName());
            compData.put("password", user.getPassword());
            compData.put("compId", user.getTINorEIN());
            compData.put("companyName", user.getCompanyName());
            compData.put("role", user.getRole());
            compData.put("auth","0");
            String reArrangeEmail = user.getUserName().replace(".", "-");
            firebaseDatabaseRef = FirebaseDatabase.getInstance();
            DatabaseReference dataReference = firebaseDatabaseRef.getReference().child("companyName").child(user.getCompanyName()).child("companyName");
            dataReference.setValue(compData.get("companyName"));
            DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
            databaseRef.setValue(compData);
        return true;
    }

    public boolean sendInvite(User user){

        Log.d(TAG, "Add invited company dao method has been called!");
        HashMap<String, Object> invite = new HashMap<>();
        String reArrangeEmail = user.getUserName().replace(".", "-");
        invite.put("auth", "2");
        invite.put("emailAddress", user.getUserName());
        invite.put("password", user.getPassword());
        invite.put("employeeId", user.getTINorEIN());
        invite.put("companyName", user.getCompanyName());
        invite.put("role", user.getRole());
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(invite);

        return true;

    }

    public UserDao getActivity(){ return this;}

}
