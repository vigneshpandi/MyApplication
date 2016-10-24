package com.bluemapletech.hippatextapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.activity.ViewUserDetails;
import com.bluemapletech.hippatextapp.dao.CompanyDao;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumaresan on 20-10-2016.
 */

public class PageAdminBaseAdapter extends BaseAdapter {
    private static final String TAG = PageBaseAdapter.class.getCanonicalName();
    public static final String userEmail = "userEmail";
    LayoutInflater inflater;
    Context context;
    List<User> userInfo = new ArrayList<User>();

    public PageAdminBaseAdapter(Context context, List<User> user) {
        this.context = context;
        this.userInfo = user;
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

        PageAdminBaseAdapter.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_admin_list_view, parent, false);
            mViewHolder = new PageAdminBaseAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (PageAdminBaseAdapter.MyViewHolder) convertView.getTag();
        }

        final User info = getItem(position);

        mViewHolder.fieldId.setText(info.getTINorEIN());
        mViewHolder.fieldName.setText(info.getUserName());

        ((Button) convertView.findViewById(R.id.accept_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo.get(position).getAuth().matches("0")) {
                    addInvitedUser(userInfo, position);
                } else {
                    deleteUser(userInfo, position);
                }
            }
        });

        ((TextView) convertView.findViewById(R.id.layout_field_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewUserDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(userEmail, userInfo.get(position).getUserName());
                context.startActivity(intent);
            }
        });

        if (userInfo.get(position).getAuth().matches("1")) {
           /* View btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setVisibility(btn.INVISIBLE);
           View btns = (Button) convertView.findViewById(R.id.cancel_btn);
            btns.setVisibility(btns.INVISIBLE);*/
            /*Button btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setText("View");*/
            Button btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setText("Delete");
            btn.setBackgroundColor(Color.parseColor("#ff3322"));
        }
        return convertView;
    }

    private class MyViewHolder {

        private TextView fieldId, fieldName;

        public MyViewHolder(View item) {
            fieldId = (TextView) item.findViewById(R.id.layout_field_id);
            fieldName = (TextView) item.findViewById(R.id.layout_field_name);
        }
    }

    public void addInvitedUser(List<User> user, int position) {
        Log.d(TAG, "Add invited User method has been called!");
        final UserDao userDao = new UserDao();
        boolean result = userDao.sendInvite(user.get(position));
        if (result) {
            Log.d(TAG, "User accepted successfully!");
            Toast.makeText(this.context, "Request has been sent!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while adding the company, please try again!");
        }
    }

    /*public void cancelCompany(List<User> user) {
        Log.d(TAG, "Add invited company method has been called!");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.cancelCompany(user.get(0));
        if (result) {
            Log.d(TAG, "Company canceled successfully!");
            Toast.makeText(this.context, "Request has been canceled by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while cancel the company, please try again!");
        }
    }*/

    public void deleteUser(List<User> user, int position) {
        Log.d(TAG, "Delete user method has been called!");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.cancelCompany(user.get(position));
        if (result) {
            Log.d(TAG, "User has been deleted successfully!");
            Toast.makeText(this.context, "User has been deleted by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }
}
