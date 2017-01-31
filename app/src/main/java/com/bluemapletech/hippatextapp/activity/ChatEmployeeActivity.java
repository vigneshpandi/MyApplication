package com.bluemapletech.hippatextapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bluemapletech.hippatextapp.R;
import com.bluemapletech.hippatextapp.adapter.PageEmployeeBaseAdpter;
import com.bluemapletech.hippatextapp.dao.UserDao;
import com.bluemapletech.hippatextapp.model.Message;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class ChatEmployeeActivity extends AppCompatActivity implements View.OnClickListener,UserDao.MessagesCallbacks {
    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
    private ListView mListView;
    private UserDao.MessagesListener mListener;
    private String childappendid,loginRole,loginAuth,chatOnline,userStaus,userName,userRole,toMail;
    private String fromMail, senderId, userFirstName, userLastName,mConvoId,notificationId;
    private ImageView selectImage;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase fireBaseDatabase;
    final private int SELECT_FILE = 1;
    final private int REQUEST_CAMERA = 2;
    private String base64Profile,isOnline;
    private static final String TAG = ChatEmployeeActivity.class.getCanonicalName();
    Message message;

    private int delPosition;
    private Toolbar toolbar;
    private  LinearLayout layout;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    SharedPreferences.Editor editors;
    boolean wallpaperimage = false;
    private String roleValue,logn_senderId;
    EditText newMessageView;
    String newMessage = null;
    HashMap<String,String> onlineHash = new HashMap<String,String>();
    ImageView imageView;
    private String loginMail; // loginDetail string declare
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caht_employee);
        newMessageView = (EditText)findViewById(R.id.new_message);
        toMail = getIntent().getStringExtra(PageEmployeeBaseAdpter.toEmail);
        fromMail = getIntent().getStringExtra(PageEmployeeBaseAdpter.fromEmail);
        senderId = getIntent().getStringExtra(PageEmployeeBaseAdpter.sendId);
        notificationId = getIntent().getStringExtra(PageEmployeeBaseAdpter.notificationId);
        userFirstName = getIntent().getStringExtra(PageEmployeeBaseAdpter.firstName);
        userLastName = getIntent().getStringExtra(PageEmployeeBaseAdpter.lastName);
        userRole = getIntent().getStringExtra(PageEmployeeBaseAdpter.role);
        Log.d(TAG,"Root Role...."+userRole);
        mListView = (ListView)findViewById(R.id.message_list);
        selectImage = (ImageView) findViewById(R.id.select_image);
        layout = (LinearLayout) findViewById(R.id.activity_caht_employee);
        mMessages = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessages);
        mListView.setAdapter(mAdapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        userName = userFirstName +" "+ userLastName;
        Log.d(TAG,"userLastName..."+userName);
        if(userFirstName.matches("") && userLastName.matches("")){
            Log.d(TAG,"inside empty");
            String[] valueuserName = toMail.split("[@._-]");
            for (int j = 0; j <= valueuserName.length - 1; j++)
            {
                Log.d("valueuserName","valueuserName"+valueuserName[j]);
                userName = valueuserName[j];
                break;
            }
        }
        //login userDetails
        pref = getSharedPreferences("loginUserDetails", Context.MODE_PRIVATE);
        roleValue =  pref.getString("role", "");
        loginRole = pref.getString("role","");
        loginAuth = pref.getString("auth","");
        logn_senderId = pref.getString("senderId","");
        isOnline =  pref.getString("isOnline", "");
        loginMail =  pref.getString("loginMail", "");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            String[] chatName = toMail.split("@");
            getSupportActionBar().setTitle(chatName[0]);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView header = (TextView) findViewById(R.id.header);
            header.setText(chatName[0]);
        }
        ImageView sendMessage = (ImageView) findViewById(R.id.send_message);

        sendMessage.setOnClickListener(this);
        String toMails = toMail.replace(".", "-");
        String fromMails = fromMail.replace(".", "-");
        String[] ids = {toMails,"+", fromMails};
        Arrays.sort(ids);
        mConvoId = ids[1]+ids[0]+ids[2];
        mListener = UserDao.addMessagesListener(mConvoId,logn_senderId, this);




        if(isOnline.matches("true")) {
            checkOnlineUser();
        }


       /* background image for chatting */
        prefs = getSharedPreferences("myBackgroundImage", Context.MODE_PRIVATE);
        String backgroundImageValue =  prefs.getString("backgroundImage", "");
        if(backgroundImageValue!=null){
            Log.d(TAG,"backgroundImageValueStringToBitMap"+backgroundImageValue);
            StringToBitMap(backgroundImageValue);
        }


    }
    public void onClick(View v) {
        saveMessages();
    }

    @Override
    public void onMessageAdded(Message message) {
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserDao.stop(mListener);
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
    protected  void onStart(){
        super.onStart();
        if(isOnline.matches("true")) {
            checkOnlineUser();
        }
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallpaperimage = false;
                chooseImage();
            }
        });
    }





    private class MessagesAdapter extends ArrayAdapter<Message> {
        MessagesAdapter(ArrayList<Message> messages){
            super(ChatEmployeeActivity.this, R.layout.item, R.id.msg, messages);
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            message = getItem(position);
            childappendid = message.getChildappendid();
            TextView nameView = (TextView)convertView.findViewById(R.id.msg);
            nameView.setText(message.getMtext());
            TextView userFirstAndLastName = (TextView) convertView.findViewById(R.id.user_name);
            TextView dateTime = (TextView) convertView.findViewById(R.id.date_time);
            imageView = (ImageView) convertView.findViewById(R.id.image);
            if(message.getImage()!=null && !message.getImage().matches("")) {
                String images = message.getImage();
                byte[] decodedString = Base64.decode(images, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 250, 250, false));
            }
           /* ViewTreeObserver vto = imageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                  float  finalHeight = imageView.getMeasuredHeight();
                  float  finalWidth = imageView.getMeasuredWidth();
                    System.out.println("height: " + finalHeight + " Width: " + finalWidth);
                    return true;
                }
            });*/
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams)dateTime.getLayoutParams();
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams)userFirstAndLastName.getLayoutParams();
            int sdk = Build.VERSION.SDK_INT;
            //format of given date
            String myFormat = "yyyy-MM-dd HH:mm:ss z";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);


            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c.getTime());
            System.out.println("today's date = "+ formattedDate);

            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DATE, -1);
            String yest_date = df.format(c1.getTime());
            System.out.println("Yesterday's date = "+ yest_date);





            String msg_date = null;
            Date date = null;
            Date date1 = null;
            String val = null;
            String d1 = null;
            String d2 = null;
            try {
                // convert for date format showing
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
                Log.d(TAG,"today"+"today");
                msg_date = "today";

            }else if(yest_date.matches(d2)){
                Log.d(TAG,"yesterday"+"yesterday");
                msg_date = "yesterday";
            }else{
                msg_date = val;
            }



            if (message.getMsender().equals(fromMail)){
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
                }  else if(message.getImage()!=null && !message.getImage().matches("")){
                    imageView.setVisibility(View.VISIBLE);
                    nameView.setVisibility(View.GONE);
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        imageView.setBackground(getActivity().getResources().getDrawable(R.drawable.bubble2));
                        Log.d(TAG,"inside...111");

                        dateTime.setText(msg_date);
                        layoutParams.gravity = Gravity.RIGHT;
                        layoutParams1.gravity = Gravity.RIGHT;
                    } else {
                        imageView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.bubble2));
                        Log.d(TAG,"inside...1111");

                        dateTime.setText(msg_date);
                        layoutParams.gravity = Gravity.RIGHT;
                        layoutParams1.gravity = Gravity.RIGHT;
                    }
                }
            }else if(!message.getMsender().equals(fromMail)){
                userFirstAndLastName.setVisibility(View.VISIBLE);
                if(message.getMtext()!=null && !message.getMtext().matches("")){
                    nameView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nameView.setBackground(getResources().getDrawable(R.drawable.bubble1));
                        Log.d(TAG,"inside...2");
                        userFirstAndLastName.setText(userName);
                        dateTime.setText(msg_date);
                        layoutParams.gravity = Gravity.LEFT;
                        layoutParams1.gravity = Gravity.LEFT;
                        layoutParams2.gravity = Gravity.LEFT;
                    } else{
                        nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble1));
                        Log.d(TAG,"inside...22");
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
                        Log.d(TAG,"inside...222");
                        userFirstAndLastName.setText(userName);
                        dateTime.setText(msg_date);
                        layoutParams.gravity = Gravity.LEFT;
                        layoutParams1.gravity = Gravity.LEFT;
                        layoutParams2.gravity = Gravity.LEFT;
                    }else {
                        imageView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.bubble1));
                        Log.d(TAG,"inside...2222");
                        userFirstAndLastName.setText(userName);
                        dateTime.setText(msg_date);
                        layoutParams.gravity = Gravity.LEFT;
                        layoutParams1.gravity = Gravity.LEFT;
                        layoutParams2.gravity = Gravity.LEFT;
                    }
                }
            }
            imageView.setLayoutParams(layoutParams);
            nameView.setLayoutParams(layoutParams);
            convertView.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog=new Dialog(ChatEmployeeActivity.this,android.R.style.Theme_Black_NoTitleBar);
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

                    message = getItem(position);
                    delPosition = position;
                    toolbar.getMenu().findItem(R.id.delete).setVisible(true);
                    return true;

                }
            });
            final View finalConvertView = convertView;
            convertView.findViewById(R.id.msg).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                  // finalConvertView.setSelected(true);
                    message = getItem(position);
                    delPosition = position;
                    toolbar.getMenu().findItem(R.id.delete).setVisible(true);
                    return false;
                }
            });
            return convertView;
        }


    }


    public void chooseImage (){
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatEmployeeActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(ChatEmployeeActivity.this);
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
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        base64Profile = bitmapToBase64(thumbnail);
        if(wallpaperimage!=true){
            saveMessages();
        }else if(wallpaperimage==true){
            BitmapDrawable myBackground = new BitmapDrawable(thumbnail);
            Log.d(TAG,"myBackground"+myBackground);
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


        if(wallpaperimage!=true){
            saveMessages();
        }else if(wallpaperimage==true){
            BitmapDrawable myBackground = new BitmapDrawable(bm);
            Log.d(TAG,"myBackground"+myBackground);
            layout.setBackgroundDrawable(myBackground);
            base64Profile = "";
        }
        //imageView.setImageBitmap(bm);
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

   /* public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
*/
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String val = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        Log.d(TAG,"wallpaperimage"+wallpaperimage);
        if(wallpaperimage==true){
            editors = prefs.edit();
            editors.putString("backgroundImage", val);
            editors.apply();
        }
        return val;
    }
    public void saveMessages (){
        newMessage = newMessageView.getText().toString();
        newMessageView.setText("");
            Message msg = new Message();
            msg.setMtext(newMessage);
            msg.setMsender(fromMail);
            msg.setToChatEmail(toMail);
            msg.setSenderId(senderId);
            msg.setPushNotificationId(notificationId);
            if (base64Profile != null && newMessage != null) {
                msg.setImage(base64Profile);
                newMessage = "";
            } else {
                msg.setImage("");
            }
        if(!msg.getMtext().matches("") || !msg.getImage().matches("")){
            UserDao.saveMessage(msg, mConvoId);
        }

            base64Profile = "";

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
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG,"roleValue......"+roleValue);
                if(roleValue.matches("user")&&loginAuth.matches("1")){
                    Log.d(TAG,"userRole... user..."+userRole);
                    backPage();
                }else
                if(roleValue.matches("root")&&loginAuth.matches("1")){
                    Log.d(TAG,"userRole....root.."+userRole);
                    backPageRoot();
                }else
                if(roleValue.matches("admin")&&loginAuth.matches("1")){
                    Log.d(TAG,"userRole....admin.."+userRole);
                    backPageAdmin();
                }else
                if(!loginAuth.matches("1")&& loginRole.matches("root")){
                    startActivity(new Intent(getActivity(),ListOfRoots.class));
                }else if(!loginAuth.matches("1")&& loginRole.matches("admin")){
                    startActivity(new Intent(getActivity(),ListOfRoots.class));
                }else if(!loginAuth.matches("1")&& loginRole.matches("user")){
                    startActivity(new Intent(getActivity(),ListOfRoots.class));
                }



                return true;
        }
        if (id == R.id.delete) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("Delete message?");
            alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    message.setLoginSenderId(logn_senderId);
                    UserDao.deleteChatMessage(message, mConvoId);
                    toolbar.getMenu().findItem(R.id.delete).setVisible(false);
                    mMessages.remove(delPosition);
                    mAdapter = new MessagesAdapter(mMessages);
                    mListView.setAdapter(mAdapter);
                   /* mMessages = new ArrayList<>();
                    mAdapter = new MessagesAdapter(mMessages);
                    mListView.setAdapter(mAdapter);*/
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
            wallpaperimage = true;
            chooseImage();
        }
        if(id == R.id.no_wallpaper){
            noWallpaper();
        }

        return super.onOptionsItemSelected(item);
    }

    private void backPage() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),EmployeeHomeActivity.class));
    }
    private void backPageRoot() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),RootHomeActivity.class));
    }
    private void backPageAdmin() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
    }
    public ChatEmployeeActivity getActivity() {
        return this;
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try{
            Log.d(TAG,"encodedString"+encodedString);
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

    public void noWallpaper(){
        layout.setBackgroundResource(0);
        SharedPreferences preferencess = getSharedPreferences("myBackgroundImage", 0);
        SharedPreferences.Editor editors = preferencess.edit();
        editors.clear();
        editors.commit();
    }
    public void checkOnlineUser(){
        String reArrangeEmail = toMail.replace(".", "-");
        fireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("onlineUser").child(reArrangeEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onlineHash = new HashMap<String, String>();
                String  onlineUser = (String) dataSnapshot.child("onlineUser").getValue();
                onlineHash.put(onlineUser,onlineUser);
                checkStatus();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }


    public void checkStatus(){
        String checkOnline = onlineHash.get(toMail);
        if (checkOnline!=null) {
            userStaus = "online";
            getSupportActionBar().setSubtitle(userStaus);
        } else {
            userStaus = "";
            checklastUpdate();
        }

    }

    public void checklastUpdate(){
        String reArrangeEmail = toMail.replace(".", "-");
        fireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataReferences = fireBaseDatabase.getReference().child("userDetails").child(reArrangeEmail);
        dataReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String  lastUpdateDate = (String) dataSnapshot.child("updatedDate").getValue();
//format of given date
                String myFormat = "yyyy-MM-dd HH:mm:ss z";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat, Locale.US);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());
                System.out.println("today's date = "+ formattedDate);

                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1);
                String yest_date = df.format(c1.getTime());
                System.out.println("Yesterday's date = "+ yest_date);


                Date now = new Date();




                Date date = null;
                String val = null;
                String d1 = null;
                String d2 = null; String d3 = null;

                try {
                    // convert for date format showing
                    date = sdf.parse(lastUpdateDate);
                    sdf = new SimpleDateFormat("hh:mm a");
                    d1 = sdf.format(date);
                    sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    sdf1 = new SimpleDateFormat("MMM dd,yyyy");
                    d2 = sdf.format(date);
                    d3 = sdf1.format(date);
                    val = d2+d1;
                    Log.d(TAG,"message.getDateAndTime = "+ val);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(formattedDate.matches(d2)){
                    String lastSeen = "Today at "+d1;
                    userStaus = lastSeen;
                }else if(yest_date.matches(d2)){
                    String lastSeen = "last seen Yesterday at "+d1;
                    userStaus = lastSeen;
                }else{
                    String lastSeen = "last seen "+d3;
                    userStaus = lastSeen;
                }

               /* userStaus = lastUpdateDate;*/
                Log.d("it is last updated date", "success"+lastUpdateDate);
                getSupportActionBar().setSubtitle(userStaus);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }



}