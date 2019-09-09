package com.example.myfirstwhatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.example.myfirstwhatsapp.semister.FifthSemActivity;

public class SemisterSelectionActivity extends AppCompatActivity {

    private ListView listView;
    ArrayAdapter<String> adapter;
    String[] sem = {"Semister 1" , "Semister 2" ,"Semister 3" ,"Semister 4" ,"Semister 5"
            ,"Semister 6"  , "Semister 7", "Semister 8"  };
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semister_selection);

        listView = (ListView)findViewById(R.id.id_all_sem_list);

        mToolbar = (Toolbar)findViewById(R.id.id_actBar_Ss);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Semisters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapter = new ArrayAdapter<>
                (
                        this ,android.R.layout.simple_list_item_1,android.R.id.text1, sem
                );

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int pos =  position;
                String itemName = (String)listView.getItemAtPosition(pos);
                if(itemName.equals("Semister 5"))
                {
                    sendUserToFifthSem(itemName);
                }
                else
                {
                    Toast.makeText(SemisterSelectionActivity.this,
                            itemName, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserToFifthSem(String itemName) {
        Toast.makeText(this,
                "welcome to 5th sem", Toast.LENGTH_SHORT).show();

        sendUserToFifthSemActivity(itemName);
    }

    private void sendUserToFifthSemActivity(String itemName) {

        Intent obj = new Intent(getApplicationContext() , FifthSemActivity.class);
        obj.putExtra("semName" , itemName);
        startActivity(obj);

    }
}
