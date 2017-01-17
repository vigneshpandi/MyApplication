package com.bluemapletech.hippatextapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageAdminBaseAdapter;
import com.bluemapletech.hippatextapp.adapter.PageBaseAdapter;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.data;
import static android.R.attr.value;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "aaa==="+EditProfileActivity.class.getCanonicalName();
    private EditText editFirstName, editLastName, editEmail, editCompanyName, editEmployeeId, editDesignation;
    private Button updateProfileBtn;
    private ImageView userImage;
    final private int SELECT_FILE = 1;
    final private int REQUEST_CAMERA = 2;
    private String base64Profile,reArrangeEmail;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    private DatabaseReference databaseRef;
    private StorageReference mStorage;
    Uri value;
    Uri downloadUrl;
    User user = new User();
    private ProgressDialog progressDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private String comNames, emailAddress, firstName, designation, lastName, userId, auth, chatPin, companyCin, password, profile;
    private String providerNPI, providerName, notification, role, senderId, status, createDate, updateDate;
    private String compFirstName, compLastName, compEmail, compCompany, compEmployee, compDesignation;
    private String loginRole, loginAuth,isOnline;
    Bitmap bm = null;
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        fireBaseDatabase = FirebaseDatabase.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginRole = pref.getString("role", "");
        loginAuth = pref.getString("auth", "");
        init();
    }

    private void init() {
        Log.d(TAG, "init method called");
        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        editFirstName = (EditText) findViewById(R.id.com_first_name);
        editLastName = (EditText) findViewById(R.id.com_last_name);
        editEmail = (EditText) findViewById(R.id.edit_com_email);
        editCompanyName = (EditText) findViewById(R.id.edit_com_companyname);
        editEmployeeId = (EditText) findViewById(R.id.edit_emp_id);
        editDesignation = (EditText) findViewById(R.id.edit_designation);
        updateProfileBtn = (Button) findViewById(R.id.update_profile);
        userImage = (ImageView) findViewById(R.id.user_image);
        Log.d(TAG, "logged....." + logged);
        if (logged != null) {
            Log.d("logged", logged.toString());
            reArrangeEmail = logged.getEmail().replace(".", "-");
        }
        databaseRef = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                comNames = map.get("companyName");
                emailAddress = map.get("emailAddress");
                designation = map.get("designation");
                firstName = map.get("firstName");
                lastName = map.get("lastName");
                userId = map.get("employeeId");
                auth = map.get("auth");
                chatPin = map.get("chatPin");
                companyCin = map.get("companyCINNumber");
                password = map.get("password");
                profile = map.get("profilePhoto");
                providerNPI = map.get("providerNPIId");
                providerName = map.get("providerName");
                notification = map.get("pushNotificationId");
                createDate = map.get("createdDate");
                updateDate = map.get("updatedDate");
                isOnline = map.get("showOnline");
                role = map.get("role");
                senderId = map.get("senderId");
                status = map.get("status");
                editFirstName.setText(firstName);
                editLastName.setText(lastName);
                editEmployeeId.setText(userId);
                editCompanyName.setText(comNames);
                editEmail.setText(emailAddress);
                editDesignation.setText(designation);
                Picasso.with(EditProfileActivity.this).load(profile).fit().centerCrop().into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Log.d(TAG,"progressBar is not used..");
                    Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG,"progrssBar is show...");
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Processing update profile...");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    saveImage();
                }
            }

            private boolean validate() {
                compFirstName = editFirstName.getText().toString().trim();
                compLastName = editLastName.getText().toString().trim();
                compEmail = editEmail.getText().toString().trim();
                compCompany = editCompanyName.getText().toString().trim();
                compEmployee = editEmployeeId.getText().toString().trim();
                compDesignation = editDesignation.getText().toString().trim();
                boolean valid = true;
                if (!isValidEmail(compEmail)) {
                    editEmail.setError("Invalid Email");
                    valid = false;
                }

                if (compFirstName.isEmpty() || compFirstName.length() < 2) {
                    editFirstName.setError("Provider NPI Id is invalid");
                    valid = false;
                } else {
                    editFirstName.setError(null);
                }

                if (compLastName.isEmpty() || compLastName.length() < 2) {
                    editLastName.setError("Provider Name is invalid");
                    valid = false;
                } else {
                    editLastName.setError(null);
                }

                if (compCompany.isEmpty() || compCompany.length() < 2) {
                    editCompanyName.setError("Provider Name is invalid");
                    valid = false;
                } else {
                    editCompanyName.setError(null);
                }

                if (compEmployee.isEmpty() || compEmployee.length() < 2) {
                    editEmployeeId.setError("Provider Name is invalid");
                    valid = false;
                } else {
                    editEmployeeId.setError(null);
                }

                if (compDesignation.isEmpty() || compDesignation.length() < 2) {
                    editDesignation.setError("Provider Name is invalid");
                    valid = false;
                } else {
                    editDesignation.setError(null);
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
        return matcher.matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (role.equals("admin") && loginAuth.matches("1")) {
                    backPageAdmin();
                } else if (role.equals("user") && loginAuth.matches("1")) {
                    backPageEmp();
                } else if (role.equals("root") && loginAuth.matches("1")) {
                    backPageRoot();
                } else if (!loginAuth.matches("1") && loginRole.matches("root")) {
                    startActivity(new Intent(getActivity(), NotAcceptedUser.class));
                } else if (!loginAuth.matches("1") && loginRole.matches("admin")) {
                    startActivity(new Intent(getActivity(), NotAcceptedUser.class));
                } else if (!loginAuth.matches("1") && loginRole.matches("user")) {
                    startActivity(new Intent(getActivity(), NotAcceptedUser.class));
                }
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void backPageEmp() {
        startActivity(new Intent(getActivity(), EmployeeHomeActivity.class));
    }

    private void backPageAdmin() {
        startActivity(new Intent(getActivity(), AdminHomeActivity.class));
    }

    private void backPageRoot() {
        startActivity(new Intent(getActivity(), RootHomeActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Take Photo", "Choose from Library",
                        "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(EditProfileActivity.this);
                        if (items[item].equals("Take Photo")) {
                            if (result)
                                cameraIntent();
                        } else if (items[item].equals("Choose from Library")) {
                            if (result)
                                galleryIntent();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });


    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
      /*  try {
            Bitmap thumbnail = bm;
            Log.d(TAG,"camera capture image.."+data);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            assert thumbnail != null : "Image Could not be set!";
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;

            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            Log.d(TAG,"camera value..."+thumbnail);
            userImage.setImageBitmap(thumbnail);
            base64Profile = bitmapToBase64(thumbnail);
            value = data.getData();
            Log.d(TAG,"value..value.."+value);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Log.v(TAG,"data extracs "+data.getExtras().get("data"));
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert thumbnail != null:"Image Could not be set!";
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        Log.d(TAG,"thumbmail.."+thumbnail);
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            userImage.setImageBitmap(thumbnail);
            base64Profile = bitmapToBase64(thumbnail);
            Log.v(TAG,"data extracs1= "+data.getExtras().get("data"));
            value = getImageUri(getApplicationContext(), thumbnail);
            Log.d(TAG,"value == .."+value);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Get path from URI
   /* public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }*/
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                userImage.setImageBitmap(bm);
                base64Profile = bitmapToBase64(bm);
                value = data.getData();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveImage() {
        Log.d(TAG,"valueee save time..."+value);
        if (value != null) {
            Log.d(TAG, "value" + value);
            StorageReference filePath = mStorage.child(reArrangeEmail);
            filePath.putFile(value).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    profile = String.valueOf(downloadUrl);
                    Log.d(TAG,"profile...profile"+profile);
                    saveProfile();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } else if (value == null) {
            Log.d(TAG,"value is empty..."+value);
            saveProfile();
        }
    }

    private void saveProfile() {
        final UserDao userDao = new UserDao();
        user.setAuth(auth);
        user.setChatPin(chatPin);
        user.setTINorEIN(companyCin);
        user.setCompanyName(compCompany);
        user.setDesignation(compDesignation);
        user.setUserName(compEmail);
        user.setEmpId(compEmployee);
        user.setFirstName(compFirstName);
        user.setLastName(compLastName);
        user.setPassword(password);
        user.setProfilePjhoto(profile);
        user.setProviderNPIId(providerNPI);
        user.setProviderName(providerName);
        user.setPushNotificationId(notification);
        user.setRole(role);
        user.setSenderId(senderId);
        user.setStatus(status);
        user.setIsOnlie(isOnline);
        user.setCreateDate(createDate);
        user.setUpdateDate(updateDate);
        boolean data = userDao.createCompany(user);
        if (data) {
            progressDialog.dismiss();
            if(loginRole.matches("admin") && auth.matches("1")) {
                Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
                startActivity(intent);
            }else if(loginRole.matches("user") && auth.matches("1")) {
                Intent intent = new Intent(getActivity(), EmployeeHomeActivity.class);
                startActivity(intent);
            }else  if(loginRole.matches("root") && auth.matches("1")) {
                Intent intent = new Intent(getActivity(), RootHomeActivity.class);
                startActivity(intent);
            }else if(!auth.matches("1")){
                Intent intent = new Intent(getActivity(), NotAcceptedUser.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(getActivity(), "profile not successfuly updated!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        }
    }


    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public void onPause()
    {
        pref1 = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref1.getString("isOnline", "");
        if(isOnline.matches("true")) {
            fireBaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser logged = firebaseAuth.getCurrentUser();
            String reArrangeEmail = logged.getEmail().replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            dataReferences.removeValue();
        }
        super.onPause();
    }


    @Override
    protected  void onResume(){
        pref1 = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        isOnline =  pref1.getString("isOnline", "");
        if(isOnline.matches("true")) {
            HashMap<String, Object> onlineReenter = new HashMap<>();
            fireBaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser logged = firebaseAuth.getCurrentUser();
            String reArrangeEmail = logged.getEmail().replace(".", "-");
            FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
            onlineReenter.put("onlineUser", logged.getEmail());
            dataReferences.setValue(onlineReenter);
        }
        super.onResume();
    }

    public EditProfileActivity getActivity() {
        return this;
    }
}

