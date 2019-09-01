package com.example.myfirstwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTablayout;
    private TabsAccessAdapter myAccessAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FloatingActionButton fab;
    private String saveCurrentDate , saveCurrentTime , currentUserID ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.id_mainAct_toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Whatsapp");

        myViewPager = (ViewPager) findViewById(R.id.id_mainAct_view_pager);
        myAccessAdapter = new TabsAccessAdapter(getSupportFragmentManager());

        myViewPager.setAdapter(myAccessAdapter);

        myTablayout = (TabLayout) findViewById(R.id.id_mainAct_tab_layout);

        // use  the below two lines when more tab layout
     //   myTablayout.setTabGravity(TabLayout.GRAVITY_CENTER);
     //   myTablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        myTablayout.setupWithViewPager(myViewPager);



        initialize_ids();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToFindFriendActivity();
            }
        });

        updateUserStatus("online");
    }

    @Override
    protected void onStart() {
        super.onStart();

        verifyUserExistence();
        updateUserStatus("online");
    }



    @Override
    protected void onResume()
    {
        super.onResume();

        updateUserStatus("online");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        updateUserStatus("offline");
    }

    @Override
    protected void onPause() {
        super.onPause();

        updateUserStatus("offline");
    }

    private void initialize_ids() {
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        fab = (FloatingActionButton)findViewById(R.id.id_floating_btn);
        currentUserID = mAuth.getCurrentUser().getUid();
    }



    public void verifyUserExistence()
    {
        final String currentUserID = mAuth.getCurrentUser().getUid();

        rootRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if( dataSnapshot.child(currentUserID).hasChild("name"))
                {
                    //
                }
                else
                {
                    sendUserToTheSettingActivity();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.option_menu , menu );
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.setting:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                sendUserToSettingActivity();
                break;

            case R.id.logout:
                updateUserStatus("offline");
                Toast.makeText(this, "logged out ", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                sendUserToLoginActivity();
                break;

            case R.id.create_group:
                requestNewGroup();
                break;

            case R.id.id_about:
                Toast.makeText(this, "Deep", Toast.LENGTH_SHORT).show();
                break;

            case R.id.id_originsl_setting:
                Toast.makeText(this, "setitng", Toast.LENGTH_SHORT).show();
                break;

            case R.id.id_notes:
                 sendUserToSemisterSelectionActivity();
                 break;

        }
        return true;
    }



    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.AlertDialog);
        builder.setTitle("Enter group Name");

        final EditText edt  = new EditText(MainActivity.this);
        edt.setHint("e.g CSE Student");
        builder.setView(edt)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String groupName= edt.getText().toString().trim();

                        if(TextUtils.isEmpty(groupName))
                        {
                            edt.setError("Required");
                            return;
                        }
                        else
                            createNewGroup(groupName);
                    }
                })

              .setNegativeButton("cancel" , null)
              .setCancelable(false);
        builder.show();
    }


    private void createNewGroup(String groupName) {
        DatabaseReference dr2 = rootRef.child("Groups").child(groupName);
        dr2.setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,
                            "group created successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,
                            "couldnt create group", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }






    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this );

        builder.setTitle("Exit")
                .setMessage("want to exit..")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .setCancelable(true);

        builder.create().show();
    }

    private void sendUserToFindFriendActivity() {

        Intent obj = new Intent(getApplicationContext() , FindFriendsActivity.class);
        updateUserStatus("online");
        startActivity(obj);
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(getApplicationContext() , LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
    private void sendUserToSettingActivity() {
        Intent loginIntent = new Intent(getApplicationContext() , SettingActivity.class);
        startActivity(loginIntent);
    }

    public void updateUserStatus(String state)
    {
        SimpleDateFormat date = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = date.format(Calendar.getInstance().getTime());

        SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = time.format(Calendar.getInstance().getTime());

        Map map = new HashMap();
        map.put("time" , saveCurrentTime);
        map.put("date" , saveCurrentDate);
        map.put("type" , state);

        DatabaseReference userRef = rootRef.child("Users").child(currentUserID).child("userState");

        userRef.updateChildren(map);
    }

    public void sendUserToTheSettingActivity()
    {
        Intent obj = new Intent(this , SettingActivity.class);
     //   obj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(obj);
        finish();
    }

    public void sendUserToSemisterSelectionActivity()
    {
        Intent obj = new Intent(this , SemisterSelectionActivity.class);
        //   obj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(obj);

    }

}