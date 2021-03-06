package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bluemapletech.hippatextapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class NotAcceptedUser extends AppCompatActivity {
    public static final String rootValue = "rootValue";
    public static final String role = "role";
    public static final String NotAcceptUser = "NotAcceptUser";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String loginroleValue,firstName,lastName;
    String roleVal,loginAuth;
    String name = "";
    private String loginMail,underProcess,isOnline,loginEmailId;
    private String[] userEmail;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private static final String TAG = NotAcceptedUser.class.getCanonicalName();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_accepted_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        TextView underProcess = (TextView) findViewById(R.id.underProcess);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginroleValue =  pref.getString("role", "");
        loginEmailId = pref.getString("loginMail","");
        loginMail = loginEmailId;
        firstName = pref.getString("firstName","");
        lastName = pref.getString("lastName","");
        loginAuth = pref.getString("auth","");
        isOnline =  pref.getString("isOnline", "");
       // userEmail = loginEmailId.split("@");
        if(firstName.matches("")){
            if(lastName.matches("")){
                String[] valueuserName = loginEmailId.split("@");
                name = valueuserName[0];
                Log.d(TAG,"firstname mail value.."+name);
                Log.d(TAG,"mail value.."+valueuserName[0]);
            }else {
                name = lastName;
                Log.d(TAG,"first name is last.."+name);
            }
        }else{
           name = firstName;
            Log.d(TAG,"first name is not empty.."+firstName);
            //companyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        }
        Log.d(TAG,"name..."+name);
        if(loginAuth.matches("0")){
            underProcess.setText("Welcome, " +name+".\n Your account is being verified, you will receive an email after processing is complete.");
        }
        if(loginAuth.matches("2")){
            underProcess.setText("Welcome, " +name+".\n Your account is put to pending, you will receive an email after processing is complete.");
        }
        if(loginAuth.matches("3")){
            underProcess.setText("Welcome, " +name+".\n Your account is being rejected, you will receive an email after processing is complete.");
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(loginroleValue.matches("admin") || loginroleValue.matches("root") ){
            getMenuInflater().inflate(R.menu.not_accept_user_menu, menu);
        }else if(loginroleValue.matches("user")){
            getMenuInflater().inflate(R.menu.not_accept_user, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("menu selected", "menu selected");
        //noinspection SimplifiableIfStatement
        if (id == R.id.list_of_roots) {
            Intent intent = new Intent(getActivity(), ListOfRoots.class);
            String rootVal = "1";
            if(loginroleValue.matches("admin") || loginroleValue.matches("root")){
                 roleVal = "root";
            }else if(loginroleValue.matches("user")){
                 roleVal = "admin";
            }
            String notAcceptUser = "notAcceptUser";
            intent.putExtra(NotAcceptUser,notAcceptUser);
            intent.putExtra(rootValue,rootVal);
            intent.putExtra(role,roleVal);
            startActivity(intent);
            return true;
        }
        if (id == R.id.settings) {
            Intent redirect = new Intent(getActivity(), Settings.class);
            startActivity(redirect);
        }

        if (id == R.id.log_out) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Processing...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            SharedPreferences preferences = getSharedPreferences("myBackgroundImage", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            Calendar c = Calendar.getInstance();
            String myFormat = "yyyy-MM-dd HH:mm:ss Z";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String dateValue = sdf.format(c.getTime());
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            String reArrangeEmail = loginMail.replace(".", "-");
            DatabaseReference dataReference = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail).child("updatedDate");
            dataReference.setValue(dateValue);

            DatabaseReference dataReference1= mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail).child("pushNotificationId");
            dataReference1.setValue("");

            DatabaseReference dataReference2= mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            dataReference2.removeValue();

            SharedPreferences preferencess = getSharedPreferences("loginUserDetails", 0);
            SharedPreferences.Editor editors = preferencess.edit();
            editors.clear();
            editors.commit();

            Intent logOut = new Intent(getActivity(), HomeActivity.class);
            progressDialog.dismiss();
            startActivity(logOut);
            onStop();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onPause()
    {
        if(isOnline.matches("true")) {
            fireBaseDatabase = FirebaseDatabase.getInstance();
            String reArrangeEmail = loginMail.replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            dataReferences.removeValue();
        }
        super.onPause();
        //Do whatever you want to do when the application stops.
    }


    @Override
    protected  void onResume(){
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
    }
    public NotAcceptedUser getActivity() {
        return this;
    }
}
