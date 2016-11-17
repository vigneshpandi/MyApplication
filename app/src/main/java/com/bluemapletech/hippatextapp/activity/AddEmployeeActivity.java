package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.GMailSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEmployeeActivity extends AppCompatActivity {

    private static final String TAG = AddAdminActivity.class.getCanonicalName();
    private FirebaseAuth firebaseAuthRef;
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private SecureRandom random;
    private ProgressDialog progressDialog;


    private EditText addEmpEmailId, addEmpEmployeeId;
    private Button addEmpBtn;
    private String password;
    private String senderID;
    private String passRandomValue;
    GMailSender sender;
    private String toEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        random = new SecureRandom();
        password = new BigInteger(130, random).toString(32);
        String randomValue = password.substring(0, 8);
        Log.d("randomValue",randomValue);
        passRandomValue = randomValue.toString();
        init();
    }
    private void init() {
        Log.d(TAG, "Init method has been called!");

        addEmpEmailId = (EditText) findViewById(R.id.emp_email_address);
        toEmail = addEmpEmailId.getText().toString();
        addEmpEmployeeId = (EditText) findViewById(R.id.emp_id);
        addEmpBtn = (Button) findViewById(R.id.add_employee_btn);

        addEmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getActivity(), "Add Root failed!", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Added Root...");
                    progressDialog.show();
                    checkUserExistence();
                    sender = new GMailSender("transcaretextapp@gmail.com", "transc4r3");
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                            Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        new MyAsyncClass().execute();

                    } catch (Exception ex) {
                        // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
                    }
                }

            }
            private boolean validate() {
                String addEmailId = addEmpEmailId.getText().toString().trim();
                String addEmpId = addEmpEmployeeId.getText().toString().trim();
                boolean valid = true;
                if(!isValidEmail(addEmailId)){
                    addEmpEmailId.setError("Invalid Email");
                    valid = false;
                }

                if(addEmpId.isEmpty()||addEmpId.length()< 2){
                    addEmpEmployeeId.setError("EMployee Id is invalid");
                    valid = false;
                }else{
                    addEmpEmployeeId.setError(null);
                }
                return valid;
            }
        });

    }
    private boolean isValidEmail(String addEmailId) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(addEmailId);
        return  matcher.matches();
    }

    public void checkUserExistence() {
        firebaseAuthRef = FirebaseAuth.getInstance();

        Log.d("TAG","randomValue..randomValue......"+passRandomValue);
        firebaseAuthRef.createUserWithEmailAndPassword(addEmpEmailId.getText().toString(),
                passRandomValue).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    entryAuth();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Entered email address is already exists! ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... mApi) {
            Log.d(TAG,"mail method is called");
            try {
                // Add subject, Body, your mail Id, and receiver mail Id.

                sender.sendMail("My App", " HI welcome my application"+passRandomValue, "transcaretextapp@gmail.com", toEmail);
            }

            catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Toast.makeText(getApplicationContext(), "Email send").show();
        }
    }
    public void entryAuth() {
        User user = new User();
        final UserDao userDao = new UserDao();
        user.setUserName(addEmpEmailId.getText().toString().trim());
        Log.d(TAG,"passRandomValue11.."+passRandomValue);
        user.setPassword(passRandomValue);
        user.setEmpId(addEmpEmployeeId.getText().toString().trim());
        user.setCompanyName("");
        user.setAuth("1");
        user.setRole("user");
        user.setStatus("login");
        user.setChatPin("");
        user.setTINorEIN("");
        user.setDesignation("");
        user.setFirstName("");
        user.setLastName("");
        random = new SecureRandom();
        senderID = new BigInteger(130, random).toString(32);
        String randomValue = senderID.substring(0, 7);
        Log.d("randomValue",randomValue);
        user.setSenderId(randomValue);
        user.setProviderName("");
        user.setProviderNPIId("");
        user.setProfilePjhoto("");
        boolean insertUser = userDao.createEmployee(user);
        Log.d(TAG, "Returned user result: " + insertUser);
        if (insertUser) {
            progressDialog.dismiss();
            Intent intent = new Intent(getActivity(), RootHomeActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "Add Employee register not successful!");
            Toast.makeText(getActivity(), "Please added another email and password!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), AddEmployeeActivity.class);
            startActivity(intent);
        }
    }

    public AddEmployeeActivity getActivity() { return this;  }

}
