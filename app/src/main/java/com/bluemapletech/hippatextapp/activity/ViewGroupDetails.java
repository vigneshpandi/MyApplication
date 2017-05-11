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
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.PushNotification;
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

import static com.bluemapletech.hippatextapp.R.layout.item;

public class ViewGroupDetails extends AppCompatActivity {
    private static final String TAG = ViewGroupDetails.class.getCanonicalName();
    private String groupName,loggedEmail, loggedINEmail, reArrangeEmails, reArrangeEmailId,groupValues, editGroupName, profile, base64Profile;
    String reArrangeEmail,senderID;
    private FirebaseDatabase fireBaseDatabase;private StorageReference mStorage;
    private ListView iv;
    private String[] separated;
    private String[] separatedAdmin;
    private SecureRandom random;
    Groups group = new Groups();  HashMap<String,String> removeSameUser;
    List<Groups> groupObj = new ArrayList<Groups>();
    Map<String,String> groupStatus = new HashMap<String, String>();
    ImageView viewImage;ImageView backPageArrow;
    ImageView displayImage;ImageView showImage;
    Uri value, downloadUrl;
    private Toolbar toolbar;
    private int l = 0;  private int k = 0;
    boolean notAllowUser = true;
    private Bitmap bm;
    public int listPosition;
    Groups groupInformation;
    String pushNotificationId,randomValues;
    int adminCount = 0;
    boolean adminAddedPermisson = false;
    boolean adminPermission = false;
    SharedPreferences pref;SharedPreferences.Editor editor;
    //FirebaseUser logged;
    final private int SELECT_FILE = 1;final private int REQUEST_CAMERA = 2;
    String loginMail,login_role;
    SharedPreferences prefLogin;
    SharedPreferences.Editor editorLogin;
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
    String isOnline,userStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("asdas","asdasd"+"112223333");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        iv = (ListView) findViewById(R.id.group_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar_header_menu);
        TextView header = (TextView) findViewById(R.id.header);
        header.setText("");
        RelativeLayout exit=(RelativeLayout)findViewById(R.id.rel_lay_exit);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        viewImage = (ImageView) findViewById(R.id.view_group_image);
        ImageView groupNameEdit = (ImageView) findViewById(R.id.groupNameEdit);

        prefLogin = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        loginMail =   prefLogin.getString("loginMail","");
        login_role = prefLogin.getString("role","");
        isOnline =  prefLogin.getString("isOnline", "");

        /*
          values comes  from GroupMessageEmployeeActivity
         */
        groupName = getIntent().getStringExtra(GroupMessageEmployeeActivity.groupNames);
        randomValues = getIntent().getStringExtra(GroupMessageEmployeeActivity.randomValues);

        /*
          values comes from SelectUser
         */
        groupName = getIntent().getStringExtra(SelectUser.groupNames);

        /*if(!randomValues.matches("")){
            randomValues = getIntent().getStringExtra(SelectUser.randomValues);
        }
*/

        if (groupName != null) {
            pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("groupNameValue", groupName);
            editor.putString("randomValues",randomValues);
            editor.commit();
        }
        if (groupName == null || groupName.matches("")) {
            pref = getSharedPreferences("MyPref", MODE_PRIVATE);
            groupName = pref.getString("groupNameValue", "");
            randomValues  = pref.getString("randomValues", "");
            loggedINEmail = pref.getString("loginMail", "");
        }

        TextView name = (TextView) findViewById(R.id.group_name);
        name.setText(groupName);

