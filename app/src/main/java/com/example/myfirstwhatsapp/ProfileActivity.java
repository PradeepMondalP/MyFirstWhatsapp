package com.example.myfirstwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String receiverUserId  , currentUserID;
    private TextView userName , userStatus;
    private Button sendReqBtn , declinetReqBtn;
    private CircleImageView userDP;
    private Toolbar mToolbar;

    private DatabaseReference userRef , chatRequestRef  , contactsRef;
    private String current_state  , senderUserID;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    private OnlineUserStatus obbj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializing();

        retrieveUserInfo();

        obbj.updateTheStatus("online");
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



    private void initializing()
    {

        mToolbar = (Toolbar)findViewById(R.id.id_app_bar_profil_act);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        receiverUserId = getIntent().getStringExtra("visit_user_id");


        userName = (TextView)findViewById(R.id.user_name);
        userStatus = (TextView)findViewById(R.id.user_status);
        sendReqBtn  =(Button)findViewById(R.id.send_mesg_req_btn);
        declinetReqBtn  =(Button)findViewById(R.id.send_mesg_reject_btn);
        userDP = (CircleImageView)findViewById(R.id.id_visit_profile_image);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        current_state = "new";
        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();
        mDialog = new ProgressDialog(this);

        currentUserID = mAuth.getCurrentUser().getUid();

        obbj = new OnlineUserStatus();

    }

    private void retrieveUserInfo() {

        DatabaseReference dr2 = userRef.child(receiverUserId);

        dr2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.hasChild("images"))
                {
                    String image = dataSnapshot.child("images").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(userDP);
                    userName.setText(name);
                    userStatus.setText(status);

                    getSupportActionBar().setTitle(name);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);

                    userDP.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendUserToShowingSingleImageActivity(receiverUserId);
                        }
                    });

                    manageChatRequest();

                }
                else {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    Picasso.with(getApplicationContext()).load(R.drawable.profile).into(userDP);
                    userName.setText(name);
                    userStatus.setText(status);

                    getSupportActionBar().setTitle(name);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);

                    userDP.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendUserToShowingSingleImageActivity(receiverUserId);
                        }
                    });

                    manageChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void manageChatRequest()
    {

        if(! senderUserID.equals(receiverUserId))
        {


            // chacking validation between two activities  weather theybarefriends or not
            // i am modifting the below function

            DatabaseReference myRef = contactsRef.child(currentUserID).child(receiverUserId).child("Contacts");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {
                        String value = dataSnapshot.getValue().toString();

                        if( value.equals("saved"))
                        {
                            current_state ="friends";
                            sendReqBtn.setEnabled(true);
                            sendReqBtn.setText("Unfriend");

                            declinetReqBtn.setEnabled(false);
                            declinetReqBtn.setVisibility(View.INVISIBLE);
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


      // till here i am modifying for checking the validation between two activituues

            sendReqBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    sendReqBtn.setEnabled(false);

                    if(current_state.equals("new")){

                        mDialog.setTitle("Request...");
                        mDialog.setTitle("sending  request.....");
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();

                        sendChatRequest();
                    }
                    if(current_state.equals("request_sent"))
                    {

                        mDialog.setTitle("Request...");
                        mDialog.setTitle("cancelling request.....");
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();

                        cancelChatRequest();

                    }
                    if(current_state.equals("request_received"))
                    {
                        mDialog.setTitle("Request...");
                        mDialog.setTitle("Accepting request.....");
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();

                         acceptChatRequest();
                    }
                    if(current_state.equals("friends"))
                    {
                        mDialog.setTitle("Request...");
                        mDialog.setTitle("Unfriending.....");
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();

                        unFriend();
                    }

                }
            });
        }
        else
        {
            sendReqBtn.setVisibility(View.INVISIBLE);
        }

        // for managing the buttons
        chatRequestRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild(receiverUserId))
                    {

                        String req_typ = dataSnapshot.child(receiverUserId).child("request_type")
                                           .getValue().toString();

                        if(req_typ.equals("sent"))
                        {
                            current_state = "request_sent";
                            sendReqBtn.setText("cancel chat request");
                        }
                        else if(req_typ.equals("received"))
                        {
                            current_state = "request_received";
                            sendReqBtn.setText("Accept Chat Request");

                            declinetReqBtn.setVisibility(View.VISIBLE);
                            declinetReqBtn.setEnabled(true);

                            declinetReqBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.setTitle("Request...");
                                    mDialog.setTitle("Declining  request.....");
                                    mDialog.setCanceledOnTouchOutside(false);
                                    mDialog.show();
                                    cancelChatRequest();
                                }
                            });
                        }
                    }
                    else {
                           contactsRef.child(senderUserID).addListenerForSingleValueEvent(
                                   new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                          if(dataSnapshot.hasChild(receiverUserId))
                                          {
                                              current_state="friends";
                                              sendReqBtn.setText("Unfriend");
                                          }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   }
                           );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void sendChatRequest()
    {
       DatabaseReference chatReq2 = chatRequestRef.child(senderUserID).child(receiverUserId).child("request_type");

       chatReq2.setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

               if(task.isSuccessful())
               {
                   DatabaseReference chatReq3 = chatRequestRef.child(receiverUserId)
                           .child(senderUserID).child("request_type");

                   chatReq3.setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                           if(task.isSuccessful())
                           {
                               mDialog.dismiss();
                               sendReqBtn.setEnabled(true);
                               current_state ="request_sent";
                               sendReqBtn.setText("Cancel chat request");

                           }
                           else
                           {
                               mDialog.dismiss();
                               Toast.makeText(ProfileActivity.this,
                                       "error, couldnt send request", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
               }
           }
       });
    }


    public void cancelChatRequest()
    {
        DatabaseReference dr2 = chatRequestRef.child(senderUserID).child(receiverUserId);

        dr2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    DatabaseReference dr3 = chatRequestRef.child(receiverUserId).child(senderUserID);

                    dr3.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                mDialog.dismiss();

                                Toast.makeText(ProfileActivity.this,
                                        "friend request" +
                                                "cancelled successfully", Toast.LENGTH_SHORT).show();

                                sendReqBtn.setEnabled(true);
                                current_state = "new";
                                sendReqBtn.setText("Send Chat Request");

                                declinetReqBtn.setVisibility(View.INVISIBLE);
                                declinetReqBtn.setEnabled(false);

                            }
                            else
                            {
                                mDialog.dismiss();
                                Toast.makeText(ProfileActivity.this,
                                        "error in cancelling", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    public void acceptChatRequest()
    {
        DatabaseReference dr2 = contactsRef.child(senderUserID).child(receiverUserId).child("Contacts");

        // adding the values to the New Node Chat like (Friends)

        dr2.setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    DatabaseReference dr3 = contactsRef.child(receiverUserId).child(senderUserID).child("Contacts");
                    dr3.setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                // removing values from Chat request node in DB

                                DatabaseReference dr4 = chatRequestRef.child(senderUserID).child(receiverUserId);
                                dr4.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            DatabaseReference dr5 = chatRequestRef.child(receiverUserId).child(senderUserID);

                                            dr5.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                         mDialog.dismiss();

                                                         sendReqBtn.setEnabled(true);
                                                         sendReqBtn.setText("Unfriend");

                                                         declinetReqBtn.setEnabled(false);
                                                         declinetReqBtn.setVisibility(View.INVISIBLE);
                                                    }
                                                   else {
                                                       mDialog.dismiss();
                                                        Toast.makeText(ProfileActivity.this,
                                                                "error in accepting" +
                                                                        "reuests..", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                        }

                                    }
                                });
                            }

                        }
                    });

                }
            }
        });
    }

    public void unFriend()
    {
        {
            DatabaseReference dr2 = contactsRef.child(senderUserID).child(receiverUserId);

            dr2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        DatabaseReference dr3 = contactsRef.child(receiverUserId).child(senderUserID);

                        dr3.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    mDialog.dismiss();

                                    Toast.makeText(ProfileActivity.this,
                                            "Unfriended" +
                                                    " successfully", Toast.LENGTH_SHORT).show();

                                    sendReqBtn.setVisibility(View.VISIBLE);
                                    sendReqBtn.setEnabled(true);
                                    current_state = "new";
                                    sendReqBtn.setText("Send Chat Request");

                                    declinetReqBtn.setVisibility(View.INVISIBLE);
                                    declinetReqBtn.setEnabled(false);

                                }
                                else
                                {
                                    mDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this,
                                            "error in cancelling", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                }
            });
        }
    }


    public void sendUserToShowingSingleImageActivity(String receiverUserId)
    {
        Intent obj = new Intent(getApplicationContext() , ShowingSingleImageActivity.class);
        obj.putExtra("receiverID" , receiverUserId);
        startActivity(obj);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_activity_menu , menu);
        return true ;
    }
}
