package com.bluemapletech.hippatextapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.dao.EmployeeDao;
import com.bluemapletech.hippatextapp.model.Groups;
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
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ImageView viewImage;ImageView backPageArrow;
    ImageView displayImage;ImageView showImage;
    Uri value, downloadUrl;
    private Toolbar toolbar;private Toolbar toolbars;
    private int l = 0;  private int k = 0;
    private Bitmap bm;
    public int listPosition;
    Groups groupInformation;

    boolean adminAddedPermisson = false;
    SharedPreferences pref;SharedPreferences.Editor editor;FirebaseUser logged;
    final private int SELECT_FILE = 1;final private int REQUEST_CAMERA = 2;

    String loginMail,login_role;
    SharedPreferences prefLogin;
    SharedPreferences.Editor editorLogin;
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
    String isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        iv = (ListView) findViewById(R.id.group_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar_header_menu);
        TextView header = (TextView) findViewById(R.id.header);
        header.setText("");
        firebaseAuth = FirebaseAuth.getInstance();
        logged = firebaseAuth.getCurrentUser();
        RelativeLayout exit=(RelativeLayout)findViewById(R.id.rel_lay_exit);
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
        prefLogin = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginMail =   prefLogin.getString("loginMail","");
        login_role = prefLogin.getString("role","");

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
        if (groupName == null || groupName.matches("")) {
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
                    if(groupName != null){
                        if (groupValues.matches(groupName)) {
                            group.setAdmin(admin);
                            group.setGroupEmailId(groupEmailId);
                            group.setGroupImage(snapshot.child("groupImage").getValue(String.class));
                            group.setRandomName(randomName);
                            group.setGroupName(groupValues);
                            Picasso.with(ViewGroupDetails.this).load(group.getGroupImage()).fit().centerCrop().into(viewImage);
                        }}
                }
                Log.d(TAG,"groupName change imaage"+groupName);
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
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogs = new AlertDialog.Builder(ViewGroupDetails.this);
                alertDialogs.setMessage("Exit group?");
                alertDialogs.setPositiveButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialogs.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,"exit from the group"+group.toString());
                     exitGroup(group);
                    }
                });
                alertDialogs.show();
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
                                if(editGroupName.length() !=0){
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
                                //apply for the edit group name
                                pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                editor = pref.edit();
                                editor.putString("groupNameValue", editGroupName);
                                editor.commit();
                            }else{
                                   Log.d(TAG,"dialog close");
                                    dialog.cancel();
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
                Log.d(TAG,"clicking the details");
                listPosition = position;
                if(adminAddedPermisson) {
                    if(groupObj.get(listPosition).getStatus().matches("admin") && !groupObj.get(listPosition).getUserMail().matches(loginMail)){
                        final CharSequence[] items = { "View", "Remove",
                                "Cancel" };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewGroupDetails.this);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                boolean result= Utility.checkPermission(ViewGroupDetails.this);
                                if (items[item].equals("View")) {
                                    viewUserDetails();
                                } else if (items[item].equals("Remove")) {
                                    removeGroup(group,groupObj.get(listPosition).getUserMail());
                                } else if (items[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                    }else if(groupObj.get(listPosition).getStatus().matches("user") && !groupObj.get(listPosition).getUserMail().matches(loginMail)){
                        final CharSequence[] items = { "Make group admin", "View","Remove",
                                "Cancel" };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewGroupDetails.this);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                boolean result= Utility.checkPermission(ViewGroupDetails.this);
                                if (items[item].equals("Make group admin")) {
                                    if(result)
                                        makeGroupAdmin();
                                } else if (items[item].equals("View")) {
                                   // viewUserDetails();
                                } else if (items[item].equals("Remove")) {
                                   // removeGroup(group,groupObj.get(listPosition).getUserMail());
                                }else if (items[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                    }


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
                    TextView header = (TextView) findViewById(R.id.header);
                    header.setText("");
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
                        Bitmap bitmap = ((BitmapDrawable) displayImage.getDrawable()).getBitmap();
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

                // dialog box back key button
                dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            groupName = getIntent().getStringExtra(SelectUser.groupNames);
                            Intent intent = new Intent(getActivity(), ViewGroupDetails.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });



            }

        });
    }

    public void makeGroupAdmin(){
        EmployeeDao empDao = new EmployeeDao();
        boolean success = empDao.empChangeAdmintoGroup(groupObj.get(listPosition).getRandomName(), groupObj.get(listPosition).getUserMail());
        groupObj.remove(listPosition);
    }
    public void removeGroup(Groups group,String userMail){
        Log.d(TAG,"groupDetails"+group.toString());
        Log.d(TAG,"userMail"+userMail);
        fireBaseDatabase = FirebaseDatabase.getInstance();
        String[] groupUsers = group.getGroupEmailId().split(";");
        String newGroup = null;
        for (int s = 0; s < groupUsers.length; s++) {
            if(!userMail.matches(groupUsers[s])) {
                if(newGroup == null){
                    newGroup =  groupUsers[s];
                } else {
                    newGroup =  newGroup +";"+groupUsers[s];
                }
            }
        }

        for(int g = 0; g < groupUsers.length; g++) {
            reArrangeEmailId = groupUsers[g].replace(".", "-");
            fireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmailId).child(group.getRandomName()).child("groupEmailId");
            dataReference.setValue(newGroup);
        }

        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        reArrangeEmail =  userMail.replace(".","-");
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("group").child(reArrangeEmail).child(group.getRandomName());
        dataReferences.removeValue();
    }

    public void  viewUserDetails(){
        Log.d(TAG,"viewuser detail.."+groupObj.get(listPosition).getUserMail());
        String value = "back";
        Intent userDetails = new Intent(getActivity(),ViewUserDetailTabActivity.class);
        userDetails.putExtra("userEmail",groupObj.get(listPosition).getUserMail());
        userDetails.putExtra("return",value);
        startActivity(userDetails);

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
        assert thumbnail != null:"Image Could not be set!";
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            displayImage.setImageBitmap(thumbnail);
            base64Profile = bitmapToBase64(thumbnail);
            value = getImageUri(getApplicationContext(), thumbnail);
            saveImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
        displayImage.setImageBitmap(getResizedBitmap(bm,500));
        base64Profile = bitmapToBase64(getResizedBitmap(bm,500));
        value = data.getData();
        saveImage();
    }

    private void saveImage() {
        final EmployeeDao empDao = new EmployeeDao();
        random = new SecureRandom();
        senderID = new BigInteger(130, random).toString(32);
        String randomValue = senderID.substring(0, 7);
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child(groupName + senderID);
        filePath.putFile(value).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                String[] valueuserName = group.getGroupEmailId().split(";");
                for (int p = 0; p < valueuserName.length; p++) {
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
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
        groupObj = new ArrayList<Groups>();
        final Groups groupValue = new Groups();
        groupValue.setUserMail(userMail);
        groupValue.setUserImage(groupObjs.get(k).getUserImage());
        k++;
        reArrangeEmails = userMail.replace(".", "-");
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmails).child(randomValue);
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"value has been"+dataSnapshot);
                Map<String, String> map = (Map) dataSnapshot.getValue();
                if(map!=null){
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
            mViewHolder.btnName.setText("Admin");
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
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void exitGroup(Groups group){
        fireBaseDatabase = FirebaseDatabase.getInstance();
        String reArrangeEmail = loginMail.replace(".", "-");
        String[] groupUsers = group.getGroupEmailId().split(";");
        String newGroup = null;
        for (int s = 0; s < groupUsers.length; s++) {
            if(!loginMail.matches(groupUsers[s])) {
                if(newGroup == null){
                    newGroup =  groupUsers[s];
                } else {
                    newGroup =  newGroup +";"+groupUsers[s];
                }
                reArrangeEmailId = groupUsers[s].replace(".", "-");
                fireBaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmailId).child(group.getRandomName()).child("groupEmailId");
                dataReference.setValue(newGroup);
            }
        }
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        reArrangeEmail =  loginMail.replace(".","-");
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("group").child(reArrangeEmail).child(group.getRandomName());
        dataReferences.removeValue();
        if(login_role.matches("root")){
            Intent redirect = new Intent(getActivity(), RootHomeActivity.class);
            startActivity(redirect);
        }else if(login_role.matches("admin")){
            Intent redirect = new Intent(getActivity(), AdminHomeActivity.class); startActivity(redirect);
        }else if (login_role.matches("user")){
            Intent redirect = new Intent(getActivity(), EmployeeHomeActivity.class); startActivity(redirect);
        }


    }

    public ViewGroupDetails getActivity() {
        return this;
    }

}
