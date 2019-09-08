package com.example.myfirstwhatsapp.branches;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.myfirstwhatsapp.R;
import com.example.myfirstwhatsapp.semister.FifthSemActivity;

public class CseBranchActivity2 extends AppCompatActivity {
    private String branchName;
    private String semisterName;
    private Toolbar mToolbar;
    private ListView listView;
    private ArrayAdapter<String > adapter;
    private String[] section = {"Notes" , "Assignments" , "Books" , "QuestionPapers"};
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cse_branch2);

        initialize();
    }

    private void initialize() {

        branchName = getIntent().getStringExtra("branchName");
        semisterName = getIntent().getStringExtra("semisterName");

        mToolbar = (Toolbar)findViewById(R.id.id_bbb22);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(semisterName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView)findViewById(R.id.id_selct_subj);
        adapter = new ArrayAdapter<>(getApplicationContext()
                , android.R.layout.simple_list_item_1,
                android.R.id.text1 , section);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int pos = position;
                 item = section[pos];

                switch (item)
                {
                    case "Notes":
                         openPopupMenuForNotes(R.menu.fifth_sem_subject_menu);
                          break;

                    case "Assignments":
                        openPopupMenuForNotes(R.menu.assignments_menu);
                        break;

                    case "Books":
                        openPopupMenuForNotes(R.menu.fifth_sem_cs_books_menu);
                        break;

                    case "QuestionPapers":
                        Toast.makeText(CseBranchActivity2.this, "not yet " +
                                "developed", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


    private void openPopupMenuForNotes(int fifth_sem_subject_menu) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext() , listView);

        popupMenu.inflate(fifth_sem_subject_menu );



        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.id_atc:
                        Toast.makeText(CseBranchActivity2.this, "ATc", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_java:
                        Toast.makeText(CseBranchActivity2.this, "java", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_dbms:
                        Toast.makeText(CseBranchActivity2.this, "dbms", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_dot_net:
                        Toast.makeText(CseBranchActivity2.this, "dot net", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_mec:
                        Toast.makeText(CseBranchActivity2.this, "mec", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_cn:
                        Toast.makeText(CseBranchActivity2.this, "networking", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_assignment_1:
                        Toast.makeText(CseBranchActivity2.this, "AssignMent 1", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_assignment_2:
                        Toast.makeText(CseBranchActivity2.this, "assignMent 2", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_assignment_3:
                        Toast.makeText(CseBranchActivity2.this, "assignMent 3", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_atc_bk:
                        Toast.makeText(CseBranchActivity2.this, "ATc book", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_java_bk:
                        Toast.makeText(CseBranchActivity2.this, "java book", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_dbms_bk:
                        Toast.makeText(CseBranchActivity2.this, "dbms book", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_dot_net_bk:
                        Toast.makeText(CseBranchActivity2.this, "dot net book", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_mec_bk:
                        Toast.makeText(CseBranchActivity2.this, "mec book", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.id_cn_bk:
                        Toast.makeText(CseBranchActivity2.this, "networking book", Toast.LENGTH_SHORT).show();
                        break;


                }
                return true;
            }
        });
        popupMenu.setGravity(Gravity.FILL_VERTICAL);
        popupMenu.show();
    }
}
