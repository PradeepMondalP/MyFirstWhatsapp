package com.example.myfirstwhatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private CircleImageView receiverDP;
    private TextView receiverName , reciverLastSeen ;

    private String sendUserID  ,receiverUserID , uniKey , saveCurrentDate , saveCurrentTime;
    private  String lastSeenDate , lastSeenTime , lastSeenType  , checkerFile="";
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef , userRef , messageRef;
    private RecyclerView myRecyclerView;

    private static final int GalleryPix =1;
    private ImageButton sendFileBtn;
    private EditText inputMessage;
    private FloatingActionButton sendMessageButton;

    private final List<MyMessages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager ;
    private MessageAdapter messageAdapter ;
    private Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initialize();

        displayNameAndPhoto();

        receiverDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToProfileActivity();
            }
        });

        receiverName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToProfileActivity();
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        updateUserStatus("online");

        sendFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAllTytpesOfFiles();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        displayAllMessages();
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
        String  currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map map = new HashMap();
        map.put("time" , saveCurrentTime);
        map.put("date" , saveCurrentDate);
        map.put("type" , state);

        DatabaseReference userRef = rootRef.child("Users").child(currentUserID).child("userState");

        userRef.updateChildren(map);
    }


    private void displayAllMessages() {

        {
            DatabaseReference rootRef2 =  rootRef.child("Messages")
                    .child(sendUserID).child(receiverUserID);

            rootRef2.keepSynced(true);

            rootRef2.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                {
                    if(dataSnapshot.exists())
                    {
                        MyMessages messagesObj =dataSnapshot.getValue(MyMessages.class);
                        messagesList.add(messagesObj);
                        messageAdapter.notifyDataSetChanged();

                        myRecyclerView.smoothScrollToPosition(myRecyclerView
                                .getAdapter().getItemCount());
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this,
                                "outside child", Toast.LENGTH_SHORT).show();
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


    }


    private void initialize() {

        mToolbar = (Toolbar)findViewById(R.id.id_toolbar_chat_acttiviy);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar , null );
        actionBar.setCustomView(action_bar_view);


        sendFileBtn = (ImageButton)findViewById(R.id.id_selct_file_btn);
        receiverName  =(TextView)findViewById(R.id.id_custom_profile_name);
        receiverDP = (CircleImageView)findViewById(R.id.id_custom_profile_image);
        reciverLastSeen = (TextView)findViewById(R.id.id_custom_user_last_seen);
        inputMessage = (EditText)findViewById(R.id.id_chat_activity_message_text);
        sendMessageButton = (FloatingActionButton) findViewById(R.id.id_chat_activity_send_message_button);

        receiverUserID = getIntent().getStringExtra("visit_user_id");
        mAuth = FirebaseAuth.getInstance();
        sendUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");
        messageRef = rootRef.child("Messages");

        myRecyclerView = (RecyclerView)findViewById(R.id.id_recycler_chat_act);
        messageAdapter = new MessageAdapter(messagesList);

        linearLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setAdapter(messageAdapter);


    }

    private void displayNameAndPhoto()
    {

        DatabaseReference dr2 = userRef.child(receiverUserID);
        dr2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.hasChild("images")
                   )
                {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userImages = dataSnapshot.child("images").getValue().toString();

                    receiverName.setText(userName);
                    Picasso.with(getApplicationContext()).load(userImages).placeholder(R.drawable.profile)
                                                       .into(receiverDP);
                }
                else
                {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    receiverName.setText(userName);
                }

                if(dataSnapshot.hasChild("userState"))
                {
                    lastSeenDate = dataSnapshot.child("userState").child("date").getValue().toString();
                    lastSeenTime = dataSnapshot.child("userState").child("time").getValue().toString();
                    lastSeenType = dataSnapshot.child("userState").child("type").getValue().toString();


                    if(lastSeenType.equals("online"))
                    {
                        reciverLastSeen.setText("online");
                    }
                    else
                    {
                        reciverLastSeen.setText("last seen "+lastSeenDate+" "+lastSeenTime);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage()
    {
        final String messageText = inputMessage.getText().toString().trim();

        if(TextUtils.isEmpty(messageText))
        {
            inputMessage.setError("type a message");
            return;
        }
        else
        {
            inputMessage.setText("");
            final DatabaseReference messageSenderRef =
                    messageRef.child(sendUserID).child(receiverUserID);

            final DatabaseReference messageReceiverRef =
                    messageRef.child(receiverUserID).child(sendUserID);

            uniKey= messageSenderRef.push().getKey();

            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(Calendar.getInstance().getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = currentTime.format(Calendar.getInstance().getTime());

          final  Map<String , Object> map = new HashMap<>();

            map.put("date" , saveCurrentDate);
            map.put("time", saveCurrentTime);
            map.put("message", messageText);
            map.put("from" , sendUserID);
            map.put("description", "null");
            map.put("type" , "text");
            map.put("to" , receiverUserID);


          final  DatabaseReference messageSenderRef2 = messageSenderRef.child(uniKey);
          final  DatabaseReference messageReceiverRef2 = messageReceiverRef.child(uniKey);

            messageSenderRef2.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {
                        messageReceiverRef2.updateChildren(map).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            //
                                        }
                                        else
                                        {
                                            Toast.makeText(ChatActivity.this,
                                                    "couldnt send msg", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                        );
                    }
                }
            });

        }
    }


    public void sendUserToProfileActivity()
    {

        Intent intent = new Intent(getApplicationContext() , ProfileActivity.class);
        intent.putExtra("visit_user_id" , receiverUserID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chat_activity_menu , menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.id_view_profile_chat_act_menu:
                Toast.makeText(this, "go to profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.id_clear_chat_chat_act_menu:
                Toast.makeText(this, "Clear chat", Toast.LENGTH_SHORT).show();
                break;

            case R.id.id_wallpaper_chat_act_menu:
                Toast.makeText(this, "Change wallaper", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void openAllTytpesOfFiles()
    {
        CharSequence options[] = new CharSequence[]
                {
                        "image",
                        " PDF file" ,
                        "other documents"

                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select file");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                if(pos==0)
                {
                     checkerFile = "image";
                     openGallery();
                }
                if(pos==1)
                {
                      checkerFile = "pdf";
                }
                if(pos==2)
                {
                     checkerFile="docx";
                }

            }
        });
        builder.create().show();
    }

    public void openGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent , GalleryPix);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPix && resultCode==RESULT_OK && data!=null
                && data.getData()!=null)
        {
             imageUri = data.getData();

            System.out.println("image uri generated : "+ imageUri);

             Intent obj =new Intent(getApplicationContext() , Activity_During_Sending_Image_Activity.class);
             obj.putExtra("imageUri" , imageUri.toString() );
             obj.putExtra("senderUserId" , sendUserID);
             obj.putExtra("receiverUserId" , receiverUserID);
             obj.putExtra("messageID" , uniKey);
             obj.putExtra("type" , checkerFile);

            System.out.println("image uri sent  :"+ imageUri);
            System.out.println("sener UserID_1"+sendUserID);
            System.out.println("receiver_userOd_1"+receiverUserID);
             startActivity(obj);
             finish();


        }

    }

}
