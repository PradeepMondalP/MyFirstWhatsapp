package com.example.myfirstwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private EditText phoneNumberEdt , enterCodeEdt;
    private Button sendCodeBtn , veryCodeBtn;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private String mVerificationId ;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String phoneNumber = "+91";
    private String currentUserID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        initialize();

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 phoneNumber = phoneNumber.concat(phoneNumberEdt.getText().toString().trim());
                if(TextUtils.isEmpty(phoneNumber))
                {
                    phoneNumberEdt.setError("must enter a number");
                    return;
                }
                else
                {
                    loadingBar.setTitle("Verifying...");
                    loadingBar.setMessage("please wait a second...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks );        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
                }
            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Toast.makeText(PhoneLoginActivity.this, "Enter a valid mobile number ",
                        Toast.LENGTH_SHORT).show();

                sendCodeBtn.setVisibility(View.VISIBLE);
                phoneNumberEdt.setVisibility(View.VISIBLE);

                veryCodeBtn.setVisibility(View.INVISIBLE);
                veryCodeBtn.setVisibility(View.INVISIBLE);

                loadingBar.dismiss();

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {

                loadingBar.dismiss();

                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(PhoneLoginActivity.this,
                        "code is sent successfully.. ", Toast.LENGTH_SHORT).show();

                sendCodeBtn.setVisibility(View.INVISIBLE);
                phoneNumberEdt.setVisibility(View.INVISIBLE);

                veryCodeBtn.setVisibility(View.VISIBLE);
                enterCodeEdt.setVisibility(View.VISIBLE);


            }


    };


        veryCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendCodeBtn.setVisibility(View.INVISIBLE);
                phoneNumberEdt.setVisibility(View.INVISIBLE);

                String verificationCode = enterCodeEdt.getText().toString().trim();

                if(TextUtils.isEmpty(verificationCode))
                {
                    enterCodeEdt.setError("enter the code");
                    return;
                }
                else
                {
                    loadingBar.setTitle("code is Verifying...");
                    loadingBar.setMessage("please wait a second...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this,
                                    "logged in successfulky", Toast.LENGTH_SHORT).show();

                            sendUserToMainActivity();

                        } else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(PhoneLoginActivity.this,
                                        "error in log in", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }


    private void initialize() {

        phoneNumberEdt = (EditText)findViewById(R.id.id_phone_numberInput);
        enterCodeEdt = (EditText)findViewById(R.id.id_verification_code_input);
        sendCodeBtn = (Button)findViewById(R.id.send_verf_code);
        veryCodeBtn = (Button)findViewById(R.id.send_verf_accnt);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
    }

    private void sendUserToMainActivity() {

        Intent obj = new Intent(getApplicationContext() , MainActivity.class);
        obj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(obj);
        finish();
    }
}
