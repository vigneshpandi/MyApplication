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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.activity.ChatEmployeeActivity;
import com.bluemapletech.hippatextapp.model.User;
import com.squareup.picasso.Picasso;

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

    public PageEmployeeBaseAdpter(Context context, List<User> user, String fromEmail, String loggedINChatPin) {
        this.context = context;
        this.userInfo = user;
        this.fromMAil = fromEmail;
        this.chatPin = loggedINChatPin;
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

        PageEmployeeBaseAdpter.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_employee_list_view, parent, false);
            mViewHolder = new PageEmployeeBaseAdpter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (PageEmployeeBaseAdpter.MyViewHolder) convertView.getTag();
        }

        final User info = getItem(position);
       /* firstName = userInfo.get(position).getFirstName().toString();
        lastName = userInfo.get(position).getLastName();*/
      // mViewHolder.fieldId.setText(info.getEmpId());
        mViewHolder.fieldName.setText(info.getUserName());
        if (info.getProfilePjhoto() != null && !info.getProfilePjhoto().matches("")) {
            Picasso.with(context).load(info.getProfilePjhoto()).fit().centerCrop().into(mViewHolder.userImage);
        }

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
                            intent.putExtra(sendId, userInfo.get(position).getSenderId());
                            intent.putExtra(notificationId, userInfo.get(position).getPushNotificationId());
                            intent.putExtra(firstName, userInfo.get(position).getFirstName());
                            intent.putExtra(lastName, userInfo.get(position).getLastName());
                            intent.putExtra(role,userInfo.get(position).getRole());
                            Log.d(TAG,"role role value..."+userInfo.get(position).getRole());
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Chat pin is not match!", Toast.LENGTH_LONG).show();
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
        return convertView;
    }

    private class MyViewHolder {
        private TextView fieldId, fieldName;
        private ImageView userImage;

        public MyViewHolder(View item) {
            //fieldId = (TextView) item.findViewById(R.id.layout_field_id);
            fieldName = (TextView) item.findViewById(R.id.layout_field_name);
            userImage = (ImageView) item.findViewById(R.id.user_image);
        }
    }

}
