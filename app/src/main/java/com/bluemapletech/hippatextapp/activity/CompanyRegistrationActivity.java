package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.CompanyDao;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompanyRegistrationActivity extends AppCompatActivity {

    private static final String TAG = CompanyRegistrationActivity.class.getCanonicalName();

    private FirebaseAuth firebaseAuthRef;
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private SecureRandom random;
    private ProgressDialog progressDialog;

    private EditText compEmailtxt, compPasswordtxt, companyName, compEinOrTinNo,providerName,providerNPIId;
    private Button compRegBtn;
    private String password;
    private String senderID;
    private StorageReference mStorage;
    private Uri uri;
    private StorageReference filePath;

    private User comInfos = new User();;
    private Uri downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mStorage = FirebaseStorage.getInstance().getReference();
        init();
    }

    public void init() {
        Log.d(TAG, "Init method has been called!");
        compEmailtxt = (EditText) findViewById(R.id.comp_email_address);
        compPasswordtxt = (EditText) findViewById(R.id.comp_password);
        companyName = (EditText) findViewById(R.id.company_name);
        companyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        compEinOrTinNo = (EditText) findViewById(R.id.comp_tin_or_ein_no);
        compRegBtn = (Button) findViewById(R.id.comp_register_btn);
        providerName = (EditText) findViewById(R.id.provider_name);
        providerNPIId = (EditText) findViewById(R.id.provider_npi_id);
        compRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getActivity(), "Company registered failed!", Toast.LENGTH_LONG).show();
                } else{
                    Log.d(TAG, "Company registered successfully!");
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Conform your Email Address");
                // Setting Icon to Dialog
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setMessage("Are you sure do you want to use this email address " + compEmailtxt.getText());
                // Setting Positive "Yes" Button
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("registering...");
                        progressDialog.show();
                        checkUserExistence();
                    }
                });
                // Setting Negative "NO" Button
                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // Showing Alert Message
                dialog.show();
            }
            }

            private boolean validate() {
                String compEmailtxts = compEmailtxt.getText().toString().trim();
                String compPasswordtxts = compPasswordtxt.getText().toString().trim();
                String companyNames = companyName.getText().toString().trim();
                String compEinOrTinNos = compEinOrTinNo.getText().toString().trim();
                boolean valid = true;
                if(!isValidEmail(compEmailtxts)){
                    compEmailtxt.setError("Invalid Email");
                    valid = false;
                }

                if(compPasswordtxts.isEmpty()||compPasswordtxts.length()< 8|| compPasswordtxts.length()> 16){
                    compPasswordtxt.setError("Password between 8 - 16 number and character");
                    valid = false;
                }else{
                    compPasswordtxt.setError(null);
                }

                if(companyNames.isEmpty()|| companyNames.length() < 2){
                    companyName.setError("Company Name is invalid");
                    valid = false;
                }else{
                    companyName.setError(null);
                }

                if(compEinOrTinNos.isEmpty()||compEinOrTinNos.length() < 2){
                    compEinOrTinNo.setError("Company EIN/TIN No is invalid");
                    valid = false;
                }else{
                    compEinOrTinNo.setError(null);
                }
                return valid;
            }
        });
    }

    private boolean isValidEmail(String compEmailtxts) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(compEmailtxts);
        return  matcher.matches();
    }

    public void checkUserExistence() {
        firebaseAuthRef = FirebaseAuth.getInstance();
        firebaseAuthRef.createUserWithEmailAndPassword(compEmailtxt.getText().toString(),
                compPasswordtxt.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    entryAuth();
                }else{
                    Toast.makeText(getActivity(), "Entered email address is already exists! ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void entryAuth() {
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference().child("companyName");

        final UserDao userDao = new UserDao();

        databaseRef.child(companyName.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "On data change method has been called!");
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "Company name already exists!");
                } else {
                    User comInfo = new User();
                    password = compPasswordtxt.getText().toString();
                    byte[] enCode = new byte[0];
                    try {
                        enCode = password.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String enCodes = Base64.encodeToString(enCode, Base64.NO_WRAP);
                    comInfo.setUserName(compEmailtxt.getText().toString());
                    comInfo.setPassword(enCodes);
                    comInfo.setCompanyName(companyName.getText().toString());
                    comInfo.setTINorEIN(compEinOrTinNo.getText().toString());
                    comInfo.setProviderNPIId(providerNPIId.getText().toString());
                    comInfo.setProviderName(providerName.getText().toString());
                    comInfo.setRole("admin");
                    comInfo.setStatus("chatPin");
                    comInfo.setEmpId("");
                    comInfo.setAuth("0");
                    comInfo.setChatPin("");
                    comInfo.setDesignation("");
                    comInfo.setFirstName("");
                    comInfo.setLastName("");
                    random = new SecureRandom();
                    senderID = new BigInteger(130, random).toString(32);
                    String randomValue = senderID.substring(0, 7);
                    Log.d("randomValue",randomValue);
                    comInfo.setSenderId(randomValue);
                    comInfo.setProfilePjhoto("");
                    Log.d(TAG, "Company information's " + comInfo.toString());
                    boolean data = userDao.createCompany(comInfo);
                  if (data){
                        progressDialog.dismiss();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Thank You Registering");
                        alertDialog.setMessage("You will receive an email once we verify the company details, if the company is exists");
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }else{
                        progressDialog.dismiss();
                        Log.d(TAG, "Error while inserting the company details!");
                        Toast.makeText(getActivity(),"Error while inserting the company details!",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public CompanyRegistrationActivity getActivity() {
        return this;
    }
}
