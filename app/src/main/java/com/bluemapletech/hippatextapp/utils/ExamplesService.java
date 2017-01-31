package com.bluemapletech.hippatextapp.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by HPFolioUser on 16-01-2017.
 */

public class ExamplesService extends Service {

    private String isOnline,loginMail;
    private FirebaseDatabase fireBaseDatabase;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("xxxxx","xxxxxxxx1");
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref.getString("isOnline", "");
        loginMail =  pref.getString("loginMail", "");
        onPause();
        onResume();
    }

    private void onResume() {
        Log.d("onResume","onResume method is called");
        if(isOnline.matches("true")) {
            HashMap<String, Object> onlineReenter = new HashMap<>();
            fireBaseDatabase = FirebaseDatabase.getInstance();
            String reArrangeEmail = loginMail.replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            onlineReenter.put("onlineUser", loginMail);
            dataReferences.setValue(onlineReenter);
        }
    }

    private void onPause() {
        Log.d("onPause","onPause method is called");
        if(isOnline.matches("true")) {
            fireBaseDatabase = FirebaseDatabase.getInstance();
            String reArrangeEmail = loginMail.replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            dataReferences.removeValue();
        }
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("xxxxxxxxxx","xxxxxxxxxxxxxx222");
    }*/
  /*@Override
  public void onPause()
  {
      if(isOnline.matches("true")) {
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
    public  void onResume(){
        if(isOnline.matches("true")) {
            HashMap<String, Object> onlineReenter = new HashMap<>();
            fireBaseDatabase = FirebaseDatabase.getInstance();
            String reArrangeEmail = loginMail.replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            onlineReenter.put("onlineUser", loginMail);
            dataReferences.setValue(onlineReenter);
        }
        super.onResume();
    }*/
}
