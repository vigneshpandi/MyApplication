package com.bluemapletech.hippatextapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
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
import com.bluemapletech.hippatextapp.model.UserDetailDto;
import com.bluemapletech.hippatextapp.utils.MailSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumaresan on 20-10-2016.
 */

public class PageAdminBaseAdapter extends BaseAdapter {
    private static final String TAG = PageAdminBaseAdapter.class.getCanonicalName();
    public static final String userEmails = "userEmails";
    public static final String userAuth = "userAuth";
    LayoutInflater inflater;
    Context context;
    List<User> userInfo = new ArrayList<User>();
    private AlertDialog.Builder alertDialog;
    private UserDetailDto userDetailDtos = new UserDetailDto();

    public PageAdminBaseAdapter(Context context, List<User> user,UserDetailDto userDetailDto) {
        this.context = context;
        this.userInfo = user;
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

        PageAdminBaseAdapter.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_admin_list_view, parent, false);
            mViewHolder = new PageAdminBaseAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (PageAdminBaseAdapter.MyViewHolder) convertView.getTag();
        }

        final User info = getItem(position);

           mViewHolder.fieldId.setText(info.getEmpId());
           mViewHolder.fieldName.setText(info.getUserName());

        convertView.findViewById(R.id.accept_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo.get(position).getAuth().matches("0")) {
                    Intent intent = new Intent(context, ViewUserDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(userEmails, userInfo.get(position).getUserName());
                    intent.putExtra(userAuth, userInfo.get(position).getAuth());
                    context.startActivity(intent);
                } else if(userInfo.get(position).getAuth().matches("2")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("");
                    alert.setMessage("Do you want to accept '"+info.getEmpId()+"' Employee!");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            accepted(userInfo.get(position));
                        }
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                } else if(userInfo.get(position).getAuth().matches("1")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("");
                    alert.setMessage("Do you want to reject '"+info.getEmpId()+"' Employee!");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteEmpl(userInfo.get(position));
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
        convertView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo.get(0).getAuth().matches("2")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("");
                    alert.setMessage("Do you want to reject '"+info.getEmpId()+"'Employee!");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteEmpl(userInfo.get(position));
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
        convertView.findViewById(R.id.layout_field_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userInfo.get(position).getAuth().matches("0")) {
                    Intent intent = new Intent(context, ViewUserDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(userEmails, userInfo.get(position).getUserName());
                    intent.putExtra(userAuth, userInfo.get(position).getAuth());
                    context.startActivity(intent);
                }
            }
        });
        if (userInfo.get(0).getAuth().matches("0")) {
            Button btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setText("View Request");
            btn.setBackgroundColor(convertView.getResources().getColor(R.color.navigationBarColor));
            View btns = convertView.findViewById(R.id.cancel_btn);
            btns.setVisibility(View.INVISIBLE);
            TextView id = (TextView) convertView.findViewById(R.id.layout_field_id);
            id.setTextColor(Color.parseColor("#808080"));
        }

        if (userInfo.get(0).getAuth().matches("1")) {
            Button btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setText("Reject");
            TextView textColor = (TextView) convertView.findViewById(R.id.layout_field_id);
            textColor.setTextColor(convertView.getResources().getColor(R.color.textColor));

            btn.setBackgroundColor(Color.parseColor("#ff3322"));
            View btns = convertView.findViewById(R.id.cancel_btn);
            btns.setVisibility(View.INVISIBLE);
        }

        if (userInfo.get(0).getAuth().matches("2")) {
            Button btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setText("accept");
            Button btns = (Button) convertView.findViewById(R.id.cancel_btn);
            btns.setText("Reject");
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
        final UserDao userDao = new UserDao();
        boolean result = userDao.acceptedEmployee(user);
        if (result) {
            try {
                MailSender runners = new MailSender();
                String  value = "Company is accepted successfully!";
                runners.execute("Company is accepted successfully!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Toast.makeText(this.context, "Company is accepted successfully!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }

    }

    public void pending(User user) {
        user.setAuth("2");
        Log.d(TAG, "Add invited company method has been called!");
        final UserDao userDao = new UserDao();
        boolean result = userDao.pendingEmployee(user);
        if (result) {
            Toast.makeText(this.context, "Company is requested to pending!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }

    public void deleteEmpl(User user) {
        user.setAuth("3");
        Log.d(TAG, "Add invited company method has been called!");
        final UserDao userDao = new UserDao();
        boolean result = userDao.deleteEmployee(user);
        if (result) {
            try {
                MailSender runners = new MailSender();
                String  value = "Company is rejected successfully!";
                runners.execute("Company is rejected successfully!",value,"hipaatext123@gmail.com",user.getUserName());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Toast.makeText(this.context, "Company is rejected successfully!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }

}
