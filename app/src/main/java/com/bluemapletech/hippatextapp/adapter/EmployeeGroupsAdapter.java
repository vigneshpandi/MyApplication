package com.bluemapletech.hippatextapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.model.Groups;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Win7v5 on 11/7/2016.
 */

public class EmployeeGroupsAdapter extends BaseAdapter {
    private static final String TAG = EmployeeGroupsAdapter.class.getCanonicalName();

    LayoutInflater inflater;
    Context context;
    List<Groups> groupInfo = new ArrayList<Groups>();

    public EmployeeGroupsAdapter(Context context, List<Groups> groupObj) {
        this.context = context;
        this.groupInfo = groupObj;
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
