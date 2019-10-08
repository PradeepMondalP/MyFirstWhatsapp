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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email , pass;
    private Button login , phoneLoginBtn;
    private TextView newAccount;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private OnlineUserStatus obb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize_id();

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPass = pass.getText().toString().trim();

                loginToDatabase(mEmail , mPass);
            }
        });

        phoneLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPhoneLoginActivity();
            }
        });

    }


    private void loginToDatabase(String mEmail, String mPass) {
        if(TextUtils.isEmpty(mEmail)){
            email.setError("Enter a mail");
            return;
        }
        if(TextUtils.isEmpty(mPass)){
            pass.setError("Enter a password");
            return;
        }
        else {
            mDialog.setTitle("Logging in");
            mDialog.setMessage("Loading....");
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();

            mAuth.signInWithEmailAndPassword(mEmail , mPass).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                mDialog.dismiss();
                                Toast.makeText(LoginActivity.this,
                                        "welcome ", Toast.LENGTH_SHORT).show();
                                sendUserToMainActivity();
                                obb.updateTheStatus("online");
                            }else
                            {
                               mDialog.dismiss();
                                Toast.makeText(LoginActivity.this,
                                        "login failed..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        }

        }


    @Override
    protected void onStart() {
        super.onStart();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser!=null)
            sendUserToMainActivity();

    }

    private void sendUserToRegisterActivity() {
        Intent obj = new Intent(getApplicationContext() , RegisterActivity.class);
        startActivity(obj);
        finish();
    }



    private void sendUserToMainActivity() {
        Intent obj = new Intent(getApplicationContext() , MainActivity.class);
        obj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(obj);
        finish();
    }

    private void initialize_id() {

        email =(EditText) findViewById(R.id.id_email);
        pass = (EditText) findViewById(R.id.id_pass);
        login = (Button) findViewById(R.id.id_login);
        newAccount = (TextView) findViewById(R.id.id_signup);
        phoneLoginBtn = (Button)findViewById(R.id.id_phone);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        obb = new OnlineUserStatus();


    }

    private void sendUserToPhoneLoginActivity() {
        Intent obj = new Intent(getApplicationContext() , PhoneLoginActivity.class);
        startActivity(obj);
    }

}
