package com.example.myfirstwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Activity_During_Sending_Image_Activity extends AppCompatActivity {

    private PhotoView compleImageView;
    private EditText writeAbtImageET;
    private FloatingActionButton sendImageBtn;
    private Toolbar mToolbar;
    private String imageUriff  , senderUserId , receiverUserId , uniKey  , currentUserId , downloadUrl;
    private StorageReference imagesStorageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String saveDate , saveTime  ,checkFile  , randomName;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__during__sending__image_);

        initialize();



       sendImageBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               savingInformationToDatabase();
               mDialog.show();
           }
       });
    }



    private void initialize() {

        mDialog = new ProgressDialog(this);
        mDialog.setTitle("sending..");
        mDialog.setMessage("sending pix..");
        mDialog.setCanceledOnTouchOutside(false);

        compleImageView =(PhotoView) findViewById(R.id.id_during_send_message_full_imageView);
        writeAbtImageET = (EditText)findViewById(R.id.id_during_send_message_text);
        sendImageBtn = (FloatingActionButton)findViewById(R.id.id_during_send_message_button);
        mToolbar = (Toolbar)findViewById(R.id.toolbar_during_sending_image);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        imageUriff = getIntent().getStringExtra("imageUri").toString().trim();
        System.out.println("image uri received :"+imageUriff);
        senderUserId = getIntent().getStringExtra("senderUserId");
        receiverUserId = getIntent().getStringExtra("receiverUserId");
        checkFile = getIntent().getStringExtra("type");

        Picasso.with(this).load(imageUriff) . into(compleImageView);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        imagesStorageRef = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();

        System.out.println("senderUserId_2 "+ senderUserId);
        System.out.println("receiverUserId_2 "+ receiverUserId);
        System.out.println("type :"+ checkFile);
    }



    private void savingInformationToDatabase() {

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveDate = currentDate.format(Calendar.getInstance().getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveTime = currentTime.format(Calendar.getInstance().getTime());



        DatabaseReference senderMessRef = rootRef.child("Messages").
                   child(senderUserId).child(receiverUserId);

        final DatabaseReference receiverMessRef = rootRef.child("Messages").
                child(receiverUserId).child(senderUserId);

         uniKey = senderMessRef.push().getKey();

                final HashMap postsMap = new HashMap();

                postsMap.put("from" , senderUserId);
                postsMap.put("to", receiverUserId);
                postsMap.put("date" , saveDate);
                postsMap.put("time" , saveTime);
                postsMap.put("description" ,"null" );
                postsMap.put("type" , "image");
                postsMap.put("message",imageUriff);

                senderMessRef.child(uniKey).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful())
                        {
                            receiverMessRef.child(uniKey).updateChildren(postsMap).addOnCompleteListener(
                                    new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {

                                            if(task.isSuccessful())
                                            {
                                                mDialog.dismiss();

                                                sendUserToChatActivity();
                                            }
                                            else
                                            {
                                                Toast.makeText(Activity_During_Sending_Image_Activity.this,
                                                        "px not sent", Toast.LENGTH_SHORT).show();
                                                mDialog.dismiss();
                                            }
                                        }
                                    }
                            );
                        }
                    }
                });


    }


    public void sendUserToChatActivity()
    {
        Intent intent = new Intent(getApplicationContext() , ChatActivity.class);
        intent.putExtra("visit_user_id" , receiverUserId);
        startActivity(intent);
        finish();
    }


}
