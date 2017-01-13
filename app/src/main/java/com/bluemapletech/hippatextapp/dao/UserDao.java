package com.bluemapletech.hippatextapp.dao;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.bluemapletech.hippatextapp.model.Message;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.MailSender;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private static FirebaseDatabase firebaseDatabaseRef;
    private static DatabaseReference databaseRef;
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
        empData.put("pushNotificationId","");
        empData.put("createdDate",user.getCreateDate());
        empData.put("updatedDate",user.getUpdateDate());
        empData.put("showOnline",user.getIsOnlie());
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(empData);
        return true;
    }

    public boolean createCompany(User user){
        boolean success = false;
        Log.d(TAG, "Create company dao method has been called!"+ user.getUserName());
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
        compData.put("pushNotificationId","");
        compData.put("createdDate",user.getCreateDate());
        compData.put("updatedDate",user.getUpdateDate());
        compData.put("showOnline",user.getIsOnlie());
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = firebaseDatabaseRef.getReference().child("registeredCompanyName").child(user.getCompanyName()).child("companyName");
        dataReference.setValue(compData.get("companyName"));
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(compData);
        return true;
    }


    public boolean acceptedEmployee(User user) {
        Log.d(TAG, "Add invited company dao method has been called!");
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue(user.getAuth());
        try {
            String acceptEmail = user.getUserName().replace("-", ".");
            MailSender runners = new MailSender();
            String value = "This email is to notify you that your profile has been accepted by admin.\n" +
                    "Thanks for showing interest.";
            runners.execute("Profile has been accepted!",value,"hipaatext123@gmail.com",acceptEmail);

        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
        }
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
        try {
            String acceptEmail = user.getUserName().replace("-", ".");
            MailSender runners = new MailSender();
            String value = "This email is to notify you that your profile has been rejected by admin.\n" +
                    "Thanks for showing interest.";
            runners.execute("Profile has been rejected!",value,"hipaatext123@gmail.com",acceptEmail);

        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
        }
        return true;
    }



    public static void saveMessage(Message message, String convoId){
        boolean success = false;
        String  TextMessage = message.getMtext();
        byte[] data = new byte[0];
        try {
            data = TextMessage.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encoText = Base64.encodeToString(data, Base64.NO_WRAP);
        Date date = new Date();
        Calendar c = Calendar.getInstance();

        String myFormat = "yyyy-MM-dd HH:mm:ss Z";
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
        msg.put("image",message.getImage());
        msg.put("dateandtime",dateValue);
        msg.put("senderId",message.getSenderId());
        msg.put("image",message.getImage());
        DatabaseReference value = sRef.child("messages").child(convIds).child("chat").push();
        String urlValue = value.toString();
        String[] re = urlValue.split("/");
        msg.put("childappendid",re[6]);
        Task<Void> result =  value.setValue(msg);
        AsyncTaskRunners runner = new AsyncTaskRunners();
        runner.execute(message.getPushNotificationId(),message.getMtext());
    }

    public static MessagesListener addMessagesListener(String convoId, final MessagesCallbacks callbacks){
        MessagesListener listener = new MessagesListener(callbacks);
        sRef.child("messages").child(convoId).child("chat").addChildEventListener(listener);
        return listener;
    }



    public static void stop(MessagesListener listener){
        sRef.removeEventListener(listener);
    }

    public boolean saveSecurePin(User user) {
        Log.d(TAG,"secureChat userDao called!");
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("chatPin");
        databaseRef.setValue(user.getChatPin());
        try {
            //  new MyAsyncClass().execute();
            String loggedInEmail = user.getUserName().replace("-", ".");
            MailSender runners = new MailSender();
            String  value = "Your chat pin has been changed successfully in HippaText.\n" +
                    "Please use new chat pin:"+ user.getChatPin()+"\n"+
                    "Thanks for showing your interest.";
            runners.execute("Chat Pin has been changed!",value,"hipaatext123@gmail.com",loggedInEmail);

        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
        }
        return true;
    }

    public static void deleteChatMessage(Message message, String mConvoId) {
        String childappendid =  message.getChildappendid();
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference().child("messages").child(mConvoId).child("chat").child(childappendid);
        databaseRef.removeValue();
        return;
    }

    public static void deleteGroupChatMessage(Message message, String mConvoId) {
        String childappendid =  message.getChildappendid();
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference().child("groupmessage").child("message").child(mConvoId).child("message").child(childappendid);
        databaseRef.removeValue();
        return;
    }

    public boolean isOnline(User user) {
        Log.d("loginMail","loginMailll"+user.getUserName());
        String reArrangeEmailId = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmailId).child("showOnline");
        databaseRef.setValue(user.getIsOnlie());
        return true;
    }

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
            message.setImage(msg.get("image"));
            message.setChildappendid(msg.get("childappendid"));
            byte[] data1 = Base64.decode(srt, Base64.NO_WRAP);
            String text = null;
            try {
                text = new String(data1, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            message.setMtext(text);
            message.setDateAndTime(msg.get("dateandtime"));
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
    public boolean deleteUser(String userMail) {
        String reArrangeEmail = userMail.replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue("3");
        try {
            String acceptEmail = userMail.replace("-", ".");
            MailSender runners = new MailSender();
            String value = "This email is to notify you that your profile has been rejected by admin.\n" +
                    "Thanks for showing interest.";
            runners.execute("Profile has been rejected!",value,"hipaatext123@gmail.com",acceptEmail);

        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
        }
        return  true;
    }
    public boolean deleteAccount(String userMail) {
        String reArrangeEmail = userMail.replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.removeValue();
        try {
            String acceptEmail = userMail.replace("-", ".");
            MailSender runners = new MailSender();
            String value = "This email is to notify you that your profile has been deleted by admin.\n" +
                    "Thanks for showing interest.";
            runners.execute("Profile has been deleted!",value,"hipaatext123@gmail.com",acceptEmail);

        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
        }
        return  true;
    }

    public boolean acceptUser(String userMail) {
        String reArrangeEmail = userMail.replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue("1");
        try {
            String acceptEmail = userMail.replace("-", ".");
            MailSender runners = new MailSender();
            String value = "This email is to notify you that your profile has been accepted by admin.\n" +
                    "Thanks for showing interest.";
            runners.execute("Profile has been accepted!",value,"hipaatext123@gmail.com",acceptEmail);

        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
        }
        return  true;
    }


    public interface MessagesCallbacks{
        void onMessageAdded(Message message);
    }

    public boolean saveSecure(User user){
        boolean success = false;
        HashMap<String, Object> compData = new HashMap<>();
        compData.put("auth",user.getAuth());
        compData.put("chatPin",user.getChatPin());
        compData.put("companyCINNumber",user.getTINorEIN());
        compData.put("companyName", user.getCompanyName());
        compData.put("emailAddress", user.getUserName());
        compData.put("password",user.getPassword());
        compData.put("providerNPIId",user.getProviderNPIId());
        compData.put("providerName",user.getProviderName());
        compData.put("role", user.getRole());
        compData.put("status",user.getStatus());
        compData.put("designation",user.getDesignation());
        compData.put("employeeId",user.getEmpId());
        compData.put("firstName",user.getFirstName());
        compData.put("lastName",user.getLastName());
        compData.put("profilePhoto",user.getProfilePjhoto());
        compData.put("senderId",user.getSenderId());
        compData.put("pushNotificationId","");
        compData.put("createdDate",user.getCreateDate());
        compData.put("updatedDate",user.getUpdateDate());
        compData.put("showOnline",user.getIsOnlie());
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.setValue(compData);
        /*Task<Void> result =  databaseRef.setValue(compData);
        if(result.isSuccessful()){
            success = true;
        }
        return success;*/
        return true;
    }

    private static  class AsyncTaskRunners extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            Object json = null;
            try {
                URL url1;
                url1 = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "key= AIzaSyBWM1_H5KrB9EPGcf0iIK_8hh8kh1YqhAE");
                JSONObject root = new JSONObject();
                root.put("title","TCTText");
                root.put("body",params[1]);
                JSONObject root1 = new JSONObject();
                root1.put("notification",root);
                root1.put("to",params[0]);
                root1.put("priority","high");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(root1.toString());
                wr.flush();
                wr.close();
                int responsecode = conn.getResponseCode();

                if(responsecode == 200) {
                    Log.d(TAG,"success"+conn.getResponseMessage());
                }else{
                    Log.d(TAG,"error"+conn.getResponseMessage());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    public UserDao getActivity() {
        return this;
    }
}
