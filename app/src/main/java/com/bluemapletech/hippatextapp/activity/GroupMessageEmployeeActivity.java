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
import com.bluemapletech.hippatextapp.adapter.EmployeeGroupsAdapter;
import com.bluemapletech.hippatextapp.adapter.PageEmployeeBaseAdpter;
import com.bluemapletech.hippatextapp.dao.GroupMessageDao;
import com.bluemapletech.hippatextapp.model.Message;

import java.util.ArrayList;
import java.util.Arrays;

public class GroupMessageEmployeeActivity extends AppCompatActivity implements View.OnClickListener,GroupMessageDao.MessagesCallbacks {
    private ArrayList<Message> mMessages;
        private GroupMessageEmployeeActivity.MessagesAdapter mAdapter;
        private ListView mListView;
        private String mConvoId;
        private GroupMessageDao.MessagesListener mListener;
        private String randomValue;
        private String fromMail, senderId;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_group_message_employee);

            fromMail = getIntent().getStringExtra(EmployeeGroupsAdapter.fromMail);
            senderId = getIntent().getStringExtra(EmployeeGroupsAdapter.senderId);
            randomValue = getIntent().getStringExtra(EmployeeGroupsAdapter.randomValue);

            mListView = (ListView)findViewById(R.id.message_list);
            mMessages = new ArrayList<>();
            mAdapter = new GroupMessageEmployeeActivity.MessagesAdapter(mMessages);
            mListView.setAdapter(mAdapter);
            if (getSupportActionBar() != null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            Button sendMessage = (Button)findViewById(R.id.send_message);

            sendMessage.setOnClickListener(this);
            String fromMails = fromMail.replace(".", "-");
            String[] ids = {fromMails};
            Arrays.sort(ids);
            mConvoId = ids[0];
            Log.d("mConvoId",mConvoId);
            mListener = GroupMessageDao.addMessagesListener(randomValue, this);

        }

        public void onClick(View v) {
            EditText newMessageView = (EditText)findViewById(R.id.new_message);
            String newMessage = newMessageView.getText().toString();
            newMessageView.setText("");
            Message msg = new Message();
            msg.setMtext(newMessage);
            msg.setMsender(fromMail);
            msg.setSenderId(senderId);
            msg.setRandomValue(randomValue);
            GroupMessageDao.saveMessage(msg, randomValue);
        }

        @Override
        public void onMessageAdded(Message message) {
            mMessages.add(message);
            mAdapter.notifyDataSetChanged();
        }
        @Override
        protected void onDestroy() {
            super.onDestroy();
            GroupMessageDao.stop(mListener);
        }
        private class MessagesAdapter extends ArrayAdapter<Message> {
            MessagesAdapter(ArrayList<Message> messages){
                super(GroupMessageEmployeeActivity.this, R.layout.item, R.id.msg, messages);
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = super.getView(position, convertView, parent);
                Message message = getItem(position);
                TextView nameView = (TextView)convertView.findViewById(R.id.msg);
                nameView.setText(message.getMtext());
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();
                int sdk = Build.VERSION.SDK_INT;
                if (message.getSenderId().equals(senderId)){
                    Log.d("this is login id chat",fromMail);
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nameView.setBackground(getResources().getDrawable(R.drawable.bubble2));
                        layoutParams.gravity = Gravity.RIGHT;
                    } else{
                        nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble2));
                        layoutParams.gravity = Gravity.RIGHT;
                    }
                    //layoutParams.gravity = Gravity.RIGHT;
                }else{
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nameView.setBackground(getResources().getDrawable(R.drawable.bubble1));
                        layoutParams.gravity = Gravity.LEFT;
                    } else{
                        nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble1));
                        layoutParams.gravity = Gravity.LEFT;
                    }
                    // layoutParams.gravity = Gravity.RIGHT;
                }
                nameView.setLayoutParams(layoutParams);
                return convertView;
            }
    }
}
