package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
    private String password,senderID,toEmail,firstName,lastName;
    private StorageReference mStorage;
    private Uri uri;
    private StorageReference filePath;
    GMailSender sender;
    private User comInfos = new User();
    private Uri downloadUrl;
    final UserDao userDao = new UserDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        TextView header = (TextView) findViewById(R.id.header);
        header.setText("Company Registration");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Company Registration");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mStorage = FirebaseStorage.getInstance().getReference();
        init();
    }

    public void init() {
        Log.d(TAG, "Init method has been called!");
        compEmailtxt = (EditText) findViewById(R.id.comp_email_address);
       // toEmail = compEmailtxt.getText().toString();
        compPasswordtxt = (EditText) findViewById(R.id.comp_password);
        companyName = (EditText) findViewById(R.id.company_name);
        companyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        compEinOrTinNo = (EditText) findViewById(R.id.comp_tin_or_ein_no);
        compRegBtn = (Button) findViewById(R.id.comp_register_btn);
        providerName = (EditText) findViewById(R.id.provider_name1);
        providerNPIId = (EditText) findViewById(R.id.provider_npi_id);
        compRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getActivity(), "Company registered failed!", Toast.LENGTH_LONG).show();
                } else{
                    Log.d(TAG, "Company registered successfully!");
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Confirm your Email Address");
                    // Setting Icon to Dialog
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setMessage("Are you sure do you want to use this email address " + compEmailtxt.getText());
                    // Setting Positive "Yes" Button
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog = new ProgressDialog(getActivity());
                            Log.d(TAG,"npi.."+providerNPIId.getText().toString());
                            progressDialog.setMessage("registering...");
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(false);
                            AsyncTaskRunner runner = new AsyncTaskRunner();
                            runner.execute(providerNPIId.getText().toString());

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
                String providerNpi = providerNPIId.getText().toString().trim();
                String providerNameText = providerName.getText().toString().trim();
                boolean valid = true;
                if (!isValidEmail(compEmailtxts)) {
                    Log.d(TAG,"eneter invalid email");
                    compEmailtxt.setError("Invalid Email");
                    valid = false;
                }
                if(compEmailtxts.isEmpty()){
                    compEmailtxt.setError("Company email address is required");
                    valid = false;
                }
                if(compPasswordtxts.isEmpty()){
                    compPasswordtxt.setError("Company password is required");
                    valid = false;
                }else if(compPasswordtxts.length()< 6|| compPasswordtxts.length()> 16){
                    compPasswordtxt.setError("Password between 6 - 16 number and character");
                    valid = false;
                }else{
                    compPasswordtxt.setError(null);
                }
                if(companyNames.isEmpty()){
                    companyName.setError("Company Name is required");
                    valid = false;
                }else if(companyNames.length() < 2){
                    companyName.setError("Company Name is invalid");
                    valid = false;
                }else{
                    companyName.setError(null);
                }
                if(compEinOrTinNos.isEmpty()){
                    compEinOrTinNo.setError("Company EIN/TIN No is required");
                    valid = false;
                }else if(compEinOrTinNos.length() < 2){
                    compEinOrTinNo.setError("Company EIN/TIN No is invalid");
                    valid = false;
                }else{
                    compEinOrTinNo.setError(null);
                }
                if(providerNpi.isEmpty()){
                    providerNPIId.setError("Provider NPI Id is required");
                    valid = false;
                }else if(providerNpi.length() != 10){
                    providerNPIId.setError("Provider NPI Id is invalid");
                    valid = false;
                }else{
                    providerNPIId.setError(null);
                }
                if(providerNameText.isEmpty()){
                    providerName.setError("Provider Name is required");
                    valid = false;
                }else if(providerNameText.length()<2 || !providerNameText.toString().matches("[a-zA-Z ]+")){
                    providerName.setError("Provider Name is invalid");
                    valid = false;
                }else{
                    providerName.setError(null);
                }
                return valid;
            }
        });
    }

    private boolean isValidEmail(String compEmailtxts) {
        Log.d(TAG,"eneter invalid email1");
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(compEmailtxts);
        return matcher.matches();
    }

    public void checkUserExistence() {
        firebaseAuthRef = FirebaseAuth.getInstance();
        firebaseAuthRef.createUserWithEmailAndPassword(compEmailtxt.getText().toString(),
                compPasswordtxt.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                   saveImage();
                   // entryAuth();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Entered email address is already exists! ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void entryAuth() {
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference().child("registeredCompanyName");
        String rearrangeCompany = companyName.getText().toString().replace(".","-");

        databaseRef.child(rearrangeCompany).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "On data change method has been called!");
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "Company name already exists!");
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Entered Company name already exists! ",Toast.LENGTH_LONG).show();
                } else {
                    checkUserExistence();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
public  void registerCompanyDetails(){
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
    comInfo.setFirstName(firstName);
    comInfo.setLastName(lastName);
    random = new SecureRandom();
    senderID = new BigInteger(130, random).toString(32);
    String randomValue = senderID.substring(0, 7);
    comInfo.setSenderId(randomValue);
    comInfo.setIsOnlie("true");
    comInfo.setProfilePjhoto(String.valueOf(downloadUrl));
    Calendar c = Calendar.getInstance();
    String myFormat = "yyyy-MM-dd HH:mm:ss Z";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    String dateValue = sdf.format(c.getTime());
    comInfo.setCreateDate(dateValue);
    comInfo.setUpdateDate(dateValue);
    boolean data = userDao.createCompany(comInfo);
    if (data){
        progressDialog.dismiss();
        try {
            MailSender runners = new MailSender();
            runners.execute("Profile has been created!","Thanks for your registration, Please wait for HippaText admin's confirmation.","hipaatext123@gmail.com",compEmailtxt.getText().toString());

        } catch (Exception ex) {

        }
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                BackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void BackPressed() {
        Log.d(TAG,"back press clicked");
        startActivity(new Intent(getActivity(),HomeActivity.class));
    }


    private void saveImage() {
        final  String reArrangeEmailId = compEmailtxt.getText().toString().replace(".", "-");
        Log.d(TAG,"reArrangeEmailId...."+reArrangeEmailId);
        Uri uri = Uri.parse("android.resource://com.bluemapletech.hippatextapp/" + R.drawable.user);
        StorageReference filePath = mStorage.child(reArrangeEmailId);
        Log.d(TAG,"filePath.."+filePath.toString());
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                Log.d(TAG,"success..");
                Log.d(TAG,"downloadUrl"+downloadUrl);
                registerCompanyDetails();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"failure..");
            }
        });
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "params: " + params[0]);
            String url = "https://npiregistry.cms.hhs.gov/api?pretty=true&limit=1";
            url = url + "&number=" + params[0];
            Log.d(TAG, url);
            Object json = null;
            try {
                json = new JSONObject(IOUtils.toString(new URL(url),
                        Charset.forName("UTF-8")));
                JSONObject jsonObj = new JSONObject(json.toString());
                Log.d(TAG,"responseNPI"+jsonObj);
                JSONArray data = jsonObj.getJSONArray("results");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonOBject = data.getJSONObject(i);
                    firstName = String.valueOf(jsonOBject.getJSONObject("basic").get("first_name"));
                    lastName = String.valueOf(jsonOBject.getJSONObject("basic").get("last_name"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json.toString();
        }

        @Override
        protected void onPostExecute(String result) {
           // providerNameTxt.setText(firstName + " " + lastName);
            Log.d(TAG,firstName + " " + lastName);
            if(firstName !=null && !firstName.isEmpty()) {
                Log.d(TAG,"not empty");
               // checkUserExistence();
                entryAuth();
            }else {
                Log.d(TAG,"Please enter valid npi id");
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Please enter valid NPI ID",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            BackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public CompanyRegistrationActivity getActivity() {
        return this;
    }
}
