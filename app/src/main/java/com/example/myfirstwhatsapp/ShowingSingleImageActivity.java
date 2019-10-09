package com.example.myfirstwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
    private OnlineUserStatus obbj;
    private  boolean state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_single_image);

        initialize();

        showImage();
        obbj.updateTheStatus("online");

        profileDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!state)
                {
                    mToolbar.setVisibility(View.VISIBLE);
                    state = true;
                }
                else
                {
                    mToolbar.setVisibility(View.GONE);
                    state =false;
                }

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                {
                    profileDP.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


                }
                else
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
                {
                    profileDP.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
                }
                else
                {
                    //.......
                }

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        obbj.updateTheStatus("online");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        obbj.updateTheStatus("online");

    }

    @Override
    protected void onResume() {
        super.onResume();


        obbj.updateTheStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        obbj.updateTheStatus("offline");
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

        obbj = new OnlineUserStatus();
    }


}
