package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.GMailSender;
import com.bluemapletech.hippatextapp.utils.MailSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEmployeeActivity extends AppCompatActivity {

    private static final String TAG = AddAdminActivity.class.getCanonicalName();
    private FirebaseAuth firebaseAuthRef;
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private SecureRandom random;
    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    private Uri downloadUrl;
    private EditText addEmpEmailId, addEmpEmployeeId;
    private Button addEmpBtn;
    private String senderID,isOnline,password,passRandomValue,loggedINCompany,toEmail;
    GMailSender sender;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private FirebaseDatabase fireBaseDatabase;
    private FirebaseAuth firebaseAuth;
    private String loginMail; // loginDetail string declare
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        TextView header = (TextView) findViewById(R.id.header);
        header.setText("Add Employee");
        //login details
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref.getString("isOnline", "");
        loginMail =  pref.getString("loginMail", "");


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Add Employee");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mStorage = FirebaseStorage.getInstance().getReference();
        random = new SecureRandom();
        password = new BigInteger(130, random).toString(32);
        String randomValue = password.substring(0, 8);
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
                    Toast.makeText(getActivity(), "Add Employee failed!", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Processing Add Employee...");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    checkUserExistence();
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
                if(addEmailId.isEmpty()){
                    addEmpEmailId.setError("Email address is required");
                    valid = false;
                }
                if(addEmpId.isEmpty()){
                    addEmpEmployeeId.setError("EMployee Id is required");
                    valid = false;
                }else{
                    addEmpEmployeeId.setError(null);
                }

                if(addEmpId.length()< 2){
                    addEmpEmployeeId.setError("EMployee Id is invalid");
                    valid = false;
                }else{
                    addEmpEmployeeId.setError(null);
                }
                return valid;
            }
        });
        getCompanyValue();
    }
    private void getCompanyValue() {
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        firebaseAuthRef = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuthRef.getCurrentUser();
        String reArrangeEmail = logged.getEmail().replace(".", "-");
        DatabaseReference dataReferences = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedINCompany = (String) dataSnapshot.child("companyName").getValue();
                Log.d(TAG,"loggedINCompany...."+loggedINCompany);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    saveImage();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Entered email address is already exists! ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void entryAuth() {
        User user = new User();
        final UserDao userDao = new UserDao();
        user.setUserName(addEmpEmailId.getText().toString().trim());
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
        user.setEmpId(addEmpEmployeeId.getText().toString().trim());
        user.setCompanyName(loggedINCompany);
        user.setAuth("1");
        user.setRole("user");
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
        Calendar c = Calendar.getInstance();
        String myFormat = "yyyy-MM-dd HH:mm:ss Z";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dateValue = sdf.format(c.getTime());
        user.setCreateDate(dateValue);
        user.setUpdateDate(dateValue);
        user.setProfilePjhoto(String.valueOf(downloadUrl));
        boolean insertUser = userDao.createEmployee(user);
        Log.d(TAG, "Returned user result: " + insertUser);
        if (insertUser) {
            progressDialog.dismiss();
            try {
                MailSender runners = new MailSender();
                String  value = "Thanks for your registration, Please wait your admin's confirmation.\n" +
                        "Thanks for showing your interest."+passRandomValue;
                runners.execute("Profile has been accepted!",value,"hipaatext123@gmail.com",addEmpEmailId.getText().toString());

            } catch (Exception ex) {
            }
            Toast.makeText(getActivity(), "Employee addedd successfully!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "Add Employee register not successful!");
            Toast.makeText(getActivity(), "Please added another email and password!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), AddEmployeeActivity.class);
            startActivity(intent);
        }
    }
    private void saveImage() {
        final  String reArrangeEmailId = addEmpEmailId.getText().toString().replace(".", "-");
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
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
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


    public AddEmployeeActivity getActivity() { return this;  }

}
