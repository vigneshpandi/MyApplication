package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeRegisterActivity extends AppCompatActivity {
    private static final String TAG = EmployeeRegisterActivity.class.getCanonicalName();
    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuthRef;
    private SecureRandom random;
    private ProgressDialog progressDialog;
    private EditText firstName, lastName, emailTxt, passwordTxt, empIdTxt;
    private Button regBtn;
    private Spinner spinner;
    private String password,senderID;
    private StorageReference mStorage;
    private Uri uri;
    private StorageReference filePath;
    private User empInfos = new User();
    private Uri downloadUrl;
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Employee Registration");
        }
        getAllCompanyList();
        mStorage = FirebaseStorage.getInstance().getReference();
        init();
    }

    public void init() {
        Log.d(TAG, "Init method has been called!");
        firstName = (EditText) findViewById(R.id.emp_first_name1);
        //firstName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        lastName = (EditText) findViewById(R.id.emp_last_name1);
        //lastName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        emailTxt = (EditText) findViewById(R.id.emp_email_address);
        passwordTxt = (EditText) findViewById(R.id.emp_password);
        empIdTxt = (EditText) findViewById(R.id.emp_employee_id);
        regBtn = (Button) findViewById(R.id.emp_register_btn);
        spinner = (Spinner) findViewById(R.id.company_list);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner!=null){
                if (!validate()) {
                    Toast.makeText(getActivity(), "Employee registered failed!", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Conform your Email Address");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setMessage("Are you sure do you want to use this email address " + emailTxt.getText());
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage("registering...");
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(false);

                            checkUserExistence();
                        }
                    });
                    dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            }}

            private boolean validate() {
                String empFirstName = firstName.getText().toString();
                String empLastName = lastName.getText().toString();
                String empEmailTxt = emailTxt.getText().toString().trim();
                String empPasswordTxt = passwordTxt.getText().toString().trim();
                String empIdTxts = empIdTxt.getText().toString().trim();
                View select = spinner.getSelectedView();

                boolean valid = true;
                if (!isValidEmail(empEmailTxt)) {
                    emailTxt.setError("Invalid Email");
                    valid = false;
                }
                if (empEmailTxt.isEmpty()) {
                    emailTxt.setError("Employee email address is required");
                    valid = false;
                }
                if (empPasswordTxt.isEmpty()) {
                    passwordTxt.setError("Employee password is required");
                    valid = false;
                } else if (empPasswordTxt.length() < 6 || empPasswordTxt.length() > 16) {
                    passwordTxt.setError("Employee password between 6 - 16 number and character");
                    valid = false;
                } else {
                    passwordTxt.setError(null);
                }
                if (empIdTxts.isEmpty()) {
                    empIdTxt.setError("EmployeeId is invalid");
                    valid = false;
                } else if (empIdTxts.length()<2) {
                    empIdTxt.setError("EmployeeId is invalid");
                    valid = false;
                } else {
                    empIdTxt.setError(null);
                }

                if(empFirstName.isEmpty()){
                    firstName.setError("Employee First Name is required");
                    valid = false;
                }else if(empFirstName.length() < 2 || empFirstName.toString().matches("[A-Z][a-z]+( [A-Z][a-z]+ )*")){
                    firstName.setError("Employee First Name is invalid");
                    valid = false;
                }else{
                    firstName.setError(null);
                }

                if(empLastName.isEmpty()){
                    lastName.setError("Employee Last Name is required");
                    valid = false;
                }else if(empLastName.length() < 2 || empLastName.toString().matches("[A-Z][a-z]+( [A-Z][a-z]+ )*")){
                    lastName.setError("Employee Last Name is invalid");
                    valid = false;
                }else{
                    lastName.setError(null);
                }
                String selectedItem = null;
                if(spinner != null && spinner.getSelectedItem() !=null ) {
                    selectedItem  = spinner.getSelectedItem().toString();
                    View selectedView = spinner.getSelectedView();
                    if (selectedItem.equalsIgnoreCase("Select Company") ||
                            selectedItem.equalsIgnoreCase("") && selectedView != null && selectedView instanceof TextView) {
                        TextView selectedTextView = (TextView) selectedView;
                        selectedTextView.setTextColor(Color.RED);
                        selectedTextView.setText("Select Company");
                        selectedTextView.setError("Please select company name!");
                        valid = false;
                    }
                }

                return valid;
            }
        });
    }


    private boolean isValidEmail(String empEmailTxt) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(empEmailTxt);
        return matcher.matches();
    }

    public void getAllCompanyList() {
        Log.d(TAG, "Get all list company has been called!");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference();
        databaseRef.child("approvedCompany").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> companyNames = new ArrayList<String>();
                String name = "Select Company";
                companyNames.add(name);

                for (DataSnapshot companyList : dataSnapshot.getChildren()) {
                    String comName = companyList.child("companyName").getValue(String.class);
                    companyNames.add(comName);
                }
                ArrayAdapter<String> companyAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, companyNames);
                companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(companyAdapter);
                Log.d(TAG, companyNames.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void checkUserExistence() {
        firebaseAuthRef = FirebaseAuth.getInstance();
        firebaseAuthRef.createUserWithEmailAndPassword(emailTxt.getText().toString()
                , passwordTxt.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveImage();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Entered email address is already exists! ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void entryAuth() {
        password = passwordTxt.getText().toString();
        byte[] data = new byte[0];
        try {
            data = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final UserDao userDao = new UserDao();
        user.setUserName(emailTxt.getText().toString());
        String enco = Base64.encodeToString(data, Base64.NO_WRAP);
        user.setPassword(enco);
        user.setEmpId(empIdTxt.getText().toString());
        user.setCompanyName(spinner.getSelectedItem().toString());
        user.setAuth("0");
        user.setRole("user");
        user.setStatus("chatPin");
        user.setChatPin("");
        user.setTINorEIN("");
        user.setDesignation("");
        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
        random = new SecureRandom();
        senderID = new BigInteger(130, random).toString(32);
        String randomValue = senderID.substring(0, 7);
        Log.d("randomValue", randomValue);
        user.setSenderId(randomValue);
        user.setProviderName("");
        user.setProviderNPIId("");
        user.setProfilePjhoto(String.valueOf(downloadUrl));
        user.setIsOnlie("true");
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
                String subject = "Thanks for your registration, Please wait your admin's confirmation.\n" +
                        "Thanks for showing your interest.";
                MailSender runners = new MailSender();
                runners.execute("Profile has been created!",subject,"hipaatext123@gmail.com",emailTxt.getText().toString());

            } catch (Exception ex) {
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Thank You For Registering");
            alertDialog.setMessage("You will receive an email once we verify the  details");

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }
            });
            alertDialog.show();
        } else {
            Log.d(TAG, "Employee insertion not successful!");
            Toast.makeText(getActivity(), "Employee already Exists, Please login with your email and password!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), EmployeeRegisterActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
       final  String reArrangeEmailId = emailTxt.getText().toString().replace(".", "-");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            BackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public EmployeeRegisterActivity getActivity() {
        return this;
    }
}
