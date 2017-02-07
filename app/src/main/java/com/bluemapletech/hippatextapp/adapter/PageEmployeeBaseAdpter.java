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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.activity.ChatEmployeeActivity;
import com.bluemapletech.hippatextapp.activity.ViewUserDetailTabActivity;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.model.UserDetailDto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static final String userEmails = "userEmails";
    public static final String chatOnline = "chatOnline";
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    HashMap<String,String> onlineHash = new HashMap<String, String>();
    LayoutInflater inflater;
    Context context;
    private String fromMAil;
    private String chatPin;
    private String userFirstName;
    private String userLastName;
    private AlertDialog.Builder alertDialog;
    List<User> userInfo = new ArrayList<User>();
UserDetailDto userDetailDtos = new UserDetailDto();
    public PageEmployeeBaseAdpter(Context context, List<User> user, UserDetailDto userDetailDto, String loggedINChatPin,HashMap<String,String> hashValue) {
        this.context = context;
        this.userInfo = user;
        this.fromMAil = userDetailDto.getLoggedINEmail();
        this.chatPin = loggedINChatPin;
        this.onlineHash = hashValue;
        this.userDetailDtos = userDetailDto;
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
        if(info.getRole().matches("user")) {
            mViewHolder.fieldId.setText(info.getEmpId());
        }else if(info.getRole().matches("admin")){
            mViewHolder.fieldId.setText(info.getProviderNPIId());
        }
        View imgs = convertView.findViewById(R.id.onlineImageView);
        imgs.setVisibility(View.INVISIBLE);

        mViewHolder.fieldName.setText(info.getFirstName());
        if (info.getProfilePjhoto() != null && !info.getProfilePjhoto().matches("")) {
            Picasso.with(context).load(info.getProfilePjhoto()).fit().centerCrop().into(mViewHolder.userImage);
        }
convertView.findViewById(R.id.layout_field_id).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, ViewUserDetailTabActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(userEmails, userInfo.get(position).getUserName());
        context.startActivity(intent);
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
                        byte[] data1 = Base64.decode(chatPin, Base64.NO_WRAP);
                        String text = null;
                        try {
                            text = new String(data1, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (srt.matches(text)) {
                            Intent intent = new Intent(context, ChatEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            String checkOnline = onlineHash.get(info.getUserName());
                           intent.putExtra(toEmail, userInfo.get(position).getUserName());
                            Log.d(TAG,"fromMAil"+fromMAil);
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
                View img = convertView.findViewById(R.id.onlineImageView);
                img.setVisibility(View.VISIBLE);

            } else {
                View img = convertView.findViewById(R.id.onlineImageView);
                img.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    private class MyViewHolder {
        private TextView fieldId, fieldName;
        private ImageView userImage;

        public MyViewHolder(View item) {
            fieldId = (TextView) item.findViewById(R.id.layout_field_id);
            fieldName = (TextView) item.findViewById(R.id.layout_field_name);
            userImage = (ImageView) item.findViewById(R.id.user_image);
        }
    }

}
