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
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.EmployeeGroupsAdapter;
import com.bluemapletech.hippatextapp.adapter.PageEmployeeBaseAdpter;
import com.bluemapletech.hippatextapp.dao.GroupMessageDao;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.Groups;
import com.bluemapletech.hippatextapp.model.Message;
import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.acl.Group;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GroupMessageEmployeeActivity extends AppCompatActivity implements View.OnClickListener,GroupMessageDao.MessagesCallbacks {
    private ArrayList<Message> mMessages;
        private GroupMessageEmployeeActivity.MessagesAdapter mAdapter;
        private ListView mListView;
        private GroupMessageDao.MessagesListener mListener;
        private String fromMail, senderId, userName,randomValue,mConvoId,isOnline,childappendid;
        Message message;
        String newMessage;
        private ImageView selectImage;
        private String notificationId;
        final private int SELECT_FILE = 1;
        final private int REQUEST_CAMERA = 2;
        private String base64Profile;
        private  String reArrangeEmail;
        private String role,login_role,logn_senderId;
        private FirebaseAuth firebaseAuthRef;
        private String groupName,loginRoleValue;
    private int delPosition;
    private  LinearLayout layout;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SharedPreferences prefss;
    SharedPreferences.Editor editorss;
    SharedPreferences prefs;
    SharedPreferences.Editor editors;
    boolean notAllowUser = true;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    boolean wallpaperImage = false;
    private Toolbar toolbar;
    private FirebaseDatabase firebaseDatabaseRef;
    private TextView newMessages,rm_gr_user;
    private ImageView selectImages,sendMessage;
    private String loginMail; // loginDetail string declare
    public static final String groupNames = "groupNames";
    public static final String randomValues = "randomValues";
        private static final String TAG = GroupMessageEmployeeActivity.class.getCanonicalName();
    SharedPreferences pref1;
    SharedPreferences.Editor editor1;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_group_message_employee);

            sendMessage = (ImageView)findViewById(R.id.send_message);
            /*selectImages = (ImageView) findViewById(R.id.select_image);
            newMessages = (TextView) findViewById(R.id.new_message);
            rm_gr_user = (TextView) findViewById(R.id.rm_gr_user);
            rm_gr_user.setVisibility(View.GONE);*/

            //login user details
            prefss = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
            login_role =  prefss.getString("role", "");
            logn_senderId = prefss.getString("senderId","");
            isOnline =  prefss.getString("isOnline", "");
            loginMail =  prefss.getString("loginMail", "");

            layout = (LinearLayout) findViewById(R.id.activity_group_chat_employee);
            fromMail = getIntent().getStringExtra(EmployeeGroupsAdapter.fromMail);
            senderId = getIntent().getStringExtra(EmployeeGroupsAdapter.senderId);
            randomValue = getIntent().getStringExtra(EmployeeGroupsAdapter.randomValue);
            notificationId = getIntent().getStringExtra(EmployeeGroupsAdapter.notificationId);
            pref = getApplicationContext().getSharedPreferences("groupMessageDetail", MODE_PRIVATE);
    if(fromMail!=null || senderId!=null || randomValue!=null || notificationId!=null ) {
        editor = pref.edit();
        editor.putString("fromMail", fromMail);
        editor.putString("senderId", senderId);
        editor.putString("randomValue", randomValue);
        editor.putString("notificationId", notificationId);
        editor.commit();
    } else{
        fromMail =  pref.getString("fromMail", "");
        senderId =  pref.getString("senderId", "");
        randomValue =  pref.getString("randomValue", "");
        notificationId =  pref.getString("notificationId", "");
        loginRoleValue = pref.getString("role","");
    }
            mListView = (ListView)findViewById(R.id.message_list);
            selectImage = (ImageView) findViewById(R.id.select_image);
            mMessages = new ArrayList<>();
            mAdapter = new GroupMessageEmployeeActivity.MessagesAdapter(mMessages);
            mListView.setAdapter(mAdapter);
            toolbar = (Toolbar) findViewById(R.id.toolbar_header);
                setSupportActionBar(toolbar);

            sendMessage.setOnClickListener(this);
            String fromMails = fromMail.replace(".", "-");
            String[] ids = {fromMails};
            Arrays.sort(ids);
            mConvoId = ids[0];
            mListener = GroupMessageDao.addMessagesListener(randomValue,logn_senderId, this);
            init();

        }

    private void init() {
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuthRef = FirebaseAuth.getInstance();
       // FirebaseUser logged = firebaseAuthRef.getCurrentUser();
        reArrangeEmail = loginMail.replace(".", "-");
        DatabaseReference dataReference = mfireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                role = map.get("role");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        DatabaseReference dataReferences = mfireBaseDatabase.getReference().child("group").child(reArrangeEmail).child(randomValue);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                if(map != null){
                    groupName = map.get("groupName");
                    getSupportActionBar().setTitle(groupName);
                    TextView header = (TextView) findViewById(R.id.header);
                    header.setText(groupName);
                }else{
                    Log.d(TAG,"remove user within the group");
                    notAllowUser = false;
                    /*sendMessage.setVisibility(View.GONE);
                    selectImages.setVisibility(View.GONE);
                    newMessages.setVisibility(View.GONE);
                    rm_gr_user.setVisibility(View.VISIBLE);*/
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notAllowUser){
                    Intent intent = new Intent(getActivity(), ViewGroupDetails.class);
                    intent.putExtra(groupNames,groupName);
                    intent.putExtra(randomValues,randomValue);
                    startActivity(intent);
                }else{
                    showErrorMsg();
                }

            }
        });

          /* background image for chatting */
        prefs = getSharedPreferences("myBackgroundImage", Context.MODE_PRIVATE);
        String backgroundImageValue =  prefs.getString("backgroundImage", "");
        if(backgroundImageValue!=null){
            StringToBitMap(backgroundImageValue);
        }
    }

    public void onClick(View v) {
        if(notAllowUser){
            saveMessages();
        }else{
            showErrorMsg();
        }

        }

        @Override
        public void onMessageAdded(Message message) {
            mMessages.add(message);
            mAdapter.notifyDataSetChanged();
        }
        @Override
        protected void onDestroy() {
            super.onDestroy();
            GroupMessageDao.stop(mListener);
        }
        private class MessagesAdapter extends ArrayAdapter<Message> {
            MessagesAdapter(ArrayList<Message> messages){
                super(GroupMessageEmployeeActivity.this, R.layout.item, R.id.msg, messages);
            }
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                convertView = super.getView(position, convertView, parent);
                 message = getItem(position);
                childappendid = message.getChildappendid();
                String[] valueuserName = message.getMsender().split("@");
                userName = valueuserName[0];
                TextView nameView = (TextView)convertView.findViewById(R.id.msg);
                TextView userFirstAndLastName = (TextView) convertView.findViewById(R.id.user_name);
                nameView.setText(message.getMtext());
                TextView dateTime = (TextView) convertView.findViewById(R.id.date_time);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
               /* if(message.getImage()!=null && !message.getImage().matches("")) {
                    String images = message.getImage();
                    byte[] decodedString = Base64.decode(images, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                }*/
                if(message.getImage()!=null && !message.getImage().matches("")) {
                    String images = message.getImage();
                    byte[] decodedString = Base64.decode(images, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 250, 250, false));
                }
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();
                LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams)dateTime.getLayoutParams();
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams)userFirstAndLastName.getLayoutParams();
                String myFormat = "yyyy-MM-dd HH:mm:ss z";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1);
                String yest_date = df.format(c1.getTime());

                String msg_date = null;
                Date date = null;
                Date date1 = null;
                String val = null;
                String d1 = null;
                String d2 = null;
                try {
                    date = sdf.parse(message.getDateAndTime("dateandtime"));
                    sdf = new SimpleDateFormat("hh:mm a");
                   d1 = sdf.format(date);
                    sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    d2 = sdf.format(date);
                    val = d2+d1;
                    Log.d(TAG,"message.getDateAndTime = "+ val);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(formattedDate.matches(d2)){
                    msg_date = "Today";

                }else if(yest_date.matches(d2)){
                    msg_date = "Yesterday";
                }else{
                    msg_date = val;
                }
                 int sdk = Build.VERSION.SDK_INT;
                if (message.getSenderId().equals(senderId)){
                    userFirstAndLastName.setVisibility(View.GONE);
                    if(message.getMtext()!=null && !message.getMtext().matches("")){
                        imageView.setVisibility(View.GONE);
                        nameView.setVisibility(View.VISIBLE);
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nameView.setBackground(getResources().getDrawable(R.drawable.bubble2));
                        dateTime.setText(msg_date);
                        layoutParams.gravity = Gravity.RIGHT;
                        layoutParams1.gravity = Gravity.RIGHT;
                    } else{
                        nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble2));
                        dateTime.setText(msg_date);
                        layoutParams.gravity = Gravity.RIGHT;
                        layoutParams1.gravity = Gravity.RIGHT;
                    }
                }else if(message.getImage()!=null && !message.getImage().matches("")){
                        imageView.setVisibility(View.VISIBLE);
                        nameView.setVisibility(View.GONE);
                        if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            imageView.setBackground(getActivity().getResources().getDrawable(R.drawable.bubble2));
                            dateTime.setText(msg_date);
                            layoutParams.gravity = Gravity.RIGHT;
                            layoutParams1.gravity = Gravity.RIGHT;
                        }else {
                            imageView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.bubble2));
                            dateTime.setText(msg_date);
                            layoutParams.gravity = Gravity.RIGHT;
                            layoutParams1.gravity = Gravity.RIGHT;
                        }
                    }
                }else if(!message.getMsender().equals(fromMail)){
                    userFirstAndLastName.setVisibility(View.VISIBLE);
                    if(message.getMtext()!=null && !message.getMtext().matches("")){
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nameView.setBackground(getResources().getDrawable(R.drawable.bubble1));
                        userFirstAndLastName.setText(userName);
                        dateTime.setText(msg_date);
                        layoutParams.gravity = Gravity.LEFT;
                        layoutParams1.gravity = Gravity.LEFT;
                        layoutParams2.gravity = Gravity.LEFT;
                    } else{
                        nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble1));
                        userFirstAndLastName.setText(userName);
                        dateTime.setText(msg_date);
                        layoutParams.gravity = Gravity.LEFT;
                        layoutParams1.gravity = Gravity.LEFT;
                        layoutParams2.gravity = Gravity.LEFT;
                    }
                }else if(message.getImage()!=null && !message.getImage().matches("")){
                        nameView.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            imageView.setBackground(getActivity().getResources().getDrawable(R.drawable.bubble1));
                            userFirstAndLastName.setText(userName);
                            dateTime.setText(msg_date);
                            layoutParams.gravity = Gravity.LEFT;
                            layoutParams1.gravity = Gravity.LEFT;
                            layoutParams2.gravity = Gravity.LEFT;
                        }else {
                            imageView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.bubble1));
                            userFirstAndLastName.setText(userName);
                            dateTime.setText(msg_date);
                            layoutParams.gravity = Gravity.LEFT;
                            layoutParams1.gravity = Gravity.LEFT;
                            layoutParams2.gravity = Gravity.LEFT;
                        }
                    }}
                imageView.setLayoutParams(layoutParams);
                nameView.setLayoutParams(layoutParams);
                convertView.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolbar.getMenu().findItem(R.id.delete).setVisible(false);
                        Dialog dialog=new Dialog(GroupMessageEmployeeActivity.this,android.R.style.Theme_Black_NoTitleBar);
                        dialog.setContentView(R.layout.view_image_dialog);
                        String images = getItem(position).getImage();
                        ImageView showImage = (ImageView) dialog.findViewById(R.id.view_image);
                        byte[] decodedString = Base64.decode(images, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        showImage.setImageBitmap(decodedByte);
                        dialog.show();
                    }
                });
                convertView.findViewById(R.id.image).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(notAllowUser){
                            message = getItem(position);
                            delPosition = position;
                            toolbar.getMenu().findItem(R.id.delete).setVisible(true);
                        }else{
                            showErrorMsg();
                        }
                        return true;

                    }
                });
                convertView.findViewById(R.id.msg).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(notAllowUser){
                            message = getItem(position);
                            delPosition = position;
                            toolbar.getMenu().findItem(R.id.delete).setVisible(true);
                        }else{
                            showErrorMsg();
                        }
                        return false;

                    }
                });

                return convertView;
            }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        return true;
    }
    @Override
    protected  void onStart(){
        super.onStart();
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notAllowUser){
                    wallpaperImage = false;
                    chooseImage();
                }else{
                    showErrorMsg();
                }
            }
        });

    }
    public void chooseImage (){
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupMessageEmployeeActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(GroupMessageEmployeeActivity.this);
                if (items[item].equals("Take Photo")) {
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        base64Profile = bitmapToBase64(thumbnail);
        if(wallpaperImage!=true){
            saveMessages();
        }else if(wallpaperImage==true){
            BitmapDrawable myBackground = new BitmapDrawable(thumbnail);
            layout.setBackgroundDrawable(myBackground);
            editors = prefs.edit();
            editors.putString("backgroundImage", base64Profile);
            base64Profile = "";
            editors.apply();
        }
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
        base64Profile = bitmapToBase64(getResizedBitmap(bm,500));
        if(wallpaperImage!=true){
            saveMessages();
        }else if(wallpaperImage==true){
            BitmapDrawable myBackground = new BitmapDrawable(bm);
            layout.setBackgroundDrawable(myBackground);
           /* editors = prefs.edit();
            editors.putString("backgroundImage", base64Profile);
             editors.apply();*/
            base64Profile = "";

        }
    }


    public void noWallpaper(){
        layout.setBackgroundResource(0);
        SharedPreferences preferencess = getSharedPreferences("myBackgroundImage", 0);
        SharedPreferences.Editor editors = preferencess.edit();
        editors.clear();
        editors.commit();
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String val = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        if(wallpaperImage==true){
            editors = prefs.edit();
            editors.putString("backgroundImage", val);
            editors.apply();
        }
        return val;
    }

    public void saveMessages (){
        EditText newMessageView = (EditText)findViewById(R.id.new_message);
        newMessage = newMessageView.getText().toString();
        newMessageView.setText("");
        Message msg = new Message();
        msg.setMtext(newMessage);
        msg.setMsender(fromMail);
        msg.setSenderId(senderId);
        msg.setRandomValue(randomValue);
        msg.setPushNotificationId(notificationId);
        if(base64Profile!=null) {
            msg.setImage(base64Profile);
            newMessage = "";
        }else{
            msg.setImage("");
        }
        if(!msg.getMtext().matches("") || !msg.getImage().matches("")){
            GroupMessageDao.saveMessage(msg, randomValue);
        }
        base64Profile ="";
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_chat, menu);
        menu.findItem(R.id.delete).setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(role.equals("admin")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageAdmin();
                    return true;
            }
        }
        if(role.equals("user")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    backPageEmp();
                    return true;
            }
        }
        if(id == R.id.delete){
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("Delete message?");
            alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    message.setLoginSenderId(logn_senderId);
            UserDao.deleteGroupChatMessage(message,randomValue);
            toolbar.getMenu().findItem(R.id.delete).setVisible(false);
                    mMessages.remove(delPosition);
                    mAdapter = new MessagesAdapter(mMessages);
                    mListView.setAdapter(mAdapter);
            //startActivity(getIntent());
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
        if(id == R.id.chat_image_background){
            wallpaperImage = true;
            chooseImage();
        }

        if(id == R.id.no_wallpaper){
            noWallpaper();
        }
        return super.onOptionsItemSelected(item);
    }

    private void backPageEmp() {
        startActivity(new Intent(getActivity(),EmployeeHomeActivity.class));
    }

    private void backPageAdmin() {
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
    }
    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmapValue=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            BitmapDrawable myBackground = new BitmapDrawable(bitmapValue);
            layout.setBackgroundDrawable(myBackground);
            return bitmapValue;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(login_role.matches("root")){
                startActivity(new Intent(getActivity(), RootHomeActivity.class));
            }
            if(login_role.matches("admin")){
                startActivity(new Intent(getActivity(), AdminHomeActivity.class));
            }
            if(login_role.matches("user")){
                startActivity(new Intent(getActivity(), EmployeeHomeActivity.class));
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
    public GroupMessageEmployeeActivity getActivity() {
        return this;
    }
}
