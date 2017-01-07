package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddAdminActivity extends AppCompatActivity {

    private static final String TAG = AddAdminActivity.class.getCanonicalName();
    private FirebaseAuth firebaseAuthRef;
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private SecureRandom random;
    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    private Uri downloadUrl;

    private EditText adminEmailTxt, adminProviderNPItxt, adminProviderName;
    private Button addAminBtn;
    private String password;
    private String senderID;
    private String loggedINCompany;
    private String firstName;
    private String lastName;
    private User comInfos = new User();
    private String passRandomValue;
    GMailSender sender;
    private String toEmail;
    private String compEmailtxts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);
        mStorage = FirebaseStorage.getInstance().getReference();
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
    public void init() {
        Log.d(TAG, "Init method has been called!");
        adminEmailTxt = (EditText) findViewById(R.id.comp_email_address);
        toEmail = adminEmailTxt.getText().toString();
        adminProviderName = (EditText) findViewById(R.id.com_provider_name);
        adminProviderNPItxt = (EditText) findViewById(R.id.com_provider_npi_id);
        addAminBtn = (Button) findViewById(R.id.add_admin_btn);
        addAminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getActivity(), "Admin registered failed!", Toast.LENGTH_LONG).show();
                } else{
                    Log.d(TAG, "Admin registered successfully!");
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Added Admin...");
                    progressDialog.show();
                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.execute(adminProviderNPItxt.getText().toString());

                }

            }

            private boolean validate() {
                String compEmailtxts = adminEmailTxt.getText().toString().trim();
                String providerNpi = adminProviderNPItxt.getText().toString().trim();
                String providerNameText = adminProviderName.getText().toString().trim();
                boolean valid = true;
                if(!isValidEmail(compEmailtxts)){
                    adminEmailTxt.setError("Invalid Email");
                    valid = false;
                }

                if(providerNpi.isEmpty()||providerNpi.length()<4){
                    adminProviderNPItxt.setError("Provider NPI Id is invalid");
                    valid = false;
                }else{
                    adminProviderNPItxt.setError(null);
                }

                if(providerNameText.isEmpty()||providerNameText.length()<2){
                    adminProviderName.setError("Provider Name is invalid");
                    valid = false;
                }else{
                    adminProviderName.setError(null);
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

    private boolean isValidEmail(String compEmailtxts) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(compEmailtxts);
        return  matcher.matches();
    }

    public void checkUserExistence() {
        firebaseAuthRef = FirebaseAuth.getInstance();

        Log.d("TAG","randomValue..randomValue......"+passRandomValue);
        firebaseAuthRef.createUserWithEmailAndPassword(adminEmailTxt.getText().toString(),
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
        User comInfo = new User();
        final UserDao userDao = new UserDao();
        comInfo.setUserName(adminEmailTxt.getText().toString());
        /*random = new SecureRandom();
        password = new BigInteger(130, random).toString(32);
        String passwordRandomValue = password.substring(0, 8);
        Log.d("randomValue",passwordRandomValue);*/
        Log.d("TAG","randomValue11..randomValue11......"+passRandomValue+loggedINCompany+firstName+lastName);
        comInfo.setPassword(passRandomValue);
        comInfo.setCompanyName(loggedINCompany);
        comInfo.setTINorEIN("");
        comInfo.setProviderNPIId(adminProviderNPItxt.getText().toString());
        comInfo.setProviderName(adminProviderName.getText().toString());
        comInfo.setRole("admin");
        comInfo.setStatus("chatPin");
        comInfo.setEmpId("");
        comInfo.setAuth("1");
        comInfo.setChatPin("");
        comInfo.setDesignation("");
        comInfo.setFirstName(firstName);
        comInfo.setLastName(lastName);
        random = new SecureRandom();
        senderID = new BigInteger(130, random).toString(32);
        String senderIdRandomValue = senderID.substring(0, 7);
        Log.d("randomValue",senderIdRandomValue);
        comInfo.setSenderId(senderIdRandomValue);
        comInfo.setProfilePjhoto(String.valueOf(downloadUrl));
        Calendar c = Calendar.getInstance();
        String myFormat = "yyyy-MM-dd HH:mm:ss Z";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dateValue = sdf.format(c.getTime());
        comInfo.setCreateDate(dateValue);
        comInfo.setUpdateDate(dateValue);
        Log.d(TAG, "Company information's " + comInfo.toString());
        boolean data = userDao.createCompany(comInfo);
        if (data){
            progressDialog.dismiss();
            Log.d(TAG, "Add Admin  successfuly registered!");
            try {
                //  new MyAsyncClass().execute();
                MailSender runners = new MailSender();
                String  value = "Thanks for your registration, Please wait for HippaText admin's confirmation."+passRandomValue;
                runners.execute("Profile has been accepted!",value,"hipaatext123@gmail.com",adminEmailTxt.getText().toString());

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
            Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "Add Admin not successfuly registered!");
            Toast.makeText(getActivity(), "Please added another email address!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), AddAdminActivity.class);
            startActivity(intent);
        }
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
                URL url1;
                url1 = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "key=AIzaSyDGbtV6pU8idsFMADn905ynj4Y7UNK4ibI");
                JSONObject root = new JSONObject();
                root.put("title","notification");
                root.put("body","hi dddsdsdsd");
                JSONObject root1 = new JSONObject();
                root1.put("notification",root);
                root1.put("to","eU4j8cshvvI:APA91bFWU_RRESEGvrErTzxC_FUU15HywJkcs6Tv-AU49pmd18GfZ6SZoUj3PMy9cKtNpBw2eaw3RfI-xCuiXN4R6LdTYnQBxxKlUgQfSJz4kuBYTHtN3zJDFSc6k7feXPUCWisWsZ0h");
                root1.put("priority","high");
                Log.d(TAG,"rootValue1"+root1);
                Log.d(TAG,"rootValue"+root);
                OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
                wr.write(root1.toString());
                wr.flush();
                wr.close();
                int responsecode = conn.getResponseCode();

                if(responsecode == 200) {
                    Log.d(TAG,"success"+conn.getResponseMessage());
                }else{
                    Log.d(TAG,"error"+conn.getResponseMessage());
                }


                json = new JSONObject(IOUtils.toString(new URL(url),
                        Charset.forName("UTF-8")));
                JSONObject jsonObj = new JSONObject(json.toString());
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
                checkUserExistence();
            }else {
                Log.d(TAG,"Please enter valid npi id");
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Please enter valid NPI ID",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveImage() {
        final  String reArrangeEmailId = adminEmailTxt.getText().toString().replace(".", "-");
        Uri uri = Uri.parse("android.resource://com.bluemapletech.hippatextapp/" + R.drawable.user);
        Log.d("saveImagesaveImagew",reArrangeEmailId);
        StorageReference filePath = mStorage.child(reArrangeEmailId);
        Log.d("saveImagesaveImagee",reArrangeEmailId);
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

   /* class MyAsyncClass extends AsyncTask<Void, Void, Void> {
        String compEmailtxts = adminEmailTxt.getText().toString().trim();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... mApi) {
            Log.d(TAG,"mail method is called"+compEmailtxts);
            try {
                // Add subject, Body, your mail Id, and receiver mail Id.

                sender.sendMail("My App", " HI welcome to TCT Text Application"+passRandomValue, "hipaatext123@gmail.com", compEmailtxts);
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
    }*/

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

    public AddAdminActivity getActivity() {
        return this;
    }
}
