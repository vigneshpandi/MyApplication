package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeRegisterActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuthRef;
    private SecureRandom random;
    private ProgressDialog progressDialog;

    private static final String TAG = EmployeeRegisterActivity.class.getCanonicalName();

    private EditText emailTxt, passwordTxt, empIdTxt;
    private Button regBtn;
    private Spinner spinner;
    private String password;
    private String senderID;
    private StorageReference mStorage;
    private Uri uri;
    private StorageReference filePath;
    private User empInfos = new User();;
    private Uri downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getAllCompanyList();
        mStorage = FirebaseStorage.getInstance().getReference();
        init();
    }

    public void init() {
        Log.d(TAG, "Init method has been called!");
        emailTxt = (EditText) findViewById(R.id.emp_email_address);
        passwordTxt = (EditText) findViewById(R.id.emp_password);
        empIdTxt = (EditText) findViewById(R.id.emp_employee_id);
        regBtn = (Button) findViewById(R.id.emp_register_btn);
        spinner = (Spinner) findViewById(R.id.company_list);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getActivity(), "Employee registered failed!", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("registering...");
                    progressDialog.show();
                    checkUserExistence();
                }
            }

            private boolean validate() {
                String empEmailTxt = emailTxt.getText().toString().trim();
                String empPasswordTxt = passwordTxt.getText().toString().trim();
                String empIdTxts = empIdTxt.getText().toString().trim();
                View select = spinner.getSelectedView();

                boolean valid = true;
                if (!isValidEmail(empEmailTxt)) {
                    emailTxt.setError("Invalid Email");
                    valid = false;
                }

                if (empPasswordTxt.isEmpty() || empPasswordTxt.length() < 8 || empPasswordTxt.length() > 16) {
                    passwordTxt.setError("Password between 8 - 16 number and character");
                    valid = false;
                } else {
                    passwordTxt.setError(null);
                }

                if (empIdTxts.isEmpty()) {
                    empIdTxt.setError("EmployeeId is invalid");
                    valid = false;
                } else {
                    empIdTxt.setError(null);
                }

                String selectedItem = spinner.getSelectedItem().toString();
                View selectedView = spinner.getSelectedView();
                if (selectedItem.equalsIgnoreCase("Select Company") ||
                        selectedItem.equalsIgnoreCase("") && selectedView != null && selectedView instanceof TextView) {
                    TextView selectedTextView = (TextView) selectedView;
                    selectedTextView.setTextColor(Color.RED);
                    selectedTextView.setError("Please select company name!");
                    /*Toast.makeText(EmployeeRegisterActivity.this, "Please select company name!", Toast.LENGTH_SHORT).show();*/
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

                for (DataSnapshot companyList : dataSnapshot.getChildren()) {
                    String comName = companyList.child("companyName").getValue(String.class);
                    String name = "Select Company";
                    companyNames.add(name);
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
                    entryAuth();
                } else {
                    Toast.makeText(getActivity(), "Entered email address is already exists! ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void entryAuth() {
        User user = new User();
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
        user.setFirstName("");
        user.setLastName("");
        random = new SecureRandom();
        senderID = new BigInteger(130, random).toString(32);
        String randomValue = senderID.substring(0, 7);
        Log.d("randomValue", randomValue);
        user.setSenderId(randomValue);
        user.setProviderName("");
        user.setProviderNPIId("");
        user.setProfilePjhoto("");
        boolean insertUser = userDao.createEmployee(user);
        Log.d(TAG, "Returned user result: " + insertUser);
        if (insertUser) {
            progressDialog.dismiss();
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
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
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public EmployeeRegisterActivity getActivity() {
        return this;
    }
}
