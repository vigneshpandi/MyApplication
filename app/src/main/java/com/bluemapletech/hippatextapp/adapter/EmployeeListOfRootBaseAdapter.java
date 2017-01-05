package com.bluemapletech.hippatextapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
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
import com.bluemapletech.hippatextapp.activity.ViewUserDetails;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.model.UserDetailDto;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BlueMaple on 12/9/2016.
 */

public class EmployeeListOfRootBaseAdapter   extends BaseAdapter {
    private static final String TAG = EmployeeListOfRootBaseAdapter.class.getCanonicalName();
    public static final String toEmail = "toEmail";
    public static final String fromEmail = "fromEmail";
    public static final String sendId = "sendId";
    public static final String notificationId = "notificationId";
    public static final String firstName = "firstName";
    public static final String lastName = "lastName";

    public static final String userEmails = "userEmails";
    public static final String userAuth = "userAuth";

        List<User> userInfo = new ArrayList<User>();
        LayoutInflater inflater;
        Context context;
        private String loginMail;
    private  String loginChatPin;
    private String not_acp_user;
    private String role_user_val;
        public EmployeeListOfRootBaseAdapter(Context context, List<User> user , UserDetailDto userDetailDto,String not_acp_user) {
            this.context = context;
            this.userInfo = user;
            this.loginMail = userDetailDto.getLoggedINEmail();
            this.loginChatPin = userDetailDto.getLoggedINChatPin();
            this.not_acp_user = not_acp_user;
            this.role_user_val = userDetailDto.getRole_val_det();
            inflater = LayoutInflater.from(this.context);
        }


        public int getCount() {
            return userInfo.size();
        }

        @Override
        public User getItem(int position) {
            return userInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            MyViewHolder mViewHolder  = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.view_root_list, parent, false);
                mViewHolder = new EmployeeListOfRootBaseAdapter.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (EmployeeListOfRootBaseAdapter.MyViewHolder) convertView.getTag();
            }

            final User info = getItem(position);
            Log.d("getUserName",info.getUserName());
            mViewHolder.fieldName.setText(info.getUserName());
            String[] separated = info.getUserName().split("@");
            mViewHolder.rootNname.setText(separated[0]);
            convertView.findViewById(R.id.accept_root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     if (userInfo.get(position).getAuth().matches("3")) {
                        acceptUser(userInfo.get(position).getUserName());
                    }

                }
            });

            convertView.findViewById(R.id.chat_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(toEmail, userInfo.get(position).getUserName());
                    intent.putExtra(fromEmail, loginMail);
                    intent.putExtra(sendId, userInfo.get(position).getSenderId());
                    intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                    intent.putExtra(firstName, userInfo.get(position).getFirstName());
                    intent.putExtra(lastName, userInfo.get(position).getLastName());
                    context.startActivity(intent);
                        }

            });
            convertView.findViewById(R.id.reject_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userInfo.get(position).getAuth().matches("1") && !not_acp_user.matches("notAcceptUser")) {
                        deleteUser(userInfo.get(position).getUserName());
                    }

                    if(!not_acp_user.matches("notAcceptUser") && role_user_val.matches("admin")){
                        Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(toEmail, userInfo.get(position).getUserName());
                        intent.putExtra(fromEmail, loginMail);
                        intent.putExtra(sendId, userInfo.get(position).getSenderId());
                        intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                        intent.putExtra(firstName, userInfo.get(position).getFirstName());
                        intent.putExtra(lastName, userInfo.get(position).getLastName());
                        context.startActivity(intent);
                    }
                    if(not_acp_user.matches("notAcceptUser")) {
                        Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(toEmail, userInfo.get(position).getUserName());
                        intent.putExtra(fromEmail, loginMail);
                        intent.putExtra(sendId, userInfo.get(position).getSenderId());
                        intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                        intent.putExtra(firstName, userInfo.get(position).getFirstName());
                        intent.putExtra(lastName, userInfo.get(position).getLastName());
                        context.startActivity(intent);
                    }
                }
            });
            convertView.findViewById(R.id.root_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!userInfo.get(position).getRole().matches("root")) {
                        Intent intent = new Intent(context, ViewUserDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(userEmails, userInfo.get(position).getUserName());
                        intent.putExtra(userAuth, userInfo.get(position).getAuth());
                        context.startActivity(intent);
                    }
                }
            });
            if (userInfo.get(position).getAuth().matches("1")) {
                if(not_acp_user.matches("notAcceptUser")){
                    Button btn = (Button) convertView.findViewById(R.id.reject_btn);
                    btn.setText("Chat");
                    btn.setBackgroundColor(Color.parseColor("#009193"));
                }
                View btn = convertView.findViewById(R.id.accept_root);
                btn.setVisibility(View.INVISIBLE);

                View btns = convertView.findViewById(R.id.chat_btn);
                btns.setVisibility(View.INVISIBLE);
               /* View btn3 = (Button) convertView.findViewById(R.id.chat_btn);
                btns.setVisibility(btns.INVISIBLE);*/
            }

            if (userInfo.get(position).getAuth().matches("3")) {
                if(role_user_val.matches("admin")){
                    View btns = convertView.findViewById(R.id.accept_root);
                    btns.setVisibility(View.INVISIBLE);
                    View btns1 = convertView.findViewById(R.id.chat_btn);
                    btns1.setVisibility(View.INVISIBLE);
                    Button btn = (Button) convertView.findViewById(R.id.reject_btn);
                    btn.setText("Chat");
                    btn.setBackgroundColor(Color.parseColor("#009193"));
                }else{
                    View btn = convertView.findViewById(R.id.reject_btn);
                    btn.setVisibility(View.INVISIBLE);
                }

            }
            return convertView;
        }

        public void deleteUser(String userMail) {
            final UserDao userDao = new UserDao();
            boolean result = userDao.deleteUser(userMail);
            if (result) {
                Toast.makeText(this.context, "Company has been Rejected by the admin!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.context, "Error while delete the company, please try again!", Toast.LENGTH_LONG).show();
            }
        }
    public void acceptUser(String userMail){
        final UserDao userDao = new UserDao();
        boolean result = userDao.acceptUser(userMail);
        if (result) {
            Toast.makeText(this.context, "Company has been Rejected by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.context, "Error while delete the company, please try again!", Toast.LENGTH_LONG).show();
        }

    }

        private class MyViewHolder {
            private TextView fieldName;
            private TextView rootNname;
            public MyViewHolder(View item) {
                fieldName = (TextView) item.findViewById(R.id.root_mail);
                rootNname = (TextView) item.findViewById(R.id.root_name);
            }
        }
}
