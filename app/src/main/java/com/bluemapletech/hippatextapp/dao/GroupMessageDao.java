package com.bluemapletech.hippatextapp.dao;

import android.util.Base64;
import android.util.Log;

import com.bluemapletech.hippatextapp.model.Message;
import com.bluemapletech.hippatextapp.utils.PushNotification;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Win7v5 on 11/8/2016.
 */

public class GroupMessageDao {
    private static final FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
    private static final DatabaseReference sRef = mfireBaseDatabase.getReference();

    private static String convIds,loginSenderId;
    private FirebaseDatabase firebaseDatabaseRef;
    private static final String TAG = GroupMessageDao.class.getCanonicalName();
    DatabaseReference databaseRef;
    private static String  TextMessage;
    public static void saveMessage(Message message, String convoId){
        TextMessage = message.getMtext();
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
        String[] ids = {sendMail};
        Arrays.sort(ids);
        convIds = ids[0];
        HashMap<String, String> msg = new HashMap<>();
        msg.put("text", encoText);
        msg.put("email",message.getMsender());
        msg.put("tochatemail",message.getToChatEmail());
        msg.put("image",message.getImage());
        msg.put("dateandtime",dateValue);
        msg.put("isDeletedBy","");
        msg.put("senderId",message.getSenderId());
        DatabaseReference value = sRef.child("groupmessage").child("message").child(message.getRandomValue()).child("message").push();
        String urlValue = value.toString();
        String[] re = urlValue.split("/");
        msg.put("childByAppendId",re[7]);
       value.setValue(msg);
        String arr[] = message.getPushNotificationId().split(";");
        for(int i = 0; i < arr.length; i++){
            String reArrangeEmail = arr[i].trim().replace(".", "-");
            DatabaseReference databaseRef = sRef.child("userDetails").child(reArrangeEmail);
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   String notificationId = dataSnapshot.child("pushNotificationId").getValue(String.class);
                    try {
                        PushNotification runners = new PushNotification();
                        runners.execute("TCTText",TextMessage,notificationId);

                    } catch (Exception ex) {
                        Log.d("error","Exception error...");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public static GroupMessageDao.MessagesListener addMessagesListener(String convoId,String login_sender_id, final GroupMessageDao.MessagesCallbacks callbacks){
        GroupMessageDao.MessagesListener listener = new GroupMessageDao.MessagesListener(callbacks);
        sRef.child("groupmessage").child("message").child(convoId).child("message").addChildEventListener(listener);
        loginSenderId = login_sender_id;
        return listener;
    }


    public static void stop(MessagesListener listener){
        sRef.removeEventListener(listener);
    }

    public static class MessagesListener implements ChildEventListener {
        private GroupMessageDao.MessagesCallbacks callbacks;
        MessagesListener(GroupMessageDao.MessagesCallbacks callbacks){
            this.callbacks = callbacks;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Map<String,String> msg = (Map)dataSnapshot.getValue();
            String del_user = msg.get("isDeletedBy");
            HashMap<String, String> hm = new HashMap<String, String>();
            String del[] = del_user.split(";");
            for(int i=0;i< del.length;i++){
                hm.put(del[i],del[i]);
            }
            if(hm.get(loginSenderId) == null) {
                Message message = new Message();
                message.setMsender(msg.get("email"));
                message.setSenderId(msg.get("senderId"));
                String srt = msg.get("text");
                message.setImage(msg.get("image"));
                message.setChildappendid(msg.get("childByAppendId"));
                byte[] data1 = Base64.decode(srt, Base64.NO_WRAP);
                String text = null;
                try {
                    text = new String(data1, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                message.setMtext(text);
                message.setDateAndTime(msg.get("dateandtime"));
                if (callbacks != null) {
                    callbacks.onMessageAdded(message);
                }
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
        void onMessageAdded(Message message);
    }
   /* private static  class AsyncTaskRunnerss extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            Object json = null;
            try {
                URL url1;
                url1 = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "key=AIzaSyAuVyl2BdhVZIw1sjplD41sY8utywdz8_k");
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
    }*/

}
