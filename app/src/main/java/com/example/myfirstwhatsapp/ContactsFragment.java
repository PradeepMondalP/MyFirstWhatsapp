package com.example.myfirstwhatsapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View conteactsView;
    private RecyclerView myContactlist;
    private DatabaseReference contactsRef , userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
                                       // it is nothing but chats fragment................

    public ContactsFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        conteactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactlist = (RecyclerView) conteactsView.findViewById(R.id.my_recycle);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userRef =    FirebaseDatabase.getInstance().getReference().child("Users");

        myContactlist.setLayoutManager(new LinearLayoutManager(getContext()));



        viewAllFriends();

        return conteactsView;
    }


    private void viewAllFriends() {

        contactsRef.keepSynced(true);

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> obj =
                new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>
                        (
                                Contacts.class, R.layout.all_user_display_layout,
                                ContactsViewHolder.class, contactsRef
                        ) {
                    @Override
                    protected void populateViewHolder(final ContactsViewHolder holder, Contacts model, final int position) {

                        String ID = getRef(position).getKey();

                        userRef.child(ID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists() && dataSnapshot.hasChild("name")
                                        && dataSnapshot.hasChild("images"))
                                {
                                    String retrieveName = dataSnapshot.child("name").getValue().toString();
                                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                                    String retrieveProfileImage = dataSnapshot.child("images").getValue().toString();

                                  holder.userNamee.setText(retrieveName);
                                    holder.statuss.setText(retrieveStatus);

                                    Picasso.with(getContext()).load(retrieveProfileImage)
                                            .placeholder(R.drawable.profile).into(holder.profileImagee);
                                }
                                else if(dataSnapshot.exists() && dataSnapshot.hasChild("name") )
                                {
                                    String retrieveName = dataSnapshot.child("name").getValue().toString();
                                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();


                                    holder.userNamee.setText(retrieveName);
                                    holder.statuss.setText(retrieveStatus);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String visit_user_id = getRef(position).getKey();
                                Intent obj = new Intent(getContext() , ChatActivity.class);
                                obj.putExtra("visit_user_id" , visit_user_id);
                                startActivity(obj);
                            }
                        });

                        holder.profileImagee.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String visit_user_id = getRef(position).getKey();
                                Intent obj = new Intent(getContext() , ShowingSingleImageActivity.class);
                                obj.putExtra("receiverID"  , visit_user_id);
                                startActivity(obj);

                            }
                        });
                    }
                };

        myContactlist.setAdapter(obj);
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        CircleImageView profileImagee;
        TextView userNamee, statuss;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            profileImagee = (CircleImageView) mView.findViewById(R.id.id_all_user_profile_image);
            userNamee = (TextView) mView.findViewById(R.id.id_user_profile_name);
            statuss = (TextView) mView.findViewById(R.id.id_user_status);
        }

        public void setStatus(String status) {
            statuss.setText(status);
        }

        public void setFullName(String fullName) {
            userNamee.setText(fullName);
        }

        public void setImages(final Context ctx,  final String images) {

            Picasso.with(ctx).load(images).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(profileImagee, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(images).into(profileImagee);
                        }
                    });
        }

    }
}
