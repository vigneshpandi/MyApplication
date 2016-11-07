package com.bluemapletech.hippatextapp.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageEmployeeBaseAdpter;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ChatEmployeeActivity extends AppCompatActivity implements View.OnClickListener,UserDao.MessagesCallbacks {
    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
    private ListView mListView;
    private String mConvoId;
    private UserDao.MessagesListener mListener;
    private String toMail;
    private String fromMail, senderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caht_employee);
        toMail = getIntent().getStringExtra(PageEmployeeBaseAdpter.toEmail);
        fromMail = getIntent().getStringExtra(PageEmployeeBaseAdpter.fromEmail);
        senderId = getIntent().getStringExtra(PageEmployeeBaseAdpter.sendId);
        mListView = (ListView)findViewById(R.id.message_list);
        mMessages = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessages);
        mListView.setAdapter(mAdapter);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button sendMessage = (Button)findViewById(R.id.send_message);

        sendMessage.setOnClickListener(this);
        String toMails = toMail.replace(".", "-");
        String fromMails = fromMail.replace(".", "-");
        String[] ids = {toMails,"+", fromMails};
        Arrays.sort(ids);
        mConvoId = ids[1]+ids[0]+ids[2];
        Log.d("mConvoId",mConvoId);
       mListener = UserDao.addMessagesListener(mConvoId, this);

    }

    public void onClick(View v) {
        EditText newMessageView = (EditText)findViewById(R.id.new_message);
        String newMessage = newMessageView.getText().toString();
        newMessageView.setText("");
        Message msg = new Message();
        msg.setMtext(newMessage);
        msg.setMsender(fromMail);
        msg.setToChatEmail(toMail);
        Log.d("sendeerID",senderId);
        msg.setSenderId(senderId);
        UserDao.saveMessage(msg, mConvoId);
    }

    @Override
    public void onMessageAdded(Message message) {
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //UserDao.stop(mListener);
    }
    private class MessagesAdapter extends ArrayAdapter<Message> {
        MessagesAdapter(ArrayList<Message> messages){
            super(ChatEmployeeActivity.this, R.layout.item, R.id.msg, messages);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            Message message = getItem(position);
            TextView nameView = (TextView)convertView.findViewById(R.id.msg);
            nameView.setText(message.getMtext());
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();
            int sdk = Build.VERSION.SDK_INT;
            if (message.getMsender().equals(fromMail)){
                Log.d("this is login id chat",fromMail);
               if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getResources().getDrawable(R.drawable.bubble2));
                    layoutParams.gravity = Gravity.RIGHT;
                } else{
                    nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble2));
                    layoutParams.gravity = Gravity.RIGHT;
                }
               /* layoutParams.gravity = Gravity.RIGHT;*/
            }else{
                if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getResources().getDrawable(R.drawable.bubble1));
                    layoutParams.gravity = Gravity.LEFT;
                } else{
                    nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble1));
                    layoutParams.gravity = Gravity.LEFT;
                }
           /* layoutParams.gravity = Gravity.RIGHT;*/
            }
            nameView.setLayoutParams(layoutParams);
            return convertView;
        }
    }
}
