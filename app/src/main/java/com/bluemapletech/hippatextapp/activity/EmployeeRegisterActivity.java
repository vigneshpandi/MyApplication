package com.bluemapletech.hippatextapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeRegisterActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabaseRef;
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuthRef;

    private ProgressDialog progressDialog;

    private static final String TAG = EmployeeRegisterActivity.class.getCanonicalName();

    private EditText emailTxt, passwordTxt, empIdTxt;
    private Button regBtn;
    private Spinner spinner;

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
                String empEmailTxt= emailTxt.getText().toString().trim();
                String empPasswordTxt= passwordTxt.getText().toString().trim();
                String empIdTxts = empIdTxt.getText().toString().trim();

                boolean valid = true;
              /*  View selectedView = spinner.getSelectedView();
                if (selectedView != null && selectedView instanceof TextView) {
                    TextView selectedTextView = (TextView) selectedView;
                    if (!valid) {

                        selectedTextView.setError("Company Name is invalid");
                    }
                    else {
                        selectedTextView.setError(null);
                    }
                }*/
                if(!isValidEmail(empEmailTxt)){
                    emailTxt.setError("Invalid Email");
                    valid = false;
                }

                if(empPasswordTxt.isEmpty()||empPasswordTxt.length()< 8|| empPasswordTxt.length()> 16){
                    passwordTxt.setError("Password between 8 - 16 number and character");
                    valid = false;
                }else{
                    passwordTxt.setError(null);
                }

                if(empIdTxts.isEmpty()){
                    empIdTxt.setError("EmployeeId is invalid");
                    valid = false;
                }else{
                    empIdTxt.setError(null);
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
        return  matcher.matches();
    }

    public void getAllCompanyList() {
        Log.d(TAG, "Get all list company has been called!");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabaseRef.getReference();
        databaseRef.child("companyName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> companyNames = new ArrayList<String>();

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
                    entryAuth();
                } else {
                    // Toast.makeText(getActivity(), "Entered email address is already exists! ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void entryAuth() {
        User user = new User();
        final UserDao userDao = new UserDao();
        user.setUserName(emailTxt.getText().toString());
        user.setPassword(passwordTxt.getText().toString());
        user.setEmpId(empIdTxt.getText().toString());
        user.setCompanyName(spinner.getSelectedItem().toString());
        user.setRole("user");
        user.setChatPin("");
        user.setStatus("chatPin");
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
        switch (item.getItemId()){
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
