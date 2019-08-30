package com.example.myfirstwhatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FloatingActionButton sendMessageButton;
    private EditText userMessgeEdt;

    private String groupName  , currentUserName  , cuurentDate , currentTime   ;
    private String currentUserID ;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef , groupRef   , groupMesgKeyRef;
    private RecyclerView myRecyclerView;

    private LinearLayoutManager layoutManager ;
    private final List<GroupMessages> myList = new ArrayList<>();
    GroupMessageAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupName = getIntent().getStringExtra("groupName");

        initialize();

        getUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessageinfoToDatabase();
                userMessgeEdt.setText("");

            }
        });

        displayAllMessages();
        updateUserStatus("online");
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUserStatus("online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserStatus("online");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

        Map map = new HashMap();
        map.put("time" , saveCurrentTime);
        map.put("date" , saveCurrentDate);
        map.put("type" , state);

        DatabaseReference userRef = rootRef.child("Users").child(currentUserID).child("userState");

        userRef.updateChildren(map);
    }

    
    private void initialize() {
        mToolbar = (Toolbar)findViewById(R.id.group_chat_bar_layoyt);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true) ;


        sendMessageButton= (FloatingActionButton) findViewById(R.id.group_activty_send_mes_btn);
        userMessgeEdt = (EditText)findViewById(R.id.group_activity_message_text);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName);


        myRecyclerView = (RecyclerView)findViewById(R.id.id_recycler_view_group_chat);
        layoutManager = new LinearLayoutManager(this);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(layoutManager);
        myAdapter= new GroupMessageAdapter(myList);
        myRecyclerView.setAdapter(myAdapter);

    }


    private void getUserInfo() {

       DatabaseReference dr2 = userRef.child(currentUserID);
       dr2.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if(dataSnapshot.hasChild("name"))
               {
                   currentUserName=dataSnapshot.child("name").getValue().toString();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void sendMessageinfoToDatabase() {
        String message=userMessgeEdt.getText().toString().trim();
        String Key = groupRef.push().getKey();

        if(TextUtils.isEmpty(message)){
            userMessgeEdt.setError("must enter a mesg");
            return;
        }
        else{
            Calendar obj = Calendar.getInstance();
            SimpleDateFormat currentDatefmt = new SimpleDateFormat("MMM dd,YYYY");
            cuurentDate = currentDatefmt.format(obj.getTime());

            SimpleDateFormat currentTimefmt = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimefmt.format(Calendar.getInstance().getTime());

            HashMap<String ,Object> groupMesgKey = new HashMap<>();
            groupRef.updateChildren(groupMesgKey);

            groupMesgKeyRef =groupRef.child(Key);

            HashMap<String ,Object> mesgInfoMap = new HashMap<>();
            mesgInfoMap.put("name" , currentUserName);
            mesgInfoMap.put("message" , message);
            mesgInfoMap.put("date" , cuurentDate);
            mesgInfoMap.put("time" , currentTime);
            mesgInfoMap.put("type" , "text");
            mesgInfoMap.put("from" , currentUserID);

            groupMesgKeyRef.updateChildren(mesgInfoMap);


        }
    }


    private void displayAllMessages()
    {


        groupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists())
                {
                    GroupMessages obj = dataSnapshot.getValue(GroupMessages.class);
                    myList.add(obj);
                    myAdapter.notifyDataSetChanged();

                    myRecyclerView.smoothScrollToPosition(myRecyclerView.getAdapter().getItemCount());

                }
                else {
                    Toast.makeText(GroupChatActivity.this,
                            "no child added", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_chat_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.id_group_info:
                Toast.makeText(this,
                        "group info", Toast.LENGTH_SHORT).show();
                break;

            case R.id.id_group_wallpaper:
                Toast.makeText(this,
                        "change wallpaper", Toast.LENGTH_SHORT).show();
                break;

            case R.id.id_clear_chat:
                Toast.makeText(this,
                        "clear caht ", Toast.LENGTH_SHORT).show();
                break;

            case R.id.id_exit_group:
                Toast.makeText(this,
                        "exit group", Toast.LENGTH_SHORT).show();
                break;

            case R.id.id_more_group:
                Toast.makeText(this,
                        "more", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}
