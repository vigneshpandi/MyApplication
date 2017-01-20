package com.bluemapletech.hippatextapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.activity.GroupMessageEmployeeActivity;
import com.bluemapletech.hippatextapp.model.Groups;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Win7v5 on 11/7/2016.
 */

public class EmployeeGroupsAdapter extends BaseAdapter {
    private static final String TAG = EmployeeGroupsAdapter.class.getCanonicalName();
    public static final String randomValue = "randomValue";
    public static final String fromMail ="fromMail";
    public static final String senderId ="senderId";
    public static final String notificationId = "notificationId";
    public static final String groupName = "groupName";
    LayoutInflater inflater;
    Context context;
    List<Groups> groupInfo = new ArrayList<Groups>();
    private String loginSenderId;
    private String loginChatPin;
    private String loginMail;
    public EmployeeGroupsAdapter(Context context, List<Groups> groupObj, String loggedINsenderId, String loggedINChatPin, String loggedINEmail) {
        this.context = context;
        this.groupInfo = groupObj;
        this.loginSenderId = loggedINsenderId;
        this.loginChatPin = loggedINChatPin;
        this.loginMail = loggedINEmail;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return groupInfo.size();
    }

    @Override
    public Groups getItem(int position) {
        return groupInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        EmployeeGroupsAdapter.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.employee_group_list, parent, false);
            mViewHolder = new EmployeeGroupsAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (EmployeeGroupsAdapter.MyViewHolder) convertView.getTag();
        }

        final Groups info = getItem(position);
        convertView.findViewById(R.id.group_chat).setOnClickListener(new View.OnClickListener() {
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
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String srt = chatPinn.getEditableText().toString();
                        byte[] data1 = Base64.decode(loginChatPin, Base64.NO_WRAP);
                        String text = null;
                        try {
                            text = new String(data1, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if(srt.matches(text)) {
                            Intent intent = new Intent(context, GroupMessageEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(randomValue, groupInfo.get(position).getRandomName());
                            intent.putExtra(fromMail,loginMail);
                            intent.putExtra(senderId,loginSenderId);
                            intent.putExtra(notificationId,groupInfo.get(position).getGroupEmailId());
                            intent.putExtra(groupName,groupInfo.get(position).getGroupName());
                            context.startActivity(intent);
                        }else{
                            Toast.makeText(context, " Sorry! Chat pin does not match!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });
            Picasso.with(context).load(info.getGroupImage()).fit().centerCrop().into(mViewHolder.userImage);

        mViewHolder.fieldName.setText(info.getGroupName());


        return convertView;
    }

    private class MyViewHolder {

        private TextView  fieldName;
        private ImageView userImage;
        public MyViewHolder(View item) {

            fieldName = (TextView) item.findViewById(R.id.group_name);
            userImage = (ImageView) item.findViewById(R.id.user_image);
        }
    }


}
