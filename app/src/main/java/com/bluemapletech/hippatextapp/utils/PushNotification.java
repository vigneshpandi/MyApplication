package com.bluemapletech.hippatextapp.utils;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HPFolioUser on 27-01-2017.
 */

public class PushNotification extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {
        Object json = null;
        try {
            URL url1;
            url1 = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=AIzaSyCqV9nJeTq4gLy6g9N4eTDhTuSBE3_2uWk");
            JSONObject root = new JSONObject();
            root.put("title",params[0]);
            root.put("body",params[1]);
            JSONObject root1 = new JSONObject();
            root1.put("notification",root);
            root1.put("to",params[2]);
            root1.put("priority","high");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(root1.toString());
            wr.flush();
            wr.close();
            int responsecode = conn.getResponseCode();
            if(responsecode == 200) {
                //Log.d(TAG,"success"+conn.getResponseMessage());
            }else{
               // Log.d(TAG,"error"+conn.getResponseMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

    }
}