        fireBaseDatabase = FirebaseDatabase.getInstance();
        loggedEmail = loginMail.replace(".", "-");
        //get login user groupdetais

        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("group").child(loggedEmail).child(randomValues);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"dataSnapshot"+dataSnapshot);
                adminPermission = false;
                Map<String, String> map = (Map) dataSnapshot.getValue();
                if(map!=null) {
                    String admin = map.get("admin");
                    String groupEmailId = map.get("groupEmailId");
                    String groupImage = map.get("groupImage");
                    groupValues = map.get("groupName");
                    String randomName = map.get("randomName");
                    String status = map.get("status");
                    if (groupName != null) {
                        Log.d(TAG, "call check 1");
                        if (groupValues.matches(groupName) && randomValues.matches(randomName)) {
                            Log.d(TAG, "call check 11");
                            group.setAdmin(admin);
                            group.setGroupEmailId(groupEmailId);
                            group.setGroupImage(groupImage);
                            group.setRandomName(randomName);
                            group.setGroupName(groupValues);
                            group.setStatus(status);
                            Picasso.with(ViewGroupDetails.this).load(group.getGroupImage()).fit().centerCrop().into(viewImage);
                            Log.d(TAG, "groupTostriiing" + group.toString());
                        }
                    }
                }else{
                    Log.d(TAG,"user group empty");
                    notAllowUser = false;
                }

              separated = group.getGroupEmailId().split(";");

                separatedAdmin = group.getAdmin().split(";");

                for (int i = 0; i < separated.length; i++) {
                    userStatus = "";
                    for(int l=0;l < separatedAdmin.length; l++){
                        if(separatedAdmin[l].matches(separated[i])){
                            userStatus = "admin";
                            if(separatedAdmin[l].matches(loginMail)){
                                adminPermission = true;
                            }

                        }
                    }

                    if(userStatus == null || userStatus.matches("")){
                        userStatus = "user";
                    }
                    groupStatus.put(separated[i],userStatus);
                    Log.d(TAG,"call check 2");
                    getUserProfile();
                    Log.d(TAG,"call check 3");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //group exit
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notAllowUser) {
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
                            String groupNotificationId = null;
                            Log.d(TAG, "exit from the group" + group.toString());
                            for(int g=0; g < groupObj.size(); g++){
                                if(groupNotificationId == null){
                                    groupNotificationId = groupObj.get(g).getPushNotificationId();
                                }else{
                                    groupNotificationId = groupNotificationId +";"+groupObj.get(g).getPushNotificationId();
                                }
                            }
                            Log.d(TAG,"groupNotificationId"+groupNotificationId);
                            final String finalGroupNotificationId = groupNotificationId;
                            exitGroup(group,finalGroupNotificationId,groupObj.get(listPosition).getPushNotificationId());
                        }
                    });
                    alertDialogs.show();
                }else{
                    showErrorMsg();
                }
            }
        });
        groupNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notAllowUser) {
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
                                    if (editGroupName.length() != 0) {
                                        for (int m = 0; m < groupObj.size(); m++) {
                                            String us_mail = groupObj.get(m).getUserMail();
                                            reArrangeEmails = us_mail.replace(".", "-");
                                            String r_value = group.getRandomName();
                                            Log.d("reArrangeEmails",reArrangeEmails);
                                            Log.d("groupName",groupName);
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
                                    } else {
                                        Log.d(TAG, "dialog close");
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
                }else{
                    showErrorMsg();
                }
            }
        });

        //admin make user changed to admin
        iv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String groupNotificationId = null;
                if(notAllowUser) {
                    Log.d(TAG, "clicking the details");
                    listPosition = position;
                    for(int g=0; g < groupObj.size(); g++){
                        if(groupNotificationId == null){
                            groupNotificationId = groupObj.get(g).getPushNotificationId();
                        }else{
                            groupNotificationId = groupNotificationId +";"+groupObj.get(g).getPushNotificationId();
                        }
                    }
                    if (adminAddedPermisson) {
                        if (groupObj.get(listPosition).getStatus().matches("admin") && !groupObj.get(listPosition).getUserMail().matches(loginMail)) {
                            final CharSequence[] items = {"View", "Remove",
                                    "Cancel"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewGroupDetails.this);
                            final String finalGroupNotificationId = groupNotificationId;
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    boolean result = Utility.checkPermission(ViewGroupDetails.this);
                                    if (items[item].equals("View")) {
                                        viewUserDetails();
                                    } else if (items[item].equals("Remove")) {
                                        Log.d(TAG,"groupObjPushNotificationId"+ finalGroupNotificationId);
                                        Log.d(TAG,"selectingPushNotificationId"+groupObj.get(listPosition).getPushNotificationId());
                                        removeGroup(group, groupObj.get(listPosition).getUserMail(),finalGroupNotificationId,groupObj.get(listPosition).getPushNotificationId());
                                    } else if (items[item].equals("Cancel")) {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builder.show();
                        } else if (groupObj.get(listPosition).getStatus().matches("user") && !groupObj.get(listPosition).getUserMail().matches(loginMail)) {
                            final CharSequence[] items = {"Make group admin", "View", "Remove",
                                    "Cancel"};
                            final String finalGroupNotificationId = groupNotificationId;
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewGroupDetails.this);
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    boolean result = Utility.checkPermission(ViewGroupDetails.this);
                                    if (items[item].equals("Make group admin")) {
                                        if (result)
                                            makeGroupAdmin();
                                    } else if (items[item].equals("View")) {
                                        viewUserDetails();
                                    } else if (items[item].equals("Remove")) {
                                        Log.d(TAG,"groupObjPushNotificationId"+ finalGroupNotificationId);
                                        Log.d(TAG,"selectingPushNotificationId"+groupObj.get(listPosition).getPushNotificationId());
                                        removeGroup(group, groupObj.get(listPosition).getUserMail(),finalGroupNotificationId,groupObj.get(listPosition).getPushNotificationId());
                                    } else if (items[item].equals("Cancel")) {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builder.show();
                        }


                    }else{
                        final CharSequence[] items = { "View",
                                "Cancel"};
                        final String finalGroupNotificationId = groupNotificationId;
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewGroupDetails.this);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                boolean result = Utility.checkPermission(ViewGroupDetails.this);
                                if (items[item].equals("View")) {
                                    viewUserDetails();
                                }  else if (items[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                    }
                }else{
                    showErrorMsg();
                }
            }
        });

      // view group image
        viewImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(notAllowUser) {
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
                    if(group.getGroupImage()!=null && !group.getGroupImage().matches("")){
                        dialog.findViewById(R.id.loadingPanel).setVisibility(View.GONE);//hide the loading progress bar
                        Picasso.with(ViewGroupDetails.this).load(group.getGroupImage()).fit().centerCrop().into(displayImage);
                    }

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
                            if(displayImage.getDrawable()!=null){
                                Bitmap bitmap = ((BitmapDrawable) displayImage.getDrawable()).getBitmap();
                                if (bitmap != null) {
                                    try {
                                        String root = Environment.getExternalStorageDirectory().toString();
                                        File myDir = new File(root + "/HippaText");
                                        if (!myDir.exists()) {
                                            myDir.mkdirs();
                                        }
                                        random = new SecureRandom();
                                        String ran_img_name = new BigInteger(130, random).toString(32);
                                        String randomValue = ran_img_name.substring(0, 7);
                                        String name = randomValue + ".jpg";

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
                            }else{
                                Log.d(TAG,"please wait");
                                Toast.makeText(getActivity(), "please wait!", Toast.LENGTH_LONG).show();
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

                }else{
                    showErrorMsg();
                }

            }

        });





    }

    public void makeGroupAdmin(){
        EmployeeDao empDao = new EmployeeDao();
       // boolean success = empDao.empChangeAdmintoGroup(groupObj.get(listPosition).getRandomName(), groupObj.get(listPosition).getUserMail());
        // groupObj.remove(listPosition);
        fireBaseDatabase = FirebaseDatabase.getInstance();
        String[] groupUserMail = group.getAdmin().split(";");
        String[] groupUsers = group.getGroupEmailId().split(";");
        String newAdmin = groupObj.get(listPosition).getUserMail();
        for (int s = 0; s < groupUserMail.length; s++) {
            newAdmin = newAdmin +";"+groupUserMail[s];
        }
            for(int g = 0; g < groupUsers.length; g++) {
                reArrangeEmailId = groupUsers[g].replace(".", "-");
                fireBaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmailId).child(group.getRandomName()).child("admin");
                dataReference.setValue(newAdmin);
            }
    }
    public void removeGroup(Groups group,String userMail,String groupPushNotificationId,String selectUserPushNotificationId){
        adminCount = 0 ;
        Log.d(TAG,"groupDetailsuser"+group.toString());
        Log.d(TAG,"userMail11"+userMail);
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

        String newadmin = null;
        String[] groupAdmins = group.getAdmin().split(";");
        Log.d(TAG," group.getAdmin()"+ group.getAdmin());
        for (int s = 0; s < groupAdmins.length; s++) {
            if(!userMail.matches(groupAdmins[s])) {
                if(newadmin == null){
                    newadmin =  groupAdmins[s];
                } else {
                    newadmin =  newadmin +";"+groupAdmins[s];
                }
            }
        }
            Log.d(TAG,"groupAdmin"+group.getAdmin());
        for(int g = 0; g < groupUsers.length; g++) {
            reArrangeEmailId = groupUsers[g].replace(".", "-");
            fireBaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmailId).child(group.getRandomName()).child("groupEmailId");
            dataReference.setValue(newGroup);
            DatabaseReference dataReferences = fireBaseDatabase.getReference().child("group").child(reArrangeEmailId).child(group.getRandomName()).child("admin");
            dataReferences.setValue(newadmin);
        }
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        reArrangeEmail =  userMail.replace(".","-");
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("group").child(reArrangeEmail).child(group.getRandomName());
        dataReferences.removeValue();
        groupStatus.clear();
        Log.d(TAG,"after remove group user details"+group.toString());
        Log.d(TAG,"remove userMail..."+userMail);

        String[] seprateNotificationId = groupPushNotificationId.split(";");
        for(int j=0;j< seprateNotificationId.length; j++){
            Log.d("value","groupNotificstion"+seprateNotificationId[j]);
            if(!selectUserPushNotificationId.matches(seprateNotificationId[j])){
                try {
                    PushNotification runners = new PushNotification();
                    runners.execute("TCTText",loginMail+" "+"removed"+" "+userMail,seprateNotificationId[j]);

                } catch (Exception ex) {
                    Log.d("error","Exception error...");
                }
            }

        }

        try {
            PushNotification runners = new PushNotification();
            runners.execute("TCTText",loginMail+" removed you",selectUserPushNotificationId);

        } catch (Exception ex) {
            Log.d("error","Exception error...");
        }
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
        if(notAllowUser) {
            if (id == R.id.add_admin_group_menu) {
                Intent intent = new Intent(getActivity(), SelectUser.class);
                Bundle b = new Bundle();
                Log.d(TAG,"group group"+group);
                b.putSerializable("groupDetails", group);
                intent.putExtras(b);
                startActivity(intent);
            }
        }else{
            showErrorMsg();
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                backPage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    // get user details
    private void getUserProfile() {
        Log.d(TAG,"call check 4");
        DatabaseReference dataReference = fireBaseDatabase.getReference().child("userDetails");
        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupObj = new ArrayList<Groups>();
                Log.d(TAG,"call check 5");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userEmailId = snapshot.child("emailAddress").getValue(String.class);
                    if(groupStatus.get(userEmailId)!= null){
                        groupInformation = new Groups();
                        groupInformation.setUserMail(snapshot.child("emailAddress").getValue(String.class));
                        groupInformation.setUserImage(snapshot.child("profilePhoto").getValue(String.class));
                        groupInformation.setPushNotificationId(snapshot.child("pushNotificationId").getValue(String.class));
                        groupInformation.setStatus(groupStatus.get(groupInformation.getUserMail()));
                        groupObj.add(groupInformation);
                    }
                }
                if (getActivity() != null) {
                    Log.d(TAG,"call check 6"+groupObj.toString());
                    iv.setAdapter(new GroupUserAdapter(getActivity(), groupObj, loginMail,adminPermission));
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
            if(notAllowUser){
                startActivity(new Intent(
                        getActivity(), GroupMessageEmployeeActivity.class));
                return true;
            } else{
                showErrorMsg();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private class GroupUserAdapter extends BaseAdapter {

        List<Groups> groupInfo = new ArrayList<Groups>();
        LayoutInflater inflater;
        String loginMailId;
        Context context;
        boolean admin_premiddion;
        public GroupUserAdapter(Context context, List<Groups> group, String loginMail,boolean adminPermission) {
            this.context = context;
            this.groupInfo = group;
            this.loginMailId = loginMail;
            this.admin_premiddion = adminPermission;
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
           /* if (info.getStatus().matches("admin") && info.getUserMail().matches(loginMailId)) {
                toolbar.getMenu().findItem(R.id.add_admin_group_menu).setVisible(true);
                adminAddedPermisson = true;
            }*/
            if (admin_premiddion) {
                toolbar.getMenu().findItem(R.id.add_admin_group_menu).setVisible(true);
                adminAddedPermisson = true;
            }
            if (info.getStatus().matches("user")) {
                View btn = convertView.findViewById(R.id.btn_admin_view);
                btn.setVisibility(View.INVISIBLE);
            }
            String[] valueuserName = info.getUserMail().split("@");
            mViewHolder.fieldName.setText(valueuserName[0]);
            mViewHolder.btnName.setText("Admin");
            if (info.getStatus().matches("admin")) {
                View btn = convertView.findViewById(R.id.btn_admin_view);
                btn.setVisibility(View.VISIBLE);
            }
            if (info.getUserImage() != null) {
                Picasso.with(context).load(info.getUserImage()).fit().centerCrop().into(mViewHolder.userImage);
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
        if(notAllowUser) {
            startActivity(new Intent(
                    getActivity(), GroupMessageEmployeeActivity.class));
        }else{
            showErrorMsg();
        }
    }


    public void showErrorMsg(){
        Log.d(TAG,"showError");
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Error");
        alert.setMessage("You can't send message to this group because you're no longer a participant.");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //dialog.cancel();
                if(login_role.matches("admin")){
                    Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
                    startActivity(intent);
                } else if(login_role.matches("user")){
                    Intent intent = new Intent(getActivity(), EmployeeHomeActivity.class);
                    startActivity(intent);
                }else if(login_role.matches("root")){
                    Intent intent = new Intent(getActivity(), RootHomeActivity.class);
                    startActivity(intent);
                }

            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
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
        //Do whatever you want to do when the application stops.
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

    public void exitGroup(Groups group,String groupPushNotificationId,String selectUserPushNotificationId){
        Log.d(TAG,"adminCountUserExit"+adminCount);
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

            }
        }


        String newadmin = null;
        int p =0;
        String[] groupAdmins = group.getAdmin().split(";");
        for (int z = 0; z < groupAdmins.length; z++) {
            if(!loginMail.matches(groupAdmins[z])) {
                if(newadmin == null){
                    p++;
                    newadmin =  groupAdmins[z];
                } else {
                    newadmin =  newadmin +";"+groupAdmins[z];
                }
            }

        }

        if(p == 0) {
            if (groupUsers[0].matches(loginMail)) {
                newadmin = groupUsers[1];
            } else {
                newadmin = groupUsers[0];
            }

        }
        String[] seprateNotificationId = groupPushNotificationId.split(";");
        for(int j=0;j< seprateNotificationId.length; j++){
            Log.d("value","groupNotificstion"+seprateNotificationId[j]);
            try {
                PushNotification runners = new PushNotification();
                runners.execute("TCTText",loginMail+" left ",seprateNotificationId[j]);

            } catch (Exception ex) {
                Log.d("error", "Exception error...");
            }

        }

        int moreThanUser = 0;
        for(int z = 0; z < groupUsers.length; z++){
            if(!loginMail.matches(groupUsers[z])) {
                moreThanUser++;
                reArrangeEmailId = groupUsers[z].replace(".", "-");
                fireBaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmailId).child(group.getRandomName()).child("groupEmailId");
                dataReference.setValue(newGroup);
                DatabaseReference dataReferences = fireBaseDatabase.getReference().child("group").child(reArrangeEmailId).child(group.getRandomName()).child("admin");
                dataReferences.setValue(newadmin);
            }
        }

// dout need or not
        if(adminCount==1 && moreThanUser!=0) {
            String nextAccessAdmin = null;
            if (groupUsers[0].matches(loginMail)) {
                nextAccessAdmin = groupUsers[1];
            } else {
                nextAccessAdmin = groupUsers[0];
            }
            if(nextAccessAdmin !=null){
                reArrangeEmailId = nextAccessAdmin.replace(".", "-");
                fireBaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference dataReference = fireBaseDatabase.getReference().child("group").child(reArrangeEmailId).child(group.getRandomName()).child("status");
                dataReference.setValue("admin");
            }
        }






        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        reArrangeEmail =  loginMail.replace(".","-");
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("group").child(reArrangeEmail).child(group.getRandomName());
        dataReferences.removeValue();
        Log.d(TAG,"login value"+loginMail);

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
