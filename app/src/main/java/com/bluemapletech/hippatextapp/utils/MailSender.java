package com.bluemapletech.hippatextapp.utils;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

/**
 * Created by HPFolioUser on 22-11-2016.
 */

public class MailSender extends AsyncTask<String, String, String> {
    private GMailSender sender;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        sender = new GMailSender("hipaatext123@gmail.com", "transc4r3");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            // Add subject, Body, your mail Id, and receiver mail Id.

            sender.sendMail(params[0], params[1], params[2],params[3]);
        }

        catch (Exception ex) {

        }
        return null;
    }


}
