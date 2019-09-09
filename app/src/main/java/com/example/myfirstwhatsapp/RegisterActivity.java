package com.example.myfirstwhatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText email , pass , verify_pass ;
    private Button createAccountBtn;
    private TextView logIn;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference rootRef , userRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialize_ids();


        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendUserToLoginActivity();
            }
        });

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = email.getText().toString().trim();
                String mPass = pass.getText().toString().trim();
                String mVerifyPass = verify_pass.getText().toString().trim();

                createAccountInDatabse(mEmail ,mPass ,mVerifyPass);
            }
        });

    }

    private void createAccountInDatabse(String mEmail, String mPass, String mVerifyPass)
    {
        if(TextUtils.isEmpty(mEmail))
        {
            email.setError("Enter a Email");
            return;
        }
        else if(TextUtils.isEmpty(mPass) || mPass.length()<7){
            pass.setError("Password req of min 7 char");
            return;
        }
        else if(TextUtils.isEmpty(mVerifyPass) || mVerifyPass.length()<7)
        {
            verify_pass.setError("Enter the correct pass again ");
            return;
        }
        else if( ! mPass.equals(mVerifyPass)  && (mPass.length() <7 ))
        {
            Toast.makeText(getApplicationContext(), "Password Doesnt match or min req pass" +
                            "length 7 char..",
                    Toast.LENGTH_SHORT).show();
        }
        else {



            loadingBar.setTitle("Creating new Account");
            loadingBar.setMessage("Please wait while creating new Account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            mAuth.createUserWithEmailAndPassword(mEmail ,mPass).addOnCompleteListener
                    (new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this,
                                "successfully authenticated", Toast.LENGTH_SHORT).show();

                        sendUserToSettingActivity();
                    }
                    else
                    {
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this,
                                "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }


    private void initialize_ids() {

        mAuth = FirebaseAuth.getInstance();

        email =(EditText) findViewById(R.id.id_email_signUp);
        pass = (EditText)findViewById(R.id.id_pass_signUp);
        verify_pass = (EditText)findViewById(R.id.id_confirm_pass_signUp);
        createAccountBtn = (Button)findViewById(R.id.id_register_signUp);
        logIn = (TextView)findViewById(R.id.id_login_signUp);


        loadingBar = new ProgressDialog(this);

        rootRef =FirebaseDatabase.getInstance().getReference();
    }



    private void sendUserToLoginActivity() {
        Intent obj = new Intent(getApplicationContext() , LoginActivity.class);
        obj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(obj);
        finish();
    }

    private void sendUserToSettingActivity() {
        Intent obj = new Intent(getApplicationContext() , SettingActivity.class);
        obj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(obj);
        finish();
    }

}


