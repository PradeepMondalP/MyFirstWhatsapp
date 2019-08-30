package com.example.myfirstwhatsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView findFriendRecyclerView;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

          initialize();

          findFriendRecyclerView.setLayoutManager(new LinearLayoutManager(this));

          displayAllFriends();

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
        String  currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

        mToolbar = (Toolbar)findViewById(R.id.id_find_friend_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        findFriendRecyclerView = (RecyclerView)findViewById(R.id.id_find_friend_result_list);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    private void displayAllFriends()
    {

        Toast.makeText(this, "searching..", Toast.LENGTH_LONG).show();

        FirebaseRecyclerAdapter<Contacts ,FindFriendHolder> recyclerAdapter
                = new FirebaseRecyclerAdapter<Contacts, FindFriendHolder>
                (
                        Contacts.class , R.layout.all_user_display_layout,
                        FindFriendHolder.class , userRef
                )
        {
            @Override
            protected void populateViewHolder(FindFriendHolder holder, Contacts model , final int pos) {


                holder.setFullName(model.getName ()) ;
                holder.setStatus(model.getStatus()) ;
                holder.setImages(getApplicationContext(), model.getImages());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String visit_user_id = getRef(pos).getKey().toString();

                        Intent obj = new Intent(getApplicationContext() , ProfileActivity.class);
                        obj.putExtra("visit_user_id" , visit_user_id);
                        startActivity(obj);
                    }
                });

                holder.profileImagee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String visit_user_id = getRef(pos).getKey();
                        Intent obj = new Intent(getApplicationContext() , ShowingSingleImageActivity.class);
                        obj.putExtra("receiverID"  , visit_user_id);
                        startActivity(obj);

                    }
                });

            }

        };
        findFriendRecyclerView.setAdapter(recyclerAdapter);

    }


    public static class FindFriendHolder extends RecyclerView.ViewHolder
    {

        View mView;
        CircleImageView profileImagee ;
        TextView userNamee , statuss;
        public FindFriendHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            profileImagee = (CircleImageView)mView.findViewById(R.id.id_all_user_profile_image);
            userNamee = (TextView)mView.findViewById(R.id.id_user_profile_name);
            statuss = (TextView)mView.findViewById(R.id.id_user_status);
        }

        public void setStatus(String status)
        {
            statuss.setText(status);
        }

        public void setFullName(String fullName)
        {
            userNamee.setText(fullName);
        }

        public void setImages(Context ctx  , String images){

            Picasso.with(ctx).load(images).placeholder(R.drawable.profile).into(profileImagee);
        }
    }

}
