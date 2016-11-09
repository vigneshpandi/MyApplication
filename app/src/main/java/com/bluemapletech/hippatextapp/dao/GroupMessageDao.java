package com.bluemapletech.hippatextapp.dao;

import android.util.Base64;
import android.util.Log;

import com.bluemapletech.hippatextapp.model.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private static String convIds;
    private FirebaseDatabase firebaseDatabaseRef;
    private static final String TAG = GroupMessageDao.class.getCanonicalName();
    DatabaseReference databaseRef;
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
        msg.put("image","");
        msg.put("dateandtime",dateValue);
        msg.put("senderId",message.getSenderId());
        DatabaseReference value = sRef.child("groupmessage").child("message").child(message.getRandomValue()).child("message").push();
        Log.d("rootMessage",value.toString());
        String urlValue = value.toString();
        String[] re = urlValue.split("/");
        msg.put("childappendid",re[7]);
       value.setValue(msg);
    }
    public static GroupMessageDao.MessagesListener addMessagesListener(String convoId, final GroupMessageDao.MessagesCallbacks callbacks){
        GroupMessageDao.MessagesListener listener = new GroupMessageDao.MessagesListener(callbacks);
        sRef.child("groupmessage").child("message").child(convoId).child("message").addChildEventListener(listener);
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
            Message message = new Message();
            message.setMsender(msg.get("email"));
            message.setSenderId(msg.get("senderId"));
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
}
