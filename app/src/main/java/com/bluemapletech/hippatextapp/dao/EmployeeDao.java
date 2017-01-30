package com.bluemapletech.hippatextapp.dao;

import android.net.Uri;
import android.util.Log;

import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.PushNotification;
import com.bluemapletech.hippatextapp.model.Groups;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Win7v5 on 11/7/2016.
 */

public class EmployeeDao {
    private static final FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
    private static final DatabaseReference sRef = mfireBaseDatabase.getReference();
    private FirebaseDatabase firebaseDatabaseRef =  FirebaseDatabase.getInstance();
    DatabaseReference databaseRef;

    private static final String TAG = EmployeeDao.class.getCanonicalName();
    private SecureRandom random;
    private String randomName;
    public boolean createGroup(String loggedINEmail, String groupMail, String groupName , Uri downloadUrl,String groupPushNotiId) {
        String profileUrl = String.valueOf(downloadUrl);
        HashMap<String, Object> empData = new HashMap<>();
        Log.d(TAG, "CreateGroup employee dao method has been called!");
        String[] seprated = groupMail.split(";");
        Log.d("seprated","lebngthofthe seprated"+seprated.length);
        random = new SecureRandom();
        randomName = new BigInteger(130, random).toString(32);
        String randomValue = randomName.substring(0, 7);
        empData.put("admin",loggedINEmail);
        empData.put("groupEmailId",groupMail);
        empData.put("groupImage",profileUrl);
        empData.put("groupName",groupName);
        empData.put("randomName",randomValue);
        String login_mail = null;
       // firebaseDatabaseRef = FirebaseDatabase.getInstance();
        for(int i=0;i<seprated.length;i++){
            String userName = seprated[i];
            if(i==0){
                login_mail = userName;
                empData.put("status","admin");
            }else{
                empData.put("status","user");
            }
            String reArrangeEmail = userName.replace(".", "-");
            databaseRef = firebaseDatabaseRef.getReference().child("group").child(reArrangeEmail).child(randomValue);
            databaseRef.setValue(empData);
        }
String[] seprateNotificationId = groupPushNotiId.split(";");
        for(int j=0;j< seprateNotificationId.length; j++){
            Log.d("value","groupNotificstion"+seprateNotificationId[j]);
            try {
                PushNotification runners = new PushNotification();
                runners.execute("TCTText",login_mail+" "+"added  you",seprateNotificationId[j]);

            } catch (Exception ex) {
                Log.d("error","Exception error...");
            }
        }
        return true;
    }

    public boolean addMemberToGroup(String userName, Groups groupVal, String groupPushNotificationId, String selectUserPushNotId,String loginMail) {
        HashMap<String, Object> empData = new HashMap<>();
        String groupMail = groupVal.getGroupEmailId()+";"+userName;
        String groupMailId = groupVal.getGroupEmailId();
        String[] separated = groupMailId.split(";");
        empData.put("admin",groupVal.getAdmin());
        empData.put("groupEmailId",groupMail);
        empData.put("groupImage",groupVal.getGroupImage());
        empData.put("groupName",groupVal.getGroupName());
        empData.put("status","user");
        String reArrangeEmail = userName.replace(".", "-");
        empData.put("randomName",groupVal.getRandomName());
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference().child("group").child(reArrangeEmail).child(groupVal.getRandomName());
        Task<Void> result = databaseRef.setValue(empData);
     for(int i =0; i<separated.length; i++){
         String reArrangeEmailId = separated[i].replace(".", "-");
         databaseRef = firebaseDatabaseRef.getReference().child("group").child(reArrangeEmailId).child(groupVal.getRandomName()).child("groupEmailId");
         databaseRef.setValue(groupMail);
     }

        String[] seprateNotificationId = groupPushNotificationId.split(";");
        for(int j=0;j< seprateNotificationId.length; j++){
            Log.d("value","groupNotificstion"+seprateNotificationId[j]);
            try {
                PushNotification runners = new PushNotification();
                runners.execute("TCTText",loginMail+" "+"added"+" "+userName,seprateNotificationId[j]);

            } catch (Exception ex) {
                Log.d("error","Exception error...");
            }
        }

        try {
            PushNotification runners = new PushNotification();
            runners.execute("TCTText",loginMail+" "+"added  You",selectUserPushNotId);

        } catch (Exception ex) {
            Log.d("error","Exception error...");
        }

        return true;
    }

    public boolean empChangeAdmintoGroup(String randomName, String userMail) {
        String reArrangeEmail = userMail.replace(".", "-");
        databaseRef = firebaseDatabaseRef.getReference().child("group").child(reArrangeEmail).child(randomName).child("status");
        databaseRef.setValue("admin");
        return true;
    }
}
