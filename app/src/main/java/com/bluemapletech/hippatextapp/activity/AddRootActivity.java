package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class AddRootActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuthRef;
    private SecureRandom random;
    private ProgressDialog progressDialog;
    private static final String TAG = AddRootActivity.class.getCanonicalName();
    private EditText addRootEmailId, addRootPassword;
    private Button addRootBtn;
    private String password;
    private String senderID;
    private User empInfos = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_root);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void init() {
        Log.d(TAG, "Init method has been called!");

        addRootEmailId = (EditText) findViewById(R.id.email_address);
        addRootPassword = (EditText) findViewById(R.id.password);
        addRootBtn = (Button) findViewById(R.id.submit_btn);

        addRootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getActivity(), "Add Root failed!", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Added Root...");
                    progressDialog.show();
                    checkUserExistence();
                }
            }
            private boolean validate() {
                String addEmailId = addRootEmailId.getText().toString().trim();
                String addPassword = addRootPassword.getText().toString().trim();
                boolean valid = true;
                if(!isValidEmail(addEmailId)){
                    addRootEmailId.setError("Invalid Email");
                    valid = false;
                }

                if(addPassword.isEmpty()||addPassword.length()< 8|| addPassword.length()> 16){
                    addRootPassword.setError("Password between 8 - 16 number and character");
                    valid = false;
                }else{
                    addRootPassword.setError(null);
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
        firebaseAuthRef.createUserWithEmailAndPassword(addRootEmailId.getText().toString()
                , addRootPassword.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    entryAuth();
                } else {
                    // Toast.makeText(getActivity(), "Entered email address is already exists! ",Toast.LENGTH_LONG).show();
                    Log.d("already exist","already exist");
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Email address is already exists,Please added another root...!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void entryAuth() {
        User user = new User();
        password = addRootPassword.getText().toString();
        byte[] data = new byte[0];
        try {
            data = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final UserDao userDao = new UserDao();
        user.setUserName(addRootEmailId.getText().toString());
        String encoPass = Base64.encodeToString(data, Base64.NO_WRAP);
        user.setPassword(encoPass);
        user.setEmpId("");
        user.setCompanyName("");
        user.setAuth("1");
        user.setRole("root");
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
            Log.d(TAG, "Add Root not successful!");
            Toast.makeText(getActivity(), "Please added another email and password!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), AddRootActivity.class);
            startActivity(intent);
        }
    }

    public AddRootActivity getActivity() {

        return this;
    }
}
