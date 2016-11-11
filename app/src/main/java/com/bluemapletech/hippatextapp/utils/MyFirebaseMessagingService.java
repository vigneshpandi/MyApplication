package com.bluemapletech.hippatextapp.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.activity.EmployeeHomeActivity;
import com.bluemapletech.hippatextapp.activity.HomeActivity;
import com.bluemapletech.hippatextapp.activity.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Win7v5 on 11/9/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getCanonicalName();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if(remoteMessage.getData().size() > 0){
            Log.d(TAG,"Message Data"+remoteMessage.getData());
        }
        if(remoteMessage.getNotification() != null){
            //sendNotification(remoteMessage.getNotification().getBody());
        }
    }
    private void sendNotification(String body) {

          Intent re = new Intent(this,EmployeeHomeActivity.class);
        re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          PendingIntent pendingIn = PendingIntent.getActivity(this,0 ,re,PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifyBuilder =new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).
                setContentTitle("tcttextapp").setContentText(body)
                .setAutoCancel(true).setSound(notificationSound);

        NotificationManager notiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notiManager.notify(0 , notifyBuilder.build());
    }

}
