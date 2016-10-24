package com.bluemapletech.hippatextapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageAdminBaseAdapter;
import com.bluemapletech.hippatextapp.adapter.PageBaseAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ViewUserDetails extends AppCompatActivity {
    private static final String TAG = ViewUserDetails.class.getCanonicalName();
    String empMailId;
    String userId;
    String adminMailId;
    String reArrangeEmail;
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private TextView userEmail, compName, empId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);
        init();
    }

    public void init() {
        empMailId = getIntent().getStringExtra(PageAdminBaseAdapter.userEmail);
        adminMailId = getIntent().getStringExtra(PageBaseAdapter.userEmail);
        userEmail = (TextView) findViewById(R.id.user_email);
        compName = (TextView) findViewById(R.id.comp_name);
        empId = (TextView) findViewById(R.id.employee_id);
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        if (adminMailId != null) {
            reArrangeEmail = adminMailId.replace(".", "-");
        } else {
            reArrangeEmail = empMailId.replace(".", "-");
        }

        databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                Map<String, String> map = (Map) dataSnapshot.getValue();
                String comNames = map.get("companyName");
                String emailAddress = map.get("emailAddress");
                //String empyID = map.get("employeeId");
                if (adminMailId != null) {
                    userId = map.get("compId");
                } else {
                    userId = map.get("employeeId");
                }
                empId.setText(userId);
                compName.setText(comNames);
                userEmail.setText(emailAddress);

                //  String  companyId   = map.get("compId");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
