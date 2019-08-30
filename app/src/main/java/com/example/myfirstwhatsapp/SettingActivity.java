package com.example.myfirstwhatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private EditText userName , userStatus;
    private Button updateButton;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef ;
    private StorageReference profileImageRef;
    private ProgressDialog mDialog;

    private static final int GalleryPix =1;
    private Uri imageUri  ;
    private String downloadUrl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initialize();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

       displayNameAndStatus();
//
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


    //    @Override
//    protected void onPause() {
//        super.onPause();
//        updateUserStatus("offline");
//
//    }




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



    private void displayNameAndStatus() {
        DatabaseReference dr2 = rootRef.child("Users").child(currentUserID);
        dr2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.hasChild("name")
                && dataSnapshot.hasChild("images"))
                {
                    String retrieveName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String retrieveProfileImage = dataSnapshot.child("images").getValue().toString();

                    userName.setText(retrieveName);
                    userStatus.setText(retrieveStatus);

                    Picasso.with(getApplicationContext()).load(retrieveProfileImage)
                            .placeholder(R.drawable.profile).into(profileImage);
                }
                else if( dataSnapshot.hasChild("name") )
                {
                    String retrieveName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                    userName.setText(retrieveName);
                    userStatus.setText(retrieveStatus);
                }
               else
                   {
                    Toast.makeText(SettingActivity.this,
                            "please update your profile Setting", Toast.LENGTH_LONG).show();
                     }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSettings() {
        String userNamee = userName.getText().toString().trim();
        String userStatuss = userStatus.getText().toString().trim();

        if(TextUtils.isEmpty(userNamee))
        {
            userName.setError("required");
            return;
        }
        else
            if(TextUtils.isEmpty(userStatuss))
            {
                userStatus.setError("required");
                return;
            }
           else
            {
                mDialog.setTitle("Logging in");
                mDialog.setMessage("Loading....");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                HashMap<String , Object> map = new HashMap<>();
                map.put("uid" , currentUserID);
                map.put("name" , userNamee);
                map.put("status" , userStatuss);
              //  map.put("images" , imageUri);
                map.put("gender" , "null");

               DatabaseReference rootRef2 = rootRef.child("Users").child(currentUserID);
               rootRef2.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {

                       if(task.isSuccessful())
                       {
                           Toast.makeText(SettingActivity.this,
                                   "profile updated succesfulyy", Toast.LENGTH_SHORT).show();
                           mDialog.dismiss();

                           sendUserToMainActivity();
                       }
                       else
                       {
                           Toast.makeText(SettingActivity.this,
                                   "couldnt update the setting", Toast.LENGTH_SHORT).show();
                           mDialog.dismiss();
                       }
                   }
               });
            }
    }



    private void sendUserToMainActivity() {
        Intent obj2 = new Intent(getApplicationContext() , MainActivity.class);
        obj2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(obj2);
        finish();
    }
//
//
    private void openGallery() {
        Intent obj = new Intent();
        obj.setType("image/*");
        obj.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(obj , GalleryPix);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==GalleryPix && resultCode ==RESULT_OK
                && data!=null && data.getData()!=null) {
             imageUri = data.getData();

            // now i want to crop the image
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(  requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                mDialog.setTitle("Profile Image");
                mDialog.setMessage("Please wait while upadating ur profile image....");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                final Uri resultUri = result.getUri();

                // after cropping im displaying it immediately
                Picasso.with(getApplicationContext()).load(resultUri).into(profileImage);

                final StorageReference filePath = profileImageRef.child(currentUserID +".jpg");

                //  saving to storage and confirming it
                filePath.putFile(resultUri).addOnCompleteListener
                        (new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),
                                            "profile image  successfully uploaded"
                                            , Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();

                                    //  getting the image url and saving it to the
                                    //  firebase database and confirming it

                                    filePath.getDownloadUrl().addOnSuccessListener
                                            (new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {

                                                    // uri returns the url of the image
                                                      downloadUrl = uri.toString();

                                                    // saving the url in the database and confirming it
                                 DatabaseReference userRef =rootRef.child("Users").child(currentUserID)
                                                          .child("images");

                                                  userRef.setValue(downloadUrl).addOnCompleteListener(
                                                          new OnCompleteListener<Void>() {
                                                              @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                                  if(task.isSuccessful()) {

                                                                  }
                                                                  else
                                                                  {
                                                                      Toast.makeText(SettingActivity.this,
                                                                              "couldnt upld to imag to db",
                                                                              Toast.LENGTH_SHORT).show();
                                                                      mDialog.dismiss();
                                                                  }

                                                              }
                                                          }
                                                  );

                                                }
                                            }) ;
                                }
                            }
                        });
            }
            else
            {
                Toast.makeText(getApplicationContext(),
                        "error occured: Image Cannot be cropped try again..",
                        Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        }

    }


    private void initialize() {
        profileImage = (CircleImageView)findViewById(R.id.id_settingAct_profile_image);
        userName = (EditText)findViewById(R.id.id_settingAct_full_name);
        userStatus = (EditText)findViewById(R.id.id_settingAct_status);
        updateButton = (Button)findViewById(R.id.id_settingAct_update_btn);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();
        mDialog = new ProgressDialog(this);

        profileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

    }


}
