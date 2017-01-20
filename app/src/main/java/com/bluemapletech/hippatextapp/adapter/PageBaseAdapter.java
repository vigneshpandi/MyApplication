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
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.MailSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumaresan on 20-10-2016.
 */

public class PageBaseAdapter extends BaseAdapter {

    private static final String TAG = PageBaseAdapter.class.getCanonicalName();
    public static final String userEmail = "userEmail";
    public static final String userAuth = "userAuth";
    LayoutInflater inflater;
    Context context;
    List<User> userInfo = new ArrayList<User>();

    public PageBaseAdapter(Context context, List<User> user) {
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
        return userInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_list_view, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final User info = getItem(position);
        mViewHolder.fieldId.setText(info.getUserName());
        mViewHolder.fieldName.setText(info.getCompanyName());

        convertView.findViewById(R.id.accept_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (userInfo.get(position).getAuth().matches("0")) {
                    Intent intent = new Intent(context, ViewUserDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(userEmail, userInfo.get(position).getUserName());
                    intent.putExtra(userAuth, userInfo.get(position).getAuth());
                    context.startActivity(intent);
                }else if(userInfo.get(position).getAuth().matches("2")){
                    accepted(userInfo.get(position));
                }

            }
        });
        convertView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo.get(position).getAuth().matches("2")) {
                    deleteCompany(userInfo.get(position));
                }else if(userInfo.get(position).getAuth().matches("1")){
                    deleteCompany(userInfo.get(position));
                }
            }
        });

        convertView.findViewById(R.id.layout_field_id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!userInfo.get(position).getAuth().matches("0")) {
                        Intent intent = new Intent(context, ViewUserDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(userEmail, userInfo.get(position).getUserName());
                        intent.putExtra(userAuth, userInfo.get(position).getAuth());
                        context.startActivity(intent);
                    }

            }
        });

        if (userInfo.get(0).getAuth().matches("0")) {
            Button btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setText("View Request");
            btn.setBackgroundColor(convertView.getResources().getColor(R.color.navigationBarColor));
            Button btns = (Button) convertView.findViewById(R.id.cancel_btn);
            btns.setVisibility(View.INVISIBLE);
            TextView id = (TextView) convertView.findViewById(R.id.layout_field_id);
            id.setTextColor(Color.parseColor("#808080"));
        }

        if (userInfo.get(0).getAuth().matches("1")) {
            Button btn = (Button) convertView.findViewById(R.id.cancel_btn);
            btn.setText("Reject");
            btn.setBackgroundColor(convertView.getResources().getColor(R.color.cancel_btn));
            Button btns = (Button) convertView.findViewById(R.id.accept_btn);
            btns.setVisibility(View.INVISIBLE);
            TextView textColor = (TextView) convertView.findViewById(R.id.layout_field_id);
            textColor.setTextColor(convertView.getResources().getColor(R.color.textColor));
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
    public void accepted(User user) {
        user.setAuth("1");
        Log.d(TAG, "Add invited company method has been called!");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.acceptedCompany(user);
        if (result) {
            try {
                MailSender runners = new MailSender();
                String  value = "Company canceled successfully!";
                runners.execute("Company has been deleted by the admin!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Toast.makeText(this.context, "Company has been deleted by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }

    }
    public void pending(User user) {
        user.setAuth("2");
        Log.d(TAG, "Add invited company method has been called!");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.pendingCompany(user);
        if (result) {
            Toast.makeText(this.context, "Company has been pending by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }
    public void deleteCompany(User user) {
        user.setAuth("3");
        Log.d(TAG, "Add invited company method has been called!");
        final CompanyDao companyDao = new CompanyDao();
        boolean result = companyDao.deleteCompanys(user);
        if (result) {
            try {
                MailSender runners = new MailSender();
                String  value = "Company canceled successfully!";
                runners.execute("Company has been deleted by the admin!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Toast.makeText(this.context, "Company has been deleted by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }

}
