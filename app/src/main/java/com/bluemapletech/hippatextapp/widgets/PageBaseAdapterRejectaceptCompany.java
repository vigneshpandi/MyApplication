package com.bluemapletech.hippatextapp.widgets;

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
import com.bluemapletech.hippatextapp.adapter.PageBaseAdapter;
import com.bluemapletech.hippatextapp.dao.CompanyDao;
import com.bluemapletech.hippatextapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HPFolioUser on 06-01-2017.
 */

public class PageBaseAdapterRejectaceptCompany extends BaseAdapter {
    private static final String TAG = PageBaseAdapter.class.getCanonicalName();
    public static final String userEmail = "userEmail";
    public static final String userAuth = "userAuth";
    LayoutInflater inflater;
    Context context;
    List<User> userInfo = new ArrayList<User>();
    List<User> userInfo1 = new ArrayList<User>();
    HashMap<String,Integer> hashValue = new HashMap<String, Integer>();
    public PageBaseAdapterRejectaceptCompany(Context context, List<User> user, List<User> user1, HashMap<String,Integer> hashValue) {
        this.context = context;
        this.userInfo = user;
        this.userInfo1=user1;
        inflater = LayoutInflater.from(this.context);
        this.hashValue=hashValue;
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

        PageBaseAdapterRejectaceptCompany.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_list_view, parent, false);
            mViewHolder = new PageBaseAdapterRejectaceptCompany.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (PageBaseAdapterRejectaceptCompany.MyViewHolder) convertView.getTag();
        }

        final User info = getItem(position);
        mViewHolder.fieldId.setText(info.getTINorEIN());
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
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Do you want to cancel "+"'"+info.getCompanyName()+"'"+"company!");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteCompany(userInfo.get(position));
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
                // int position=(Integer)v.getTag();
                if (!userInfo.get(position).getAuth().matches("0")) {
                    Intent intent = new Intent(context, ViewUserDetails.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(userEmail, userInfo.get(position).getUserName());
                    intent.putExtra(userAuth, userInfo.get(position).getAuth());
                    context.startActivity(intent);
                }

            }
        });

        if (userInfo.get(0).getAuth().matches("0")) {
            /*Button btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setText("View Request");
            TextView id = (TextView) convertView.findViewById(R.id.layout_field_id);
            id.setTextColor(Color.parseColor("#666633"));
            TextView companyName = (TextView) convertView.findViewById(R.id.layout_field_name);
            companyName.setTextColor(Color.parseColor("#000000"));
            btn.setBackgroundColor(convertView.getResources().getColor(R.color.navigationBarColor));
            View btns = convertView.findViewById(R.id.cancel_btn);
            btns.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.INVISIBLE);
            *//*Button hideBtns = (Button) convertView.findViewById(R.id.accept_btn1);
            hideBtns.setVisibility(View.VISIBLE);*/
            Button btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setText("View Request");
            btn.setBackgroundColor(convertView.getResources().getColor(R.color.navigationBarColor));
            Button btns = (Button) convertView.findViewById(R.id.cancel_btn);
            btns.setVisibility(View.INVISIBLE);
            TextView id = (TextView) convertView.findViewById(R.id.layout_field_id);
            id.setTextColor(Color.parseColor("#808080"));
        }

        if (userInfo.get(0).getAuth().matches("1")) {
           /* Button btn = (Button) convertView.findViewById(R.id.cancel_btn);
            btn.setText("Reject");
            btn.setBackgroundColor(Color.parseColor("#ff3322"));
            TextView id = (TextView) convertView.findViewById(R.id.layout_field_id);
            id.setTextColor(Color.parseColor("#0080ff"));
            TextView companyName = (TextView) convertView.findViewById(R.id.layout_field_name);
            companyName.setTextColor(Color.parseColor("#000000"));
            View btns = convertView.findViewById(R.id.accept_btn);
            btns.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.INVISIBLE);
            *//*Button hideBtns = (Button) convertView.findViewById(R.id.accept_btn1);
            hideBtns.setVisibility(View.VISIBLE);*/
            Button btn = (Button) convertView.findViewById(R.id.cancel_btn);
            btn.setText("Reject");
            btn.setBackgroundColor(convertView.getResources().getColor(R.color.cancel_btn));
            Button btns = (Button) convertView.findViewById(R.id.accept_btn);
            btns.setVisibility(View.INVISIBLE);
            TextView textColor = (TextView) convertView.findViewById(R.id.layout_field_id);
            textColor.setTextColor(convertView.getResources().getColor(R.color.textColor));


        }

       /* if (userInfo.get(0).getAuth().matches("2")) {
            TextView id = (TextView) convertView.findViewById(R.id.layout_field_id);
            id.setTextColor(Color.parseColor("#0080ff"));
            TextView companyName = (TextView) convertView.findViewById(R.id.layout_field_name);
            companyName.setTextColor(Color.parseColor("#000000"));
            Button btn = (Button) convertView.findViewById(R.id.accept_btn);
            btn.setText("Accept");
            Button btns = (Button) convertView.findViewById(R.id.cancel_btn);
            btns.setText("Reject");
            Button hideBtns = (Button) convertView.findViewById(R.id.accept_btn1);
            hideBtns.setVisibility(View.INVISIBLE);
        }*/

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
            Log.d(TAG, "Company canceled successfully!");
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
            Log.d(TAG, "Company canceled successfully!");
            Toast.makeText(this.context, "Company has been pending by the admin!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Error while delete the company, please try again!");
        }
    }
    public void deleteCompany(User user) {
        List<User> emailId = new ArrayList<User>();
        Log.d(TAG, "Company canceled successfully!");
        int com_count = hashValue.get( user.getCompanyName());
        Log.d(TAG,"dasasda"+com_count);

        if(com_count == 1){
            Log.d(TAG,"dasasdasfsdfsdf");
            String companyName = user.getCompanyName();
            User user1;
            emailId.add(user);
            for(int i=0;i<userInfo1.size();i++){

                if(userInfo1.get(i).getCompanyName().matches(companyName)){
                    user1 = new User();
                    user1.setUserName(userInfo1.get(i).getUserName());
                    user1.setRole(userInfo1.get(i).getRole());
                    emailId.add(user1);
                }

            }

            Log.d(TAG, "Coasdasdadadasd");
           final CompanyDao companyDao = new CompanyDao();
            boolean result = companyDao.deleteCompanyAdminAndUser(emailId);
            if (result) {
                Log.d(TAG, "Company canceled successfully!");
                Toast.makeText(this.context, "Company deleted successfully!", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "Error while delete the company, please try again!");
            }
        }else{
            user.setAuth("3");
            Log.d(TAG, "Add invited company method has been called!");
            final CompanyDao companyDao = new CompanyDao();
            boolean result = companyDao.deleteCompanys(user);
            if (result) {
                Log.d(TAG, "Company canceled successfully!");
               Toast.makeText(this.context, "Company deleted successfully!", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "Error while delete the company, please try again!");
            }
        }

    }
}
