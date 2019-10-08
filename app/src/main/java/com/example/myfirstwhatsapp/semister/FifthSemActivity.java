package com.example.myfirstwhatsapp.semister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfirstwhatsapp.R;
import com.example.myfirstwhatsapp.branches.CseBranchActivity2;

public class FifthSemActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String > adapter;
    private String[] section = {"CSE" , "ISE" , "MECH" , "CIV"};
    private Toolbar mToolbar;
    String semisterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth_sem);


        semisterName = getIntent().getStringExtra("semName");

        mToolbar = (Toolbar)findViewById(R.id.id_fif_sem_actBB);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(semisterName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        listView = (ListView)findViewById(R.id.id_fifth_sem_sections);

        adapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_1,
                android.R.id.text1 , section);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int pos = position;
                String item = (String)listView.getItemAtPosition(pos);



                switch (item)
                {
                    case "CSE":
                        sendUserToCseActivity(semisterName,item);
                        break;

                    case "ISE":
                        sendUserToCseActivity(semisterName,item);
                        break;

                    case "MECH":
                        sendUserToCseActivity(semisterName,item);
                        break;

                    case "CIV":
                        sendUserToCseActivity(semisterName,item);
                        break;


                }
            }
        });
    }

    private void sendUserToCseActivity(String semisterName, String item) {

        Intent intent = new Intent(getApplicationContext() , CseBranchActivity2.class);
        intent.putExtra("branchName" , item);
        intent.putExtra("semisterName" , this.semisterName);
        startActivity(intent);
    }
}
