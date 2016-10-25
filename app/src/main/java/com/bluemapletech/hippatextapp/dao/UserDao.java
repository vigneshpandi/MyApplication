package com.bluemapletech.hippatextapp.dao;

import android.util.Log;

import com.bluemapletech.hippatextapp.model.Message;
import com.bluemapletech.hippatextapp.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kumaresan on 19-10-2016.
 */

public class UserDao {
    private static final FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
    private static final DatabaseReference sRef = mfireBaseDatabase.getReference();

     private static String convIds;
    private FirebaseDatabase firebaseDatabaseRef;

    DatabaseReference databaseRef;

    private static final String TAG = UserDao.class.getCanonicalName();

    public boolean createEmployee(User user){
        Log.d(TAG, "Create employee dao method has been called!");
        HashMap<String, Object> empData = new HashMap<>();
        empData.put("auth","0");
        empData.put("chatPin","");
        empData.put("companyCINNumber",user.getTINorEIN());
        empData.put("companyName", user.getCompanyName());
        empData.put("designation","");
        empData.put("emailAddress", user.getUserName());
        empData.put("employeeId", user.getEmpId());
        empData.put("firstName","");
        empData.put("lastName","");
        empData.put("password",user.getPassword());
        empData.put("profilePhoto","");
        empData.put("providerNPIId","");
        empData.put("providerName","");
        empData.put("role", user.getRole());
        empData.put("senderId","");
        empData.put("status",user.getStatus());
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(empData);
        return true;
    }

    public boolean createCompany(User user){
        Log.d(TAG, "Create company dao method has been called!");
        HashMap<String, Object> compData = new HashMap<>();
        compData.put("auth","0");
        compData.put("chatPin",user.getChatPin());
        compData.put("companyCINNumber",user.getTINorEIN());
        compData.put("companyName", user.getCompanyName());
        compData.put("emailAddress", user.getUserName());
        compData.put("password",user.getPassword());
        compData.put("providerNPIId",user.getProviderNPIId());
        compData.put("providerName",user.getProviderName());
        compData.put("role", user.getRole());
        compData.put("status",user.getStatus());
        compData.put("designation","");
        compData.put("employeeId","");
        compData.put("firstName","");
        compData.put("lastName","");
        compData.put("profilePhoto","");
        compData.put("senderId","");

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
        invite.put("auth", "1");
       invite.put("chatPin",user.getChatPin());
        invite.put("employeeId", user.getEmpId());
        invite.put("companyName", user.getCompanyName());
        invite.put("emailAddress", user.getUserName());
        invite.put("password", user.getPassword());
        invite.put("role", user.getRole());
        invite.put("status",user.getStatus());
        invite.put("designation","");
        invite.put("firstName","");
        invite.put("lastName","");
        invite.put("profilePhoto","");
        invite.put("providerNPIId","");
        invite.put("providerName","");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(invite);
        return true;
    }
    public static void saveMessage(Message message, String convoId){
        String sendMail = message.getMsender().replace(".", "-");
        String toMail = message.getToChatEmail().replace(".", "-");
        String[] ids = {sendMail,"+", toMail};
        Arrays.sort(ids);
        convIds = ids[1]+ids[0]+ids[2];

        HashMap<String, String> msg = new HashMap<>();
        msg.put("text", message.getMtext());
        msg.put("email",message.getMsender());
        msg.put("tochatemail",message.getToChatEmail());
        msg.put("image","");
        msg.put("childappendid","");
        msg.put("dateandtime","");
        msg.put("senderId","");
        sRef.child("messages").child(convIds).child("chat").push().setValue(msg);
    }


    public static MessagesListener addMessagesListener(String convoId, final MessagesCallbacks callbacks){
        MessagesListener listener = new MessagesListener(callbacks);
        sRef.child("messages").child(convoId).child("chat").addChildEventListener(listener);
        return listener;
    }


    /*public static void stop(MessagesListener listener){
        sRef.removeEventListener(listener);
    }*/

    public static class MessagesListener implements ChildEventListener {
        private MessagesCallbacks callbacks;
        MessagesListener(MessagesCallbacks callbacks){
            this.callbacks = callbacks;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Map<String,String> msg = (Map)dataSnapshot.getValue();
            Message message = new Message();
            message.setMsender(msg.get("email"));
            message.setMtext(msg.get("text"));
            if(callbacks != null){
                callbacks.onMessageAdded(message);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    }


    public interface MessagesCallbacks{
        public void onMessageAdded(Message message);
    }

    public boolean saveSecure(User user){
        Log.d(TAG, "Create company dao method has been called!");
        HashMap<String, Object> compData = new HashMap<>();
        compData.put("auth","1");
        compData.put("chatPin",user.getChatPin());
        compData.put("companyCINNumber",user.getTINorEIN());
        compData.put("companyName", user.getCompanyName());
        compData.put("emailAddress", user.getUserName());
        compData.put("password",user.getPassword());
        compData.put("providerNPIId",user.getProviderNPIId());
        compData.put("providerName",user.getProviderName());
        compData.put("role", user.getRole());
        compData.put("status",user.getStatus());
        compData.put("designation","");
        compData.put("employeeId","");
        compData.put("firstName","");
        compData.put("lastName","");
        compData.put("profilePhoto","");
        compData.put("senderId","");

        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = firebaseDatabaseRef.getReference().child("companyName").child(user.getCompanyName()).child("companyName");
        dataReference.setValue(compData.get("companyName"));
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(compData);
        return true;
    }

   /* public UserDao getActivity(){ return this;}*/

}
