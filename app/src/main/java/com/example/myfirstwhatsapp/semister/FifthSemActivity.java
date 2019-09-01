package com.example.myfirstwhatsapp.semister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfirstwhatsapp.R;
import com.example.myfirstwhatsapp.branches.CseBranchActivity;

import java.util.List;

public class FifthSemActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String > adapter;
    private String[] section = {"CSE" , "ISE" , "MECH" , "CIV"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth_sem);

        listView = (ListView)findViewById(R.id.id_fifth_sem_sections);

        adapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_1,
                android.R.id.text1 , section);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int pos = position;
                String item = (String)listView.getItemAtPosition(pos);

                if(item.equals("CSE"))
                {
                      sendUserToCseActivity();
                }
                else {
                    Toast.makeText(FifthSemActivity.this,
                            item, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserToCseActivity() {

        Intent intent = new Intent(getApplicationContext() , CseBranchActivity.class);
        startActivity(intent);
    }
}
