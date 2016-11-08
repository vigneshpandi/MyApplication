package com.bluemapletech.hippatextapp.dao;

import android.util.Base64;
import android.util.Log;

import com.bluemapletech.hippatextapp.model.Message;
import com.bluemapletech.hippatextapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
        boolean success = false;
        Log.d(TAG, "Create employee dao method has been called!");
        HashMap<String, Object> empData = new HashMap<>();
        empData.put("auth",user.getAuth());
        empData.put("chatPin",user.getChatPin());
        empData.put("companyCINNumber",user.getTINorEIN());
        empData.put("companyName", user.getCompanyName());
        empData.put("designation",user.getDesignation());
        empData.put("emailAddress", user.getUserName());
        empData.put("employeeId", user.getEmpId());
        empData.put("firstName",user.getFirstName());
        empData.put("lastName",user.getLastName());
        empData.put("password",user.getPassword());
        empData.put("profilePhoto",user.getProfilePjhoto());
        empData.put("providerNPIId",user.getProviderNPIId());
        empData.put("providerName",user.getProviderName());
        empData.put("role", user.getRole());
        empData.put("senderId",user.getSenderId());
        empData.put("status",user.getStatus());
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(empData);
        return true;
    }

    public boolean createCompany(User user){
        boolean success = false;
        Log.d(TAG, "Create company dao method has been called!");
        HashMap<String, Object> compData = new HashMap<>();
        compData.put("auth",user.getAuth());
        compData.put("chatPin",user.getChatPin());
        compData.put("companyCINNumber",user.getTINorEIN());
        compData.put("companyName", user.getCompanyName());
        compData.put("designation",user.getDesignation());
        compData.put("emailAddress", user.getUserName());
        compData.put("employeeId", user.getEmpId());
        compData.put("firstName",user.getFirstName());
        compData.put("lastName",user.getLastName());
        compData.put("password",user.getPassword());
        compData.put("profilePhoto",user.getProfilePjhoto());
        compData.put("providerNPIId",user.getProviderNPIId());
        compData.put("providerName",user.getProviderName());
        compData.put("role", user.getRole());
        compData.put("senderId",user.getSenderId());
        compData.put("status",user.getStatus());
            String reArrangeEmail = user.getUserName().replace(".", "-");
            firebaseDatabaseRef = FirebaseDatabase.getInstance();
            DatabaseReference dataReference = firebaseDatabaseRef.getReference().child("registeredCompanyName").child(user.getCompanyName()).child("companyName");
            dataReference.setValue(compData.get("companyName"));
            DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(compData);
       /* Task<Void> result =  databaseRef.setValue(compData);
        if(result.isSuccessful()){
            success = true;
        }*/
        return true;
    }


    public boolean acceptedEmployee(User user) {
        Log.d(TAG, "Add invited company dao method has been called!");
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        Log.d("sdsdsdsd","ssdsdsdsdsdsdsdsdsd"+reArrangeEmail);
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue(user.getAuth());
        return true;
    }

    public boolean pendingEmployee(User user) {
        Log.d(TAG, "Add invited company dao method has been called!");
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue(user.getAuth());
        return true;
    }

    public boolean deleteEmployee(User user) {
        Log.d(TAG, "Add invited company dao method has been called!");
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue(user.getAuth());
        return true;
    }



    public static void saveMessage(Message message, String convoId){
         String  TextMessage = message.getMtext();
        byte[] data = new byte[0];
        try {
            data = TextMessage.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encoText = Base64.encodeToString(data, Base64.NO_WRAP);
        Date date = new Date();
        Log.d(TAG,"date"+date);
        Calendar c = Calendar.getInstance();
        String myFormat = "dd/MM/yy, hh:mm:aa";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
          String dateValue = sdf.format(c.getTime());
        String sendMail = message.getMsender().replace(".", "-");
        String toMail = message.getToChatEmail().replace(".", "-");
        String[] ids = {sendMail,"+", toMail};
        Arrays.sort(ids);
        convIds = ids[1]+ids[0]+ids[2];
        HashMap<String, String> msg = new HashMap<>();
        msg.put("text", encoText);
        msg.put("email",message.getMsender());
        msg.put("tochatemail",message.getToChatEmail());
        msg.put("image","");
        msg.put("dateandtime",dateValue);
        msg.put("senderId",message.getSenderId());
        DatabaseReference value = sRef.child("messages").child(convIds).child("chat").push();
        Log.d("rootMessage",value.toString());
        String urlValue = value.toString();
        String[] re = urlValue.split("/");
        Log.d("values",re[6]);
        msg.put("childappendid",re[6]);
        value.setValue(msg);
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
            String srt = msg.get("text");
            byte[] data1 = Base64.decode(srt, Base64.NO_WRAP);
            String text = null;
            try {
                text = new String(data1, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            message.setMtext(text);
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
        boolean success = false;
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
        compData.put("employeeId",user.getEmpId());
        compData.put("firstName","");
        compData.put("lastName","");
        compData.put("profilePhoto","");
        compData.put("senderId",user.getSenderId());

        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
       Task<Void> result =  databaseRef.setValue(compData);
         if(result.isSuccessful()){
            success = true;
        }
        return success;
    }


}
