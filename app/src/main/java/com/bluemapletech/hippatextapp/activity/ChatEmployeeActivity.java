package com.bluemapletech.hippatextapp.activity;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class ChatEmployeeActivity extends AppCompatActivity implements View.OnClickListener,UserDao.MessagesCallbacks {
    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
    private ListView mListView;
    private String mConvoId;
    private UserDao.MessagesListener mListener;
    private String toMail;
    private String fromMail, senderId, userFirstName, userLastName;
    private String notificationId;
    private ImageView selectImage;
    final private int SELECT_FILE = 1;
    final private int REQUEST_CAMERA = 2;
    private String base64Profile;
    private static final String TAG = ChatEmployeeActivity.class.getCanonicalName();
    Message message;
    private String childappendid;
    private String userName;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caht_employee);
        toMail = getIntent().getStringExtra(PageEmployeeBaseAdpter.toEmail);
        fromMail = getIntent().getStringExtra(PageEmployeeBaseAdpter.fromEmail);
        senderId = getIntent().getStringExtra(PageEmployeeBaseAdpter.sendId);
        notificationId = getIntent().getStringExtra(PageEmployeeBaseAdpter.notificationId);
        userFirstName = getIntent().getStringExtra(PageEmployeeBaseAdpter.firstName);
        userLastName = getIntent().getStringExtra(PageEmployeeBaseAdpter.lastName);
        mListView = (ListView)findViewById(R.id.message_list);
        selectImage = (ImageView) findViewById(R.id.select_image);
        mMessages = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessages);
        mListView.setAdapter(mAdapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar_header);
        userName = userFirstName +" "+ userLastName;
        Log.d(TAG,"userLastName..."+userName);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(fromMail);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ImageView sendMessage = (ImageView) findViewById(R.id.send_message);

        sendMessage.setOnClickListener(this);
        String toMails = toMail.replace(".", "-");
        String fromMails = fromMail.replace(".", "-");
        String[] ids = {toMails,"+", fromMails};
        Arrays.sort(ids);
        mConvoId = ids[1]+ids[0]+ids[2];
        mListener = UserDao.addMessagesListener(mConvoId, this);


        //toolbar clicking
       /* toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
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
    private class MessagesAdapter extends ArrayAdapter<Message> {
        MessagesAdapter(ArrayList<Message> messages){
            super(ChatEmployeeActivity.this, R.layout.item, R.id.msg, messages);
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            message = getItem(position);
            childappendid = message.getChildappendid();
            Log.d(TAG,"childappendid..."+childappendid);
            TextView nameView = (TextView)convertView.findViewById(R.id.msg);
            nameView.setText(message.getMtext());
            TextView userFirstAndLastName = (TextView) convertView.findViewById(R.id.user_name);
            TextView dateTime = (TextView) convertView.findViewById(R.id.date_time);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            if(message.getImage()!=null && !message.getImage().matches("")) {
                String images = message.getImage();
                byte[] decodedString = Base64.decode(images, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams)dateTime.getLayoutParams();
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams)userFirstAndLastName.getLayoutParams();
            int sdk = Build.VERSION.SDK_INT;
            if (message.getMsender().equals(fromMail)){
                if(message.getMtext()!=null && !message.getMtext().matches("")){
                    imageView.setVisibility(View.GONE);
                    nameView.setVisibility(View.VISIBLE);
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nameView.setBackground(getResources().getDrawable(R.drawable.bubble2));
                        Log.d(TAG,"inside...1");
                       // userFirstAndLastName.setText(userName);
                        dateTime.setText(message.getDateAndTime("dateandtime"));
                        layoutParams.gravity = Gravity.RIGHT;
                        layoutParams1.gravity = Gravity.RIGHT;
                       // layoutParams2.gravity = Gravity.RIGHT;
                    } else{
                        nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble2));
                        Log.d(TAG,"inside...11");
                      //  userFirstAndLastName.setText(userName);
                        dateTime.setText(message.getDateAndTime("dateandtime"));
                        layoutParams.gravity = Gravity.RIGHT;
                        layoutParams1.gravity = Gravity.RIGHT;
                       // layoutParams2.gravity = Gravity.RIGHT;
                    }
                }  else if(message.getImage()!=null && !message.getImage().matches("")){
                    imageView.setVisibility(View.VISIBLE);
                    nameView.setVisibility(View.GONE);
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        imageView.setBackground(getActivity().getResources().getDrawable(R.drawable.bubble2));
                        Log.d(TAG,"inside...111");
                       // userFirstAndLastName.setText(userName);
                        dateTime.setText(message.getDateAndTime("dateandtime"));
                        layoutParams.gravity = Gravity.RIGHT;
                        layoutParams1.gravity = Gravity.RIGHT;
                        //layoutParams2.gravity = Gravity.RIGHT;
                    } else {
                        imageView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.bubble2));
                        Log.d(TAG,"inside...1111");
                       // userFirstAndLastName.setText(userName);
                        dateTime.setText(message.getDateAndTime("dateandtime"));
                        layoutParams.gravity = Gravity.RIGHT;
                        layoutParams1.gravity = Gravity.RIGHT;
                        //layoutParams2.gravity = Gravity.RIGHT;
                    }
                }
            }else if(!message.getMsender().equals(fromMail)){
                if(message.getMtext()!=null && !message.getMtext().matches("")){
                    nameView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nameView.setBackground(getResources().getDrawable(R.drawable.bubble1));
                        Log.d(TAG,"inside...2");
                        userFirstAndLastName.setText(userName);
                        dateTime.setText(message.getDateAndTime("dateandtime"));
                        layoutParams.gravity = Gravity.LEFT;
                        layoutParams1.gravity = Gravity.LEFT;
                        layoutParams2.gravity = Gravity.LEFT;
                    } else{
                        nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble1));
                        Log.d(TAG,"inside...22");
                        userFirstAndLastName.setText(userName);
                        dateTime.setText(message.getDateAndTime("dateandtime"));
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
                        dateTime.setText(message.getDateAndTime("dateandtime"));
                        layoutParams.gravity = Gravity.LEFT;
                        layoutParams1.gravity = Gravity.LEFT;
                        layoutParams2.gravity = Gravity.LEFT;
                    }else {
                        imageView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.bubble1));
                        Log.d(TAG,"inside...2222");
                        userFirstAndLastName.setText(userName);
                        dateTime.setText(message.getDateAndTime("dateandtime"));
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
                    Log.d(TAG,"lonngpress"+"longPress");
                    toolbar.getMenu().findItem(R.id.delete).setVisible(true);
                    return false;

                }
            });
            convertView.findViewById(R.id.msg).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    message = getItem(position);
                    Log.d(TAG,"lonngpress....1"+"longPress.....1");
                    toolbar.getMenu().findItem(R.id.delete).setVisible(true);
                    return false;

                }
            });
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
        msg.setToChatEmail(toMail);
        msg.setSenderId(senderId);
        msg.setPushNotificationId(notificationId);
        if(base64Profile!=null) {
            msg.setImage(base64Profile);
        }else{
            msg.setImage("");
        }
        UserDao.saveMessage(msg, mConvoId);
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
                backPage();
                return true;
        }
        if(id == R.id.delete){
            Log.d(TAG,"mConvoId...."+mConvoId);
            UserDao.deleteChatMessage(message,mConvoId);
            toolbar.getMenu().findItem(R.id.delete).setVisible(false);
            startActivity(getIntent());
           /* mMessages.clear();
            mListener = UserDao.addMessagesListener(mConvoId, this);
            mAdapter.notifyDataSetChanged();*/
        }

        return super.onOptionsItemSelected(item);
    }

    private void backPage() {
        Log.d(TAG,"back page..");
        startActivity(new Intent(getActivity(),EmployeeHomeActivity.class));
    }
    public ChatEmployeeActivity getActivity() {
        return this;
    }
}
