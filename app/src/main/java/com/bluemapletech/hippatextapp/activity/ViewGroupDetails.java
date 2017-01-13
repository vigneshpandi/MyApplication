package com.bluemapletech.hippatextapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.EmployeeGroupsAdapter;
import com.bluemapletech.hippatextapp.dao.EmployeeDao;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.Groups;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.R.attr.bitmap;
import static android.R.attr.value;
import static com.bluemapletech.hippatextapp.R.layout.view_groupimage_dialog;

public class ViewGroupDetails extends AppCompatActivity {
    private static final String TAG = ViewGroupDetails.class.getCanonicalName();
    private String groupName,loggedEmail, loggedINEmail,userMailId, reArrangeEmails, reArrangeEmailId,groupValues, editGroupName, profile, base64Profile;
    String reArrangeEmail,senderID;
    private FirebaseAuth firebaseAuth;  private FirebaseDatabase fireBaseDatabase;private StorageReference mStorage;
    private ListView iv;
    private String[] separated;
    private SecureRandom random;
    Groups group = new Groups(); Map<String, String> maps = new HashMap<String, String>();
    List<Groups> groupObj = new ArrayList<Groups>(); List<Groups> groupObjs = new ArrayList<Groups>();
    ImageView viewImage;ImageView backPageArrow; private ImageView displayImage;ImageView showImage;
    Uri value, downloadUrl;
    private Toolbar toolbar;private Toolbar toolbars;
    private int l = 0;  private int k = 0;
    private Bitmap bm;
    public int listPosition;
    Groups groupInformation;

