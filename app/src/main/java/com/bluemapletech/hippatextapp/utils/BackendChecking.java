package com.bluemapletech.hippatextapp.utils;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by BlueMaple on 1/9/2017.
 */

public class BackendChecking extends AppCompatActivity {
    private FirebaseDatabase fireBaseDatabase;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = BackendChecking.class.getCanonicalName();


    @Override
    public void onPause()
    {
        Log.d(TAG,"stoppp");
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        String reArrangeEmail =  logged.getEmail().replace(".", "-");
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
        dataReferences.removeValue();
        super.onPause();
        //Do whatever you want to do when the application stops.
    }


    @Override
    protected  void onResume(){
        HashMap<String, Object> onlineReenter = new HashMap<>();
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        String reArrangeEmail =  logged.getEmail().replace(".", "-");
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
        onlineReenter.put("onlineUser",logged.getEmail());
        dataReferences.setValue(onlineReenter);
        super.onResume();
    }
}
