package com.example.myfirstwhatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartingActivity extends AppCompatActivity {


    private TextView agreeBtn;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        agreeBtn = (TextView) findViewById(R.id.agreeNContinueTVBtn);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mUser !=null)
        {
            Intent obj = new Intent(getApplicationContext() , MainActivity.class);
            obj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(obj);
            finish();
        }
        else
        {
             agreeBtn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     sendUserToLoginActivity();
                 }
             });
        }
    }

    private void sendUserToLoginActivity() {

        Intent obj = new Intent(getApplicationContext() , LoginActivity.class);
        obj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(obj);
        finish();
    }
}