    boolean adminAddedPermisson = false;
    SharedPreferences pref;SharedPreferences.Editor editor;FirebaseUser logged;
    final private int SELECT_FILE = 1;final private int REQUEST_CAMERA = 2;


    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
    String isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_details);
        iv = (ListView) findViewById(R.id.group_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar_header_menu);
        firebaseAuth = FirebaseAuth.getInstance();
        logged = firebaseAuth.getCurrentUser();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        viewImage = (ImageView) findViewById(R.id.view_group_image);
        ImageView groupNameEdit = (ImageView) findViewById(R.id.groupNameEdit);
        /*
          values comes  from GroupMessageEmployeeActivity
         */
        groupName = getIntent().getStringExtra(GroupMessageEmployeeActivity.groupNames);

        /*
          values comes from SelectUser
         */
        groupName = getIntent().getStringExtra(SelectUser.groupNames);
        if (groupName != null) {
            pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("groupNameValue", groupName);
            editor.commit();
        }
        if (groupName == null) {
            Log.d(TAG, "inside groupName is null");
            pref = getSharedPreferences("MyPref", MODE_PRIVATE);
            groupName = pref.getString("groupNameValue", "");
            loggedINEmail = pref.getString("loginMail", "");
        }

        TextView name = (TextView) findViewById(R.id.group_name);
        name.setText(groupName);

        fireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuth.getCurrentUser();
        loggedEmail = logged.getEmail().replace(".", "-");
        //get login user groupdetais
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("group").child(loggedEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    groupValues = snapshot.child("groupName").getValue(String.class);
                    String groupEmailId = snapshot.child("groupEmailId").getValue(String.class);
                    String randomName = snapshot.child("randomName").getValue(String.class);
                    String admin = snapshot.child("admin").getValue(String.class);

                    Log.d(TAG, "groupName" + groupName);
                    if (groupValues.matches(groupName)) {
                        group.setAdmin(admin);
                        group.setGroupEmailId(groupEmailId);
                        group.setGroupImage(snapshot.child("groupImage").getValue(String.class));
                        group.setRandomName(randomName);
                        group.setGroupName(groupValues);
                        Picasso.with(ViewGroupDetails.this).load(group.getGroupImage()).fit().centerCrop().into(viewImage);
                    }
                }
                separated = group.getGroupEmailId().split(";");
                for (int i = 0; i < separated.length; i++) {
                    //get userProfile for   user  profile Image
                    getUserProfile(separated[i]);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        groupNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewGroupDetails.this);
                alertDialog.setMessage("Enter your group name");
                final EditText input = new EditText(ViewGroupDetails.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editGroupName = input.getText().toString();
                                for (int m = 0; m < groupObj.size(); m++) {
                                    String us_mail = groupObj.get(m).getUserMail();
                                    reArrangeEmails = us_mail.replace(".", "-");
                                    String r_value = groupObj.get(m).getRandomName();
                                    DatabaseReference dataReferences = fireBaseDatabase.getReference().child("group").child(reArrangeEmails).child(r_value).child("groupName");
                                    dataReferences.setValue(editGroupName);
                                    TextView name = (TextView) findViewById(R.id.group_name);
                                    Log.d(TAG, "groupValioo" + editGroupName);
                                    name.setText(editGroupName);

                                }
                            }
                        });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        //admin make user changed to admin
        iv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                listPosition = position;
                if(adminAddedPermisson) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage("Make group admin");
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            EmployeeDao empDao = new EmployeeDao();
                            boolean success = empDao.empChangeAdmintoGroup(groupObj.get(listPosition).getRandomName(), groupObj.get(listPosition).getUserMail());
                            groupObj.remove(position);
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
        });

        viewImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ViewGroupDetails.this, android.R.style.Widget_ProgressBar_Small_Inverse);
                dialog.setContentView(R.layout.view_groupimage_dialog);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
                ImageView backPageArrow = (ImageView) dialog.findViewById(R.id.backarrow);
                ImageView takePhoto = (ImageView) dialog.findViewById(R.id.gallery_camera);
                displayImage = (ImageView) dialog.findViewById(R.id.view_group_img);
                ImageView saveProfileImage = (ImageView) dialog.findViewById(R.id.save);
                if (toolbar != null) {
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    dialog.show();
                }
                //dialog.addContentView();
                Log.d(TAG, "randomNameLogin" + group.getRandomName());
                //showImage = (ImageView) dialog.findViewById(R.id.view_group_img);
                Picasso.with(ViewGroupDetails.this).load(group.getGroupImage()).fit().centerCrop().into(displayImage);
                dialog.show();
                backPageArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        groupName = getIntent().getStringExtra(SelectUser.groupNames);
                        Intent intent = new Intent(getActivity(), ViewGroupDetails.class);
                        startActivity(intent);

                    }
                });
                takePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final CharSequence[] items = {"Take Photo", "Choose from Library",
                                "Cancel"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewGroupDetails.this);
                        builder.setTitle("Add Photo!");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                boolean result = Utility.checkPermission(ViewGroupDetails.this);
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
                saveProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG,"save group profile1");
                        Bitmap bitmap = ((BitmapDrawable) displayImage.getDrawable()).getBitmap();
                        Log.d(TAG,"save group profile2");
                        if (bitmap != null) {
                            try {
                                String root = Environment.getExternalStorageDirectory().toString();
                                File myDir = new File(root + "/HippaText");
                                if (!myDir.exists()) {
                                    myDir.mkdirs();
                                }
                                String ran_img_name  = new BigInteger(130, random).toString(32);
                                String randomValue = ran_img_name.substring(0, 7);
                                String name = randomValue+".jpg";
                                myDir = new File(myDir, name);
                                FileOutputStream out = new FileOutputStream(myDir);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                Toast.makeText(getActivity(), "saved to files!", Toast.LENGTH_LONG).show();
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                // some action
                            }
                        }



                    }
                });
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
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert thumbnail != null : "Image Could not be set!";
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        displayImage.setImageBitmap(thumbnail);
        base64Profile = bitmapToBase64(thumbnail);
        value = data.getData();

    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        displayImage.setImageBitmap(bm);
        base64Profile = bitmapToBase64(bm);
        value = data.getData();
        saveImage();
    }

    private void saveImage() {
        final EmployeeDao empDao = new EmployeeDao();
        random = new SecureRandom();
        senderID = new BigInteger(130, random).toString(32);
        String randomValue = senderID.substring(0, 7);
        Log.d("randomValue", randomValue);
        mStorage = FirebaseStorage.getInstance().getReference();
        Log.d(TAG, "R.drawable.groupimage..." + R.drawable.groupimage + group.getRandomName());
        StorageReference filePath = mStorage.child(groupName + senderID);
        Log.d(TAG, "value...value.." + value);
        filePath.putFile(value).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                Log.d(TAG, "downloadUrl " + downloadUrl);
                Log.d(TAG, "group.getGroupEmailId()....." + group.getGroupEmailId());
                String[] valueuserName = group.getGroupEmailId().split(";");
                for (int p = 0; p < valueuserName.length; p++) {
                    Log.d("valueuserName", valueuserName[p]);
                    reArrangeEmailId = valueuserName[p].replace(".", "-");
                    fireBaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmailId).child(group.getRandomName()).child("groupImage");
                    String valuesd = String.valueOf(downloadUrl);
                    dataReference.setValue(valuesd);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_group_user_menu, menu);
        menu.findItem(R.id.add_admin_group_menu).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_admin_group_menu) {
            Log.d(TAG, "groupDetails" + group);
            Intent intent = new Intent(getActivity(), SelectUser.class);
            Bundle b = new Bundle();
            b.putSerializable("groupDetails", group);
            intent.putExtras(b);
            startActivity(intent);
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                backPage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    // get user details
    private void getUserProfile(final String userMail) {
        groupInformation = new Groups();
        reArrangeEmails = userMail.replace(".", "-");
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmails);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                groupInformation.setUserImage(map.get("profilePhoto"));
                groupObjs.add(groupInformation);
                getGroupUser(userMail, group.getRandomName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void getGroupUser(String userMail, String randomValue) {
        userMailId = userMail;
        final Groups groupValue = new Groups();
        groupValue.setUserMail(userMail);

        groupValue.setUserImage(groupObjs.get(k).getUserImage());
        k++;
        reArrangeEmails = userMail.replace(".", "-");
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmails).child(randomValue);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                groupValue.setStatus(map.get("status"));
                groupValue.setRandomName(map.get("randomName"));
                groupValue.setGroupName(map.get("groupName"));
                groupValue.setGroupImage(map.get("groupImage"));
                groupValue.setGroupEmailId(map.get("groupEmailId"));
                groupValue.setAdmin(map.get("admin"));
                groupObj.add(groupValue);
                if (getActivity() != null) {
                    iv.setAdapter(new GroupUserAdapter(getActivity(), groupObj, logged.getEmail()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startActivity(new Intent(
                    getActivity(), GroupMessageEmployeeActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class GroupUserAdapter extends BaseAdapter {

        List<Groups> groupInfo = new ArrayList<Groups>();
        LayoutInflater inflater;
        String loginMailId;
        Context context;

        public GroupUserAdapter(Context context, List<Groups> group, String loginMail) {
            this.context = context;
            this.groupInfo = group;
            this.loginMailId = loginMail;
            inflater = LayoutInflater.from(this.context);
        }


        public int getCount() {
            return groupInfo.size();
        }

        @Override
        public Groups getItem(int position) {
            return groupInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            MyViewHolder mViewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.view_list_of_user_under_group, parent, false);
                mViewHolder = new GroupUserAdapter.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (GroupUserAdapter.MyViewHolder) convertView.getTag();
            }

            final Groups info = getItem(position);
            if (info.getStatus().matches("admin") && info.getUserMail().matches(loginMailId)) {
                toolbar.getMenu().findItem(R.id.add_admin_group_menu).setVisible(true);
                adminAddedPermisson = true;
                View btn = convertView.findViewById(R.id.btn_admin_view);
                btn.setVisibility(View.VISIBLE);
            } else if (info.getStatus().matches("user")) {
                View btn = convertView.findViewById(R.id.btn_admin_view);
                btn.setVisibility(View.GONE);
            }
            String[] valueuserName = info.getUserMail().split("@");
            mViewHolder.fieldName.setText(valueuserName[0]);
            mViewHolder.btnName.setText("admin");
            if (info.getStatus().matches("admin")) {
                View btn = convertView.findViewById(R.id.btn_admin_view);
                btn.setVisibility(View.VISIBLE);
            }
            if (info.getUserImage() != null) {
                //  Picasso.with(context).load(info.getUserImage()).fit().centerCrop().into(mViewHolder.userImage);
            }
            return convertView;
        }


        private class MyViewHolder {
            private TextView fieldName;
            private ImageView userImage;
            private Button btnName;

            public MyViewHolder(View item) {
                fieldName = (TextView) item.findViewById(R.id.user_email);
                btnName = (Button) item.findViewById(R.id.btn_admin_view);
                userImage = (ImageView) item.findViewById(R.id.user_image);

            }
        }
    }


    private void backPage() {
        startActivity(new Intent(
                getActivity(), GroupMessageEmployeeActivity.class));
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
        //Do whatever you want to do when the application stops.
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
    public ViewGroupDetails getActivity() {
        return this;
    }


}
