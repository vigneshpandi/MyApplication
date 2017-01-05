package com.bluemapletech.hippatextapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bluemapletech.hippatextapp.R;
public class NotAcceptedUser extends AppCompatActivity {
    public static final String rootValue = "rootValue";
    public static final String role = "role";
    public static final String NotAcceptUser = "NotAcceptUser";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String loginroleValue;
    String roleVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_accepted_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginroleValue =  pref.getString("role", "");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(loginroleValue.matches("admin")){
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
            if(loginroleValue.matches("admin")){
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
            Intent logOut = new Intent(getActivity(), HomeActivity.class);
            startActivity(logOut);
            onStop();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public NotAcceptedUser getActivity() {
        return this;
    }
}
