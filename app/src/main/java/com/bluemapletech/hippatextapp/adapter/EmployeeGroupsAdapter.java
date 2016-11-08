package com.bluemapletech.hippatextapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.activity.ChatEmployeeActivity;
import com.bluemapletech.hippatextapp.activity.GroupMessageEmployeeActivity;
import com.bluemapletech.hippatextapp.model.Groups;


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
        return (Groups) groupInfo.get(position);
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
        ((Button) convertView.findViewById(R.id.group_chat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupMessageEmployeeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(randomValue, groupInfo.get(position).getRandomName());
                intent.putExtra(fromMail,loginMail);
                intent.putExtra(senderId,loginSenderId);

                context.startActivity(intent);
            }
        });

        mViewHolder.fieldName.setText(info.getGroupName());


        return convertView;
    }

    private class MyViewHolder {

        private TextView  fieldName;

        public MyViewHolder(View item) {

            fieldName = (TextView) item.findViewById(R.id.group_name);
        }
    }


}
