package com.bluemapletech.hippatextapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
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
import com.bluemapletech.hippatextapp.utils.MailSender;

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
    SharedPreferences pref;
    SharedPreferences.Editor editor;
        List<User> userInfo = new ArrayList<User>();
        LayoutInflater inflater;
        Context context;
        private String loginMail;
    private  String loginChatPin;
    private String not_acp_user;
    private String role_user_val;
    private String loginSenderId;
        public EmployeeListOfRootBaseAdapter(Context context, List<User> user , UserDetailDto userDetailDto,String not_acp_user) {
            this.context = context;
            this.userInfo = user;
            this.loginMail = userDetailDto.getLoggedINEmail();
            this.loginChatPin = userDetailDto.getLoggedINChatPin();
            this.loginSenderId = userDetailDto.getLoginSenderId();
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
            mViewHolder.fieldName.setText(info.getUserName());
            String[] separated = info.getUserName().split("@");
            mViewHolder.rootNname.setText(separated[0]);
            convertView.findViewById(R.id.accept_root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     if (userInfo.get(position).getAuth().matches("3")) {
                         Log.d(TAG,"deleted accepted company");
                         AlertDialog.Builder alert = new AlertDialog.Builder(context);
                         // alert.setTitle("");
                         alert.setMessage("Do you want to accept '"+userInfo.get(position).getUserName()+"'!");
                         alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int whichButton) {
                                 acceptUser(userInfo.get(position).getUserName());
                             }
                         });
                         alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int whichButton) {
                                 dialog.cancel();
                             }
                         });
                         AlertDialog alertDialog = alert.create();
                         alertDialog.show();
                    }

                }
            });

            convertView.findViewById(R.id.chat_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Security check");
                    final EditText chatPinn = new EditText(context);
                    chatPinn.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    chatPinn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    chatPinn.setHint("Enter your chat pin");
                    alert.setView(chatPinn);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String srt = chatPinn.getEditableText().toString();
                            if (srt.matches(loginChatPin)) {
                                Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(toEmail, userInfo.get(position).getUserName());
                                intent.putExtra(fromEmail, loginMail);
                                intent.putExtra(sendId, loginSenderId);
                                intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                                intent.putExtra(firstName, userInfo.get(position).getFirstName());
                                intent.putExtra(lastName, userInfo.get(position).getLastName());
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, " Sorry! Chat pin does not match!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }
            });


            convertView.findViewById(R.id.reject_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userInfo.get(position).getAuth().matches("1") && !not_acp_user.matches("notAcceptUser")) {
                        Log.d(TAG,"deleted reject company");
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        // alert.setTitle("");
                        alert.setMessage("Do you want to reject '"+userInfo.get(position).getUserName()+"'!");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteUser(userInfo.get(position).getUserName());
                            }
                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                    }

                    if(!not_acp_user.matches("notAcceptUser") && role_user_val.matches("admin")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setTitle("Security check");
                        final EditText chatPinn = new EditText(context);
                        chatPinn.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        chatPinn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        chatPinn.setHint("Enter your chat pin");
                        alert.setView(chatPinn);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String srt = chatPinn.getEditableText().toString();
                                if (srt.matches(loginChatPin)) {
                                    Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(toEmail, userInfo.get(position).getUserName());
                                    intent.putExtra(fromEmail, loginMail);
                                    intent.putExtra(sendId, loginSenderId);
                                    intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                                    intent.putExtra(firstName, userInfo.get(position).getFirstName());
                                    intent.putExtra(lastName, userInfo.get(position).getLastName());
                                    context.startActivity(intent);
                                } else {
                                    Toast.makeText(context, " Sorry! Chat pin does not match!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                    }
                    if(not_acp_user.matches("notAcceptUser")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setTitle("Security check");
                        final EditText chatPinn = new EditText(context);
                        chatPinn.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        chatPinn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        chatPinn.setHint("Enter your chat pin");
                        alert.setView(chatPinn);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String srt = chatPinn.getEditableText().toString();
                                if (srt.matches(loginChatPin)) {
                                    Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(toEmail, userInfo.get(position).getUserName());
                                    intent.putExtra(fromEmail, loginMail);
                                    intent.putExtra(sendId, loginSenderId);
                                    intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                                    intent.putExtra(firstName, userInfo.get(position).getFirstName());
                                    intent.putExtra(lastName, userInfo.get(position).getLastName());
                                    context.startActivity(intent);
                                } else {
                                    Toast.makeText(context, " Sorry! Chat pin does not match!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
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

                    TextView text = (TextView) convertView.findViewById(R.id.root_name);
                    text.setTextColor(convertView.getResources().getColor(R.color.textColor));
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
                try {
                    MailSender runners = new MailSender();
                    String  value = "Root has been Rejected!";
                    runners.execute("Root has been Rejected!",value,"hipaatext123@gmail.com",userMail);

                } catch (Exception ex) {
                    // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
                }
                Toast.makeText(this.context, "Root has been Rejected!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.context, "Error while reject the company, please try again!", Toast.LENGTH_LONG).show();
            }
        }
    public void acceptUser(String userMail){
        final UserDao userDao = new UserDao();
        boolean result = userDao.acceptUser(userMail);
        if (result) {
            try {
                MailSender runners = new MailSender();
                String  value = "Root has been accepted!";
                runners.execute("Root has been accepted!",value,"hipaatext123@gmail.com",userMail);

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Toast.makeText(this.context, "Root has been accepted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.context, "Error while accepted the company, please try again!", Toast.LENGTH_LONG).show();
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
