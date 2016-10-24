package com.bluemapletech.hippatextapp.widgets;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageBaseAdapter;
import com.bluemapletech.hippatextapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumaresan on 20-10-2016.
 */

public class AcceptedTabActivity extends Fragment {

    private ListView listview;
    ArrayList compList = new ArrayList();

    private static final String TAG = AcceptedTabActivity.class.getCanonicalName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pending_tab_frament, container, false);

        listview = (ListView) rootView.findViewById(R.id.pending_tab_fragment);

        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = mfireBaseDatabase.getReference().child("userDetails");
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                List<User> userObj = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Snapshot value: " + snapshot.toString());
                    user = new User();
                    user.setCompanyName(snapshot.child("companyName").getValue(String.class));
                    user.setTINorEIN(snapshot.child("compId").getValue(String.class));
                    user.setPassword(snapshot.child("password").getValue(String.class));
                    user.setRole(snapshot.child("role").getValue(String.class));
                    user.setAuth(snapshot.child("auth").getValue(String.class));
                    user.setUserName(snapshot.child("emailAddress").getValue(String.class));
                    if (user.getRole().matches("admin") && user.getAuth().matches("1")) {
                        Log.d(TAG, "Snapshot values from admin: " + snapshot.toString());
                        userObj.add(user);
                    }
                }

                listview.setAdapter(new PageBaseAdapter(getActivity(),userObj));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
        return rootView;
    }
}
