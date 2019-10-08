package com.example.myfirstwhatsapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OnlineUserStatus {

    private String currentSaveDate, currentSaveTime , currentUserID;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;


    public   void updateTheStatus(String state)
    {
        mAuth = FirebaseAuth.getInstance();
        rootRef =FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();

        SimpleDateFormat date = new SimpleDateFormat("MMM dd,yyyy");
        currentSaveDate = date.format(Calendar.getInstance().getTime());

        SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
        currentSaveTime = time.format(Calendar.getInstance().getTime());

        Map map = new HashMap();
        map.put("time" , currentSaveDate);
        map.put("date" , currentSaveTime);
        map.put("type" , state);

        DatabaseReference userRef = rootRef.child("Users").child(currentUserID).child("userState");

        userRef.updateChildren(map);

    }
}
