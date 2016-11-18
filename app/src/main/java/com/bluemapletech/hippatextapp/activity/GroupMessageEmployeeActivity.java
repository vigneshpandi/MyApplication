package com.bluemapletech.hippatextapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class GroupMessageEmployeeActivity extends AppCompatActivity implements View.OnClickListener,GroupMessageDao.MessagesCallbacks {
    private ArrayList<Message> mMessages;
        private GroupMessageEmployeeActivity.MessagesAdapter mAdapter;
        private ListView mListView;
        private String mConvoId;
        private GroupMessageDao.MessagesListener mListener;
        private String randomValue;
        private String fromMail, senderId;
        private ImageView selectImage;
        private String notificationId;
        final private int SELECT_FILE = 1;
        final private int REQUEST_CAMERA = 2;
        private String base64Profile;
        private  String reArrangeEmail;
        private String role;
        private FirebaseAuth firebaseAuthRef;
        private FirebaseDatabase firebaseDatabaseRef;
        private static final String TAG = GroupMessageEmployeeActivity.class.getCanonicalName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_group_message_employee);

            fromMail = getIntent().getStringExtra(EmployeeGroupsAdapter.fromMail);
            senderId = getIntent().getStringExtra(EmployeeGroupsAdapter.senderId);
            randomValue = getIntent().getStringExtra(EmployeeGroupsAdapter.randomValue);
            notificationId = getIntent().getStringExtra(EmployeeGroupsAdapter.notificationId);
            mListView = (ListView)findViewById(R.id.message_list);
            selectImage = (ImageView) findViewById(R.id.select_image);
            mMessages = new ArrayList<>();
            mAdapter = new GroupMessageEmployeeActivity.MessagesAdapter(mMessages);
            mListView.setAdapter(mAdapter);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_header);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            ImageView sendMessage = (ImageView)findViewById(R.id.send_message);

            sendMessage.setOnClickListener(this);
            String fromMails = fromMail.replace(".", "-");
            String[] ids = {fromMails};
            Arrays.sort(ids);
            mConvoId = ids[0];
            Log.d("mConvoId",mConvoId);
            mListener = GroupMessageDao.addMessagesListener(randomValue, this);
            init();
        }

    private void init() {
        FirebaseDatabase mfireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuthRef = FirebaseAuth.getInstance();
        FirebaseUser logged = firebaseAuthRef.getCurrentUser();
        reArrangeEmail = logged.getEmail().replace(".", "-");
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
            GroupMessageDao.stop(mListener);
        }
        private class MessagesAdapter extends ArrayAdapter<Message> {
            MessagesAdapter(ArrayList<Message> messages){
                super(GroupMessageEmployeeActivity.this, R.layout.item, R.id.msg, messages);
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = super.getView(position, convertView, parent);
                Message message = getItem(position);
                TextView nameView = (TextView)convertView.findViewById(R.id.msg);
                nameView.setText(message.getMtext());
                ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
                if(message.getImage()!=null && !message.getImage().matches("")) {
                    String images = message.getImage();
                    byte[] decodedString = Base64.decode(images, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                }
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();
                int sdk = Build.VERSION.SDK_INT;
                if (message.getSenderId().equals(senderId)){
                    if(message.getMtext()!=null && !message.getMtext().matches("")){
                        imageView.setVisibility(View.GONE);
                        nameView.setVisibility(View.VISIBLE);
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nameView.setBackground(getResources().getDrawable(R.drawable.bubble2));
                        layoutParams.gravity = Gravity.RIGHT;
                    } else{
                        nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble2));
                        layoutParams.gravity = Gravity.RIGHT;
                    }
                }else if(message.getImage()!=null && !message.getImage().matches("")){
                        imageView.setVisibility(View.VISIBLE);
                        nameView.setVisibility(View.GONE);
                        if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            imageView.setBackground(getActivity().getResources().getDrawable(R.drawable.bubble2));
                            layoutParams.gravity = Gravity.RIGHT;
                        }else {
                            imageView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.bubble2));
                            layoutParams.gravity = Gravity.RIGHT;
                        }
                    }
                }else if(!message.getMsender().equals(fromMail)){
                    if(message.getMtext()!=null && !message.getMtext().matches("")){
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nameView.setBackground(getResources().getDrawable(R.drawable.bubble1));
                        layoutParams.gravity = Gravity.LEFT;
                    } else{
                        nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble1));
                        layoutParams.gravity = Gravity.LEFT;
                    }
                }else if(message.getImage()!=null && !message.getImage().matches("")){
                        nameView.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            imageView.setBackground(getActivity().getResources().getDrawable(R.drawable.bubble1));
                            layoutParams.gravity = Gravity.LEFT;
                        }else {
                            imageView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.bubble1));
                            layoutParams.gravity = Gravity.LEFT;
                        }
                    }}
                imageView.setLayoutParams(layoutParams);
                nameView.setLayoutParams(layoutParams);
                return convertView;
            }
    }


    // show the popup for capture the image
    @Override
    protected  void onStart(){
        super.onStart();
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });


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
        saveMessages();
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
        base64Profile = bitmapToBase64(bm);
        saveMessages();
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public void saveMessages (){
        EditText newMessageView = (EditText)findViewById(R.id.new_message);
        String newMessage = newMessageView.getText().toString();
        newMessageView.setText("");
        Message msg = new Message();
        msg.setMtext(newMessage);
        msg.setMsender(fromMail);
        msg.setSenderId(senderId);
        msg.setRandomValue(randomValue);
        msg.setPushNotificationId(notificationId);
        if(base64Profile!=null) {
            msg.setImage(base64Profile);
        }else{
            msg.setImage("");
        }
        GroupMessageDao.saveMessage(msg, randomValue);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        return super.onOptionsItemSelected(item);
    }

    private void backPageEmp() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),EmployeeHomeActivity.class));
    }

    private void backPageAdmin() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),AdminHomeActivity.class));
    }

    public GroupMessageEmployeeActivity getActivity() {
        return this;
    }
}
