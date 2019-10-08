package com.example.myfirstwhatsapp;

import android.graphics.Color;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter <MessageAdapter.MessageViewHolder>
{
    private List<MyMessages> userMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<MyMessages> userMessageList) {
        this.userMessageList = userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_message_layout_2 , parent ,false);

        mAuth =FirebaseAuth.getInstance();
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position)
    {

        String messageSenderId = mAuth.getCurrentUser().getUid();
        MyMessages messages = userMessageList.get(position);
        String fromUserId = messages.getFrom();
        String fromMessageType=messages.getType();


        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverTime.setVisibility(View.GONE);
        holder.mesgRecvPix.setVisibility(View.GONE);
        holder.mesgSenderPix.setVisibility(View.GONE);
        holder.mesgRecvPix.setVisibility(View.GONE);
        holder.senderTime.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.senderImageDescription.setVisibility(View.GONE);
        holder.receiverImageDescription.setVisibility(View.GONE);

        if(fromMessageType.equals("text"))
        {

            if(fromUserId.equals(messageSenderId))
            {
                holder.senderTime.setVisibility(View.VISIBLE);
                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setGravity(Gravity.LEFT);

                holder.senderMessageText.setText(messages.getMessage());
                holder.senderTime.setText(messages.getTime());

            }else
            {

                holder.receiverTime.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_text_background);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setGravity(Gravity.LEFT);
                holder.receiverMessageText.setText(messages.getMessage());
                holder.receiverTime.setText(messages.getTime());

            }
        }
        else if(fromMessageType.equals("image"))
        {

            if(messageSenderId .equals(fromUserId))
            {
                holder.mesgSenderPix.setVisibility(View.VISIBLE);
                holder.senderImageDescription.setVisibility(View.VISIBLE);

                Picasso.with(holder.mesgSenderPix.getContext())
                        .load(messages.getMessage()).into(holder.mesgSenderPix);
                holder.senderImageDescription.setText(messages.getDescription());

            }
            else
            {

                holder.mesgRecvPix.setVisibility(View.VISIBLE);
                holder.receiverImageDescription.setVisibility(View.VISIBLE);

                holder.receiverImageDescription.setText(messages.getDescription());
                Picasso.with(holder.mesgRecvPix.getContext())
                        .load(messages.getMessage()).into(holder.mesgRecvPix);
            }
        }

        else if(fromMessageType.equals("pdf"))
        {

            if(messageSenderId .equals(fromUserId))
            {
                holder.mesgSenderPix.setVisibility(View.VISIBLE);
                holder.mesgSenderPix.setBackgroundResource(R.drawable.file);

            }
            else
            {

                holder.mesgRecvPix.setVisibility(View.VISIBLE);
                holder.mesgRecvPix.setBackgroundResource(R.drawable.file);

            }
        }
        else
        {

            if(messageSenderId .equals(fromUserId))
            {
                holder.mesgSenderPix.setVisibility(View.VISIBLE);
                holder.mesgSenderPix.setBackgroundResource(R.drawable.file);

            }
            else
            {

                holder.mesgRecvPix.setVisibility(View.VISIBLE);
                holder.mesgRecvPix.setBackgroundResource(R.drawable.file);

//                Picasso.with(holder.mesgRecvPix.getContext())
//                        .load(messages.getMessage()).into(holder.mesgRecvPix);
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder  extends RecyclerView.ViewHolder

    {
        View mView;
        TextView senderMessageText , receiverMessageText  , senderTime , receiverTime;
        ImageView mesgSenderPix , mesgRecvPix;
        TextView senderImageDescription , receiverImageDescription;


        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;
            senderMessageText = (TextView)mView.findViewById(R.id.sender_message_text_2);
            receiverMessageText = (TextView)mView.findViewById(R.id.receiver_message_text_2);

           senderTime = (TextView)mView.findViewById(R.id.id_sender_time);
           receiverTime = (TextView)mView.findViewById(R.id.id_receiver_time);

           mesgSenderPix =  (ImageView)mView.findViewById(R.id.id_mesg_sender_image_file);
           mesgRecvPix =  (ImageView)mView.findViewById(R.id.id_mesg_receiver_image_file);

           senderImageDescription = (TextView)mView.findViewById(R.id.id_sender_image_description);
           receiverImageDescription = (TextView)mView.findViewById(R.id.id_received_image_description);

        }

    }
}
