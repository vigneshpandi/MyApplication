package com.bluemapletech.hippatextapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.activity.ChatEmployeeActivity;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.model.UserDetailDto;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by BlueMaple on 1/5/2017.
 */

public class PageAdminChatAdapter  extends BaseAdapter {
    private static final String TAG = PageBaseAdapter.class.getCanonicalName();
    public static final String toEmail = "toEmail";
    public static final String fromEmail = "fromEmail";
    public static final String sendId = "sendId";
    public static final String notificationId = "notificationId";
    public static final String firstName = "firstName";
    public static final String lastName = "lastName";
    public static final String role = "userRole";
    LayoutInflater inflater;
    Context context;
    private String fromMAil;
    private String chatPin;
    private String userFirstName;
    private String userLastName;
    private AlertDialog.Builder alertDialog;
    List<User> userInfo = new ArrayList<User>();
    HashMap<String,String> onlineHash = new HashMap<String, String>();
    UserDetailDto userDetailDtos =new UserDetailDto();
    public PageAdminChatAdapter(Context context, List<User> user, UserDetailDto userDto, String loggedINChatPin,HashMap<String,String> hashValue) {
        this.context = context;
        this.userInfo = user;
        this.fromMAil = userDto.getLoggedINEmail();
        this.userDetailDtos = userDto;
        this.chatPin = loggedINChatPin;
        this.onlineHash = hashValue;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
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
    public View getView(final int position, View convertView, final ViewGroup parent) {

        PageAdminChatAdapter.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_admin_chat, parent, false);
            mViewHolder = new PageAdminChatAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (PageAdminChatAdapter.MyViewHolder) convertView.getTag();
        }

        final User info = getItem(position);
        mViewHolder.fieldName.setText(info.getUserName());
        mViewHolder.fieldId.setText(info.getEmpId());

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
                        byte[] data1 = Base64.decode(chatPin, Base64.NO_WRAP);
                        String text = null;
                        try {
                            text = new String(data1, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (srt.matches(text)) {
                            Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(toEmail, userInfo.get(position).getUserName());
                            intent.putExtra(fromEmail, fromMAil);
                            intent.putExtra(sendId, userDetailDtos.getLoginSenderId());
                            intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                            intent.putExtra(firstName, userInfo.get(position).getFirstName());
                            intent.putExtra(lastName, userInfo.get(position).getLastName());
                            intent.putExtra(role,userInfo.get(position).getRole());
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


        if((onlineHash != null)) {
            if (onlineHash.containsKey(info.getUserName())) {
                Log.d("it is  online", "success");
                View img = convertView.findViewById(R.id.onlineImageView);
                img.setVisibility(View.VISIBLE);
            } else {
                Log.d("it is not online", "success");
                View img = convertView.findViewById(R.id.onlineImageView);
                img.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    private class MyViewHolder {
        private TextView fieldId, fieldName;

        public MyViewHolder(View item) {
            fieldId = (TextView) item.findViewById(R.id.user_id);
            fieldName = (TextView) item.findViewById(R.id.layout_field_name);
        }
    }
}
