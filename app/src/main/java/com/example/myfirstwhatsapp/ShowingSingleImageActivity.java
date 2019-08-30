package com.example.myfirstwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zolad.zoominimageview.ZoomInImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ShowingSingleImageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private PhotoView profileDP;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String userID   ,userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_single_image);

        initialize();

        showImage();
        updateUserStatus("online");
    }


    @Override
    protected void onStart() {
        super.onStart();

        updateUserStatus("online");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateUserStatus("online");

    }

    @Override
    protected void onResume() {
        super.onResume();


        updateUserStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        updateUserStatus("offline");
    }

    public void updateUserStatus(String state)
    {
        SimpleDateFormat date = new SimpleDateFormat("MMM dd,yyyy");
        String saveCurrentDate = date.format(Calendar.getInstance().getTime());
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
        String  saveCurrentTime = time.format(Calendar.getInstance().getTime());
        String  currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map map = new HashMap();
        map.put("time" , saveCurrentTime);
        map.put("date" , saveCurrentDate);
        map.put("type" , state);

        DatabaseReference userRef = rootRef.child("Users").child(currentUserID).child("userState");

        userRef.updateChildren(map);
    }


    private void showImage() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("images"))
                {
                    String image = dataSnapshot.child("images").getValue().toString();
                    Picasso.with(getApplicationContext()).load(image)
                                   .into( profileDP);

                    userName = dataSnapshot.child("name").getValue().toString();
                }
                else {
                    Picasso.with(getApplicationContext()).load(R.drawable.profile).into(profileDP);
                    userName = dataSnapshot.child("name").getValue().toString();
                }

                getSupportActionBar().setTitle(userName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ShowingSingleImageActivity.this,
                        "couldn't load profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialize() {

        userID = getIntent().getStringExtra("receiverID");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);


        mToolbar = (Toolbar)findViewById(R.id.id_app_bar_of_showing_singleImage);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        profileDP = (PhotoView) findViewById(R.id.id_zoomingProfileImage);
    }
}
