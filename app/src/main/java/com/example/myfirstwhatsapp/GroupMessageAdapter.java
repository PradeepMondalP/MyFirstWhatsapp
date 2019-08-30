package com.example.myfirstwhatsapp;

import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMessageAdapter extends RecyclerView.Adapter <GroupMessageAdapter.MyGroupHolder>{

    private List<GroupMessages> userMessageList ;
    private DatabaseReference rootRef;
    private String messageSenderID;
    private FirebaseAuth mAuth;

    public GroupMessageAdapter(List<GroupMessages> userMessageList) {
        this.userMessageList = userMessageList;

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.user_group_chat_2 , parent ,false);

        return  new MyGroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyGroupHolder holder, int position)
    {
         GroupMessages groupMessages = userMessageList.get(position);
         String fromMessageType = groupMessages.getType();
         String fromUserID = groupMessages.getFrom();



        holder.receiverMessage.setVisibility(View.INVISIBLE);
        holder.userDP.setVisibility(View.INVISIBLE);
        holder.receiverName.setVisibility(View.INVISIBLE);
        holder.receiverTime.setVisibility(View.INVISIBLE);
        holder.receiverLayout.setVisibility(View.INVISIBLE);

                if(fromUserID.equals(messageSenderID))
                {

                    holder.senderMessage.setVisibility(View.VISIBLE);
                    holder.senderTime.setVisibility(View.VISIBLE);
                    holder.senderLayout.setVisibility(View.VISIBLE);

                    holder.senderMessage.setBackgroundResource(R.drawable.sender_message_text_background);
                    holder.senderMessage.setTextColor(Color.BLACK);
                    holder.senderMessage.setGravity(Gravity.LEFT);

                    holder.senderMessage.setText(groupMessages.getMessage());
                    holder.senderTime.setText(groupMessages.getTime());
                }
                else
                {
                    holder.senderMessage.setVisibility(View.INVISIBLE);
                    holder.senderTime.setVisibility(View.INVISIBLE);
                    holder.senderLayout.setVisibility(View.INVISIBLE);

                    holder.receiverLayout.setVisibility(View.VISIBLE);
                    holder.receiverMessage.setVisibility(View.VISIBLE);
                    holder.userDP.setVisibility(View.VISIBLE);
                    holder.receiverName.setVisibility(View.VISIBLE);
                    holder.receiverTime.setVisibility(View.VISIBLE);

                    holder.receiverMessage.setBackgroundResource(R.drawable.receiver_message_text_background);
                    holder.receiverMessage.setTextColor(Color.BLACK);
                    holder.receiverMessage.setGravity(Gravity.LEFT);

                    holder.receiverMessage.setText(groupMessages.getMessage());
                    holder.receiverName.setText(groupMessages.getName());
                    holder.receiverTime.setText(groupMessages.getTime());

                    // to get the dp

                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(fromUserID);

                    dr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild("images"))
                            {
                                String image = dataSnapshot.child("images").getValue().toString();
                                Picasso.with(holder.userDP.getContext()).load(image)
                                        .into(holder.userDP);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MyGroupHolder  extends RecyclerView.ViewHolder {


        View mView;
        TextView receiverMessage , senderMessage , senderTime , receiverTime , receiverName;
        CircleImageView userDP;
        FrameLayout senderLayout , receiverLayout;


        public MyGroupHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView ;
            senderLayout = (FrameLayout)mView.findViewById(R.id.id_frame_layout_2);
            receiverLayout = (FrameLayout)mView.findViewById(R.id.id_frame_layout_1);

            receiverMessage = (TextView)mView.findViewById(R.id.receiver_message_text_group_chat_2);
            senderMessage = (TextView)mView.findViewById(R.id.sender_message_text_groupchat);
            senderTime = (TextView)mView.findViewById(R.id.id_sender_time_groupchat);
            receiverTime = (TextView)mView.findViewById(R.id.id_time_group_chat);
            receiverName = (TextView)mView.findViewById(R.id.receiver_message_text_group_chat);
            userDP = (CircleImageView)mView.findViewById(R.id.message_profile_image_group_chat_x);

        }
    }
}
