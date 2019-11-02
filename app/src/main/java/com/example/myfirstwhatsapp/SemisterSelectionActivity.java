package com.example.myfirstwhatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.example.myfirstwhatsapp.adapters.ImageAdapter;
import com.example.myfirstwhatsapp.semister.FifthSemActivity;

import java.util.Arrays;
import java.util.List;

public class SemisterSelectionActivity extends AppCompatActivity {

    int image [] =
            {
                    R.mipmap.one , R.mipmap.two ,
                    R.mipmap.three , R.mipmap.four ,
                    R.mipmap.five , R.mipmap.six ,
                    R.mipmap.seven , R.mipmap.eight
            };

    List<String> list = Arrays.asList("Semister 1 ","Semister 2 ",
            "Semister 3 ","Semister 4 "
            ,"Semister 5 ","Semister 6 ",
            "Semister 7 ","Semister 8 ");

    private RecyclerView myRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageAdapter adapter;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semister_selection);


        mToolbar = (Toolbar)findViewById(R.id.id_actBar_Ss);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Semisters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        initialization();
        addTheImagesToTheRecycler();

    }

    private void initialization() {
        myRecyclerView = (RecyclerView)findViewById(R.id.id_recyclerView);
        layoutManager = new GridLayoutManager(this ,2);
        adapter = new ImageAdapter(image , list);
    }

    private void addTheImagesToTheRecycler() {

        myRecyclerView.setLayoutManager(layoutManager);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setAdapter(adapter);
    }
//    private void sendUserToFifthSem(String itemName) {
//        Toast.makeText(this,
//                "welcome to 5th sem", Toast.LENGTH_SHORT).show();
//        sendUserToFifthSemActivity(itemName);
//    }
//
//    private void sendUserToFifthSemActivity(String itemName) {
//
//        Intent obj = new Intent(getApplicationContext() , FifthSemActivity.class);
//        obj.putExtra("semName" , itemName);
//        startActivity(obj);
//
//    }
}
