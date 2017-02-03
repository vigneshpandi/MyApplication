package com.bluemapletech.hippatextapp.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by HPFolioUser on 16-01-2017.
 */

public class ExamplesService extends Service {
    private static final String TAG = ExamplesService.class.getCanonicalName();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "calling for onBind");
        NewCheck serviceCalling = new NewCheck();
        serviceCalling.getValue();
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "on createmethod");
        NewCheck serviceCalling = new NewCheck();
        serviceCalling.getValue();
        super.onCreate();
    }

    private class NewCheck extends AppCompatActivity {
        private FirebaseDatabase fireBaseDatabase;
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        private String loginMail, isOnline;
        private FirebaseAuth firebaseAuth;

        @Override
        public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
            super.onCreate(savedInstanceState, persistentState);
            Log.d(TAG, "calling on create NewCheck");
            pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
            isOnline = pref.getString("isOnline", "");
            loginMail = pref.getString("loginMail", "");

        }

        public void getValue() {
            Log.d(TAG, "calling NewCheck");
            /*firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser logged = firebaseAuth.getCurrentUser();
            String reArrangeEmail = logged.getEmail().replace(".", "-");
            Log.d(TAG,"reArrangeEmail"+reArrangeEmail);*/
           /* pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
            isOnline = pref.getString("isOnline", "");
            loginMail = pref.getString("loginMail", "");*/
        }


        @Override
        public void onPause() {
            if (isOnline.matches("true")) {
                Log.d(TAG, "pause pause");
                fireBaseDatabase = FirebaseDatabase.getInstance();
                String reArrangeEmail = loginMail.replace(".", "-");
                FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
                dataReferences.removeValue();
            }
            //Do whatever you want to do when the application stops.
            super.onPause();
        }

        @Override
        public void onResume() {
            Log.d(TAG, "onResume");
            if (isOnline.matches("true")) {
                HashMap<String, Object> onlineReenter = new HashMap<>();
                fireBaseDatabase = FirebaseDatabase.getInstance();
                String reArrangeEmail = loginMail.replace(".", "-");
                FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
                onlineReenter.put("onlineUser", loginMail);
                dataReferences.setValue(onlineReenter);
            }
            super.onResume();
        }
    }
}
