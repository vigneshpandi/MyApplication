package com.bluemapletech.hippatextapp.dao;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

/**
 * Created by Win7v5 on 11/7/2016.
 */

public class EmployeeDao {
    private static final FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
    private static final DatabaseReference sRef = mfireBaseDatabase.getReference();
    private FirebaseDatabase firebaseDatabaseRef;

    DatabaseReference databaseRef;

    private static final String TAG = EmployeeDao.class.getCanonicalName();
    private SecureRandom random;
    private String randomName;
    public boolean createGroup(String loggedINEmail, String groupMail, String groupName , Uri downloadUrl) {
        String profileUrl = String.valueOf(downloadUrl);
        HashMap<String, Object> empData = new HashMap<>();
        Log.d(TAG, "CreateGroup employee dao method has been called!");
        Log.d("groupMail",groupMail);
        String[] seprated = groupMail.split(";");
        Log.d("seprated","lebngthofthe seprated"+seprated.length);
        random = new SecureRandom();
        randomName = new BigInteger(130, random).toString(32);
        String randomValue = randomName.substring(0, 7);
        Log.d("randomValue",randomValue);
        empData.put("admin",loggedINEmail);
        empData.put("groupEmailId",groupMail);
        empData.put("groupImage",profileUrl);
        empData.put("groupName",groupName);
        empData.put("randomName",randomValue);
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        for(int i=0;i<seprated.length;i++){
            String userName = seprated[i];
            if(i==0){
                empData.put("status","admin");
            }else{
                empData.put("status","user");
            }
            String reArrangeEmail = userName.replace(".", "-");
            databaseRef = firebaseDatabaseRef.getReference().child("group").child(reArrangeEmail).child(randomValue);
            databaseRef.setValue(empData);
        }

        return true;
    }
}
