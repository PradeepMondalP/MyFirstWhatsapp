package com.example.myfirstwhatsapp;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View requestFragmentView;
    private RecyclerView myRequestList;
    private String currentUserID;
    private DatabaseReference chatRequestsRef  , userRef  ,rootRef  , contactsRef
            , chatRequestRef22  , contactRef22  ,chatRequestRef33;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        myRequestList = (RecyclerView)requestFragmentView.findViewById(R.id.id_myRecyclerViewOfReqFrg);

        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        chatRequestsRef = FirebaseDatabase.getInstance().getReference()
                .child("Chat Requests").child(currentUserID);

        userRef         =FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef = FirebaseDatabase.getInstance().getReference();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        chatRequestRef22 =FirebaseDatabase.getInstance().getReference () ;
        chatRequestRef33 =FirebaseDatabase.getInstance().getReference ().child("Chat Requests") ;
        contactRef22 = FirebaseDatabase.getInstance().getReference();
        mDialog = new ProgressDialog(getContext());



        displayRequests();

        return  requestFragmentView;
    }

    private void displayRequests() {

        chatRequestsRef.keepSynced(true);

        FirebaseRecyclerAdapter<Contacts ,RequestsHolder> obj =
                new FirebaseRecyclerAdapter<Contacts, RequestsHolder>
                        (
                                Contacts.class , R.layout.all_user_display_layout,
                                RequestsHolder.class ,chatRequestsRef
                        )
                {
                    @Override
                    protected void populateViewHolder(final RequestsHolder holder, Contacts model, int pos)
                    {
                         final String list_user_ID = getRef(pos).getKey();

                         DatabaseReference getTypeRef = rootRef.child("Chat Requests")
                                                              .child(currentUserID).child(list_user_ID);

                         getTypeRef.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                 if(dataSnapshot.exists() )
                                 {
                                   final  String type = dataSnapshot.child("request_type").getValue().toString();

                                     if(type.equals("received") )
                                     {
                                         holder.acceptBtn.setVisibility(View.VISIBLE);
                                         holder.rejectBtn.setVisibility(View.VISIBLE);
                                         holder.rejectBtn.setEnabled(true);
                                         holder.acceptBtn.setEnabled(true);

                                          userRef.child(list_user_ID).addValueEventListener(new ValueEventListener() {
                                              @Override
                                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                  if(dataSnapshot.hasChild("images"))
                                                  {
                                                      String retrieveName = dataSnapshot.child("name").getValue().toString();
                                                      String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                                                      String retrieveProfileImage = dataSnapshot.child("images").getValue().toString();

                                                      holder.userNamee.setText(retrieveName);
                                                      holder.statuss.setText(retrieveStatus);

                                                      Picasso.with(getContext()).load(retrieveProfileImage)
                                                              .placeholder(R.drawable.profile).into(holder.profileImagee);

                                                  }
                                                  else
                                                  {
                                                      String retrieveName = dataSnapshot.child("name").getValue().toString();
                                                      String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                                                      holder.userNamee.setText(retrieveName);
                                                      holder.statuss.setText(retrieveStatus);
                                                      Picasso.with(getContext())
                                                              .load(R.drawable.profile).into(holder.profileImagee);


                                                  }
                                              }

                                              @Override
                                              public void onCancelled(@NonNull DatabaseError databaseError) {

                                              }
                                          });

                                         holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                                                 mDialog.setTitle("Request accepting.....");
                                                 mDialog.setCanceledOnTouchOutside(false);
                                                 mDialog.show();

                                                 acceptChatRequest(currentUserID , list_user_ID);

                                                 System.out.println("Userd ID Cuurent(sender)"+currentUserID);
                                                 System.out.println("User ID  receiver" + list_user_ID) ;
                                             }
                                         });

                                         holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                                                 mDialog.setTitle("cancelling request.....");
                                                 mDialog.setCanceledOnTouchOutside(false);
                                                 mDialog.show();

                                         //        cancelChatRequest(currentUserID , list_user_ID);

                                                 AlertDialog.Builder obj = new AlertDialog.Builder(getContext());
                                                 obj.setTitle("Cancel Request");
                                                 obj.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         cancelChatRequest(currentUserID , list_user_ID);
                                                     }
                                                 })

                                                  .setNegativeButton("No" , null) .setCancelable(false);

                                                 AlertDialog alertDialog = obj.create();
                                                 alertDialog.show();


                                             }
                                         });


                                     }
       //did the below function  myselves.....................................................

                                     if(type.equals("sent"))
                                     {
                                           holder.acceptBtn.setVisibility(View.VISIBLE);
                                           holder.acceptBtn.setText("Cancel Friend Request");
                                           holder.rejectBtn.setVisibility(View.INVISIBLE);
                                           holder.rejectBtn.setEnabled(false);

                                         userRef.child(list_user_ID).addValueEventListener(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                 if(dataSnapshot.hasChild("images"))
                                                 {
                                                     String retrieveName = dataSnapshot.child("name").getValue().toString();
                                                     String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                                                     String retrieveProfileImage = dataSnapshot.child("images").getValue().toString();

                                                     holder.userNamee.setText(retrieveName);
                                                     holder.statuss.setText(retrieveStatus);

                                                     Picasso.with(getContext()).load(retrieveProfileImage)
                                                             .placeholder(R.drawable.profile).into(holder.profileImagee);


                                                 }
                                                 else
                                                 {
                                                     String retrieveName = dataSnapshot.child("name").getValue().toString();
                                                     String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                                                     holder.userNamee.setText(retrieveName);
                                                     holder.statuss.setText(retrieveStatus);
                                                     Picasso.with(getContext())
                                                             .load(R.drawable.profile).into(holder.profileImagee);

                                                 }
                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {

                                             }
                                         });

                                         holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                                                 mDialog.setTitle("cancelling request.....");
                                                 mDialog.setCanceledOnTouchOutside(false);
                                                 mDialog.show();

                                                 AlertDialog.Builder obj = new AlertDialog.Builder(getContext());
                                                 obj.setTitle("Cancel Request");
                                                 obj.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         cancelChatRequest(currentUserID , list_user_ID);
                                                     }
                                                 })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            mDialog.dismiss();
                                                        }
                                                    }).setCancelable(false);

                                                 AlertDialog alertDialog = obj.create();
                                                 alertDialog.show();

                                             }
                                         });

                                     }
                                 }

                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {

                             }
                         });




                    }
                };

        myRequestList.setAdapter(obj);
        obj.startListening();
    }


    public static class RequestsHolder extends RecyclerView.ViewHolder
    {

        View mView;
        CircleImageView profileImagee;
        TextView userNamee, statuss;
        Button acceptBtn , rejectBtn;

        public RequestsHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            profileImagee = (CircleImageView) mView.findViewById(R.id.id_all_user_profile_image);
            userNamee = (TextView) mView.findViewById(R.id.id_user_profile_name);
            statuss = (TextView) mView.findViewById(R.id.id_user_status);
            acceptBtn = (Button)mView.findViewById(R.id.id_accept_request_btn);
            rejectBtn = (Button)mView.findViewById(R.id.id_reject_request_btn);

        }

    }


    private void cancelChatRequest(final String senderUserID, final String receiverUserId) {

        {
            DatabaseReference dr2 = chatRequestRef33.child(senderUserID).child(receiverUserId);

            dr2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        DatabaseReference dr3 = chatRequestRef33.child(receiverUserId).child(senderUserID);

                        dr3.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    mDialog.dismiss();

                                    Toast.makeText(getContext(),
                                            "friend request" +
                                                    "cancelled successfully", Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    mDialog.dismiss();
                                    Toast.makeText(getContext(),
                                            "error in cancelling", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                }
            });
        }

    }

    private void acceptChatRequest( final String senderUserID, final String receiverUserId) {

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

                                    DatabaseReference dr4 = chatRequestRef22.child("Chat Requests").child(senderUserID).child(receiverUserId);
                                    dr4.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                DatabaseReference dr5 = chatRequestRef22.child("Chat Requests").child(receiverUserId).child(senderUserID);

                                                dr5.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            mDialog.dismiss();

                                                        }
                                                        else {

                                                            mDialog.dismiss();
                                                            Toast.makeText(getContext(),
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
    }



}
