package com.bluemapletech.hippatextapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bluemapletech.hippatextapp.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Win7v5 on 11/9/2016.
 */

public class MyFirebaseInstansceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstansceIDService.class.getCanonicalName();
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences sharedpreferences;
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"refreshedToken " + refreshedToken);
        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("notificationId", token);
        editor.commit();

       /*String userFirstLogin = pref.getString("notificationId",null);
        Log.d(TAG,"userFirstLogin"+userFirstLogin);*/
    }
    public MyFirebaseInstansceIDService getActivity() {
        return this;
    }
}
