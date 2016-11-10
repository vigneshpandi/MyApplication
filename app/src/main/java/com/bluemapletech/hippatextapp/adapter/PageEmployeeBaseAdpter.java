package com.bluemapletech.hippatextapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.activity.ChatEmployeeActivity;
import com.bluemapletech.hippatextapp.model.User;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Win7v5 on 10/24/2016.
 */

public class PageEmployeeBaseAdpter extends BaseAdapter {
    private static final String TAG = PageBaseAdapter.class.getCanonicalName();
    public static final String toEmail = "toEmail";
    public static final String fromEmail = "fromEmail";
    public static final String sendId = "sendId";
    public static final String notificationId = "notificationId";
    LayoutInflater inflater;
    Context context;
    private String fromMAil;
    private String  chatPin;
    private AlertDialog.Builder alertDialog;
    List<User> userInfo = new ArrayList<User>();

    public PageEmployeeBaseAdpter(Context context, List<User> user,String fromEmail,String loggedINChatPin) {
        this.context = context;
        this.userInfo = user;
       this.fromMAil = fromEmail;
        this.chatPin=loggedINChatPin;
       inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return userInfo.size();
    }

    @Override
    public User getItem(int position) {
        return (User) userInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        PageEmployeeBaseAdpter.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_employee_list_view, parent, false);
            mViewHolder = new PageEmployeeBaseAdpter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (PageEmployeeBaseAdpter.MyViewHolder) convertView.getTag();
        }

        final User info = getItem(position);
        mViewHolder.fieldId.setText(info.getEmpId());
        mViewHolder.fieldName.setText(info.getUserName());

        ((Button) convertView.findViewById(R.id.chat_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   // chatEmployee(userInfo, position);
              Log.d("chatPin",chatPin);
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Security check");
                final EditText chatPinn = new EditText(context);
                chatPinn.setHint("Enter your chat pin");
                alert.setView(chatPinn);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String srt = chatPinn.getEditableText().toString();
                       Log.d("chatPin srt",srt);
                        byte[] data1 = Base64.decode(chatPin, Base64.NO_WRAP);
                        String text = null;
                        try {
                            text = new String(data1, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if(srt.matches(text)) {
                            Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(toEmail, userInfo.get(position).getUserName());
                            intent.putExtra(fromEmail, fromMAil);
                            intent.putExtra(sendId,userInfo.get(position).getSenderId());
                            intent.putExtra(notificationId,userInfo.get(position).getPushNotificationId());
                            context.startActivity(intent);
                        }else{
                            Toast.makeText(context, "Chat pin is not match!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            /* Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(toEmail, userInfo.get(position).getUserName());
                intent.putExtra(fromEmail, fromMAil);
                context.startActivity(intent);*/
            }
        });
        return convertView;
    }

    private class MyViewHolder {
        private TextView fieldId, fieldName;
        public MyViewHolder(View item) {
            fieldId = (TextView) item.findViewById(R.id.layout_field_id);
            fieldName = (TextView) item.findViewById(R.id.layout_field_name);
        }
    }

}
