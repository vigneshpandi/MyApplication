package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.MailSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
    private String passRandomValue;
    private User empInfos = new User();
    private StorageReference mStorage;
    private Uri downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_root);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Add Root");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        random = new SecureRandom();
        password = new BigInteger(130, random).toString(32);
        String randomValue = password.substring(0, 8);
        Log.d("randomValue",randomValue);
        passRandomValue = randomValue.toString();
    }

    private void init() {
        Log.d(TAG, "Init method has been called!");

        addRootEmailId = (EditText) findViewById(R.id.email_address);
        addRootBtn = (Button) findViewById(R.id.submit_btn);

        addRootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getActivity(), "Add Root failed!", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Processing Add Root...");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    checkUserExistence();
                }
            }
            private boolean validate() {
                String addEmailId = addRootEmailId.getText().toString().trim();
                boolean valid = true;
                if(!isValidEmail(addEmailId)){
                    addRootEmailId.setError("Invalid Email");
                    valid = false;
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
                , passRandomValue).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
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
        final UserDao userDao = new UserDao();
        user.setUserName(addRootEmailId.getText().toString());
        Log.d("TAG","randomValue11..randomValue11......"+passRandomValue);
        String userPassword = passRandomValue;
        byte[] enCode = new byte[0];
        try {
            enCode = userPassword.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String enCodes = Base64.encodeToString(enCode, Base64.NO_WRAP);
        Log.d(TAG,"encrypt password"+enCodes);
        user.setPassword(enCodes);
        user.setEmpId("");
        user.setCompanyName("");
        user.setAuth("1");
        user.setRole("root");
        user.setStatus("chatPin");
        user.setChatPin("");
        user.setTINorEIN("");
        user.setDesignation("");
        user.setFirstName("");
        user.setLastName("");
        user.setIsOnlie("true");
        random = new SecureRandom();
        senderID = new BigInteger(130, random).toString(32);
        String randomValue = senderID.substring(0, 7);
        Log.d("randomValue",randomValue);
        user.setSenderId(randomValue);
        user.setProviderName("");
        user.setProviderNPIId("");
        user.setProfilePjhoto("");
        Calendar c = Calendar.getInstance();
        String myFormat = "yyyy-MM-dd HH:mm:ss Z";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dateValue = sdf.format(c.getTime());
        user.setCreateDate(dateValue);
        user.setUpdateDate(dateValue);
        boolean insertUser = userDao.createEmployee(user);
        Log.d(TAG, "Returned user result: " + insertUser);
        if (insertUser) {
            progressDialog.dismiss();
            try {
                //  new MyAsyncClass().execute();
                MailSender runners = new MailSender();
                String  value = "This email is to notify you that your password has been created successfully in HippaText.\n" +
                        "Please use this password to login:"+ passRandomValue+"\n"+
                        "Thanks for showing your interest.";
                runners.execute("Profile has been accepted!",value,"hipaatext123@gmail.com",addRootEmailId.getText().toString());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            finish();
            Intent intent = new Intent(getActivity(), RootHomeActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "Add Root not successful!");
            Toast.makeText(getActivity(), "Please added another email and password!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), AddRootActivity.class);
            startActivity(intent);
        }
    }
    private void saveImage() {
        final  String reArrangeEmailId = addRootEmailId.getText().toString().replace(".", "-");
        Uri uri = Uri.parse("android.resource://com.bluemapletech.hippatextapp/" + R.drawable.user);
        StorageReference filePath = mStorage.child(reArrangeEmailId);
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                Log.d(TAG,"downloadUrl"+downloadUrl);
                entryAuth();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                backPage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backPage() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),RootHomeActivity.class));
    }


    public AddRootActivity getActivity() { return this;  }
}
