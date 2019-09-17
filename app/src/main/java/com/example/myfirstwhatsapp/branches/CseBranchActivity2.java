package com.example.myfirstwhatsapp.branches;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.myfirstwhatsapp.R;
import com.example.myfirstwhatsapp.general.GeneralNotesActivity;

public class CseBranchActivity2 extends AppCompatActivity {
    private String branchName ,semisterName;
    private Button popupDisplayBtn;
    private Toolbar mToolbar;
    private ListView listView;
    private ArrayAdapter<String > adapter;
    private String[] section = {"Notes" , "Assignments" , "Books" , "QuestionPapers"};
    String item ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cse_branch2);

        initialize();
    }

    private void initialize() {

        branchName = getIntent().getStringExtra("branchName");
        semisterName = getIntent().getStringExtra("semisterName");
        popupDisplayBtn = (Button)findViewById(R.id.id_popup_btn);

        mToolbar = (Toolbar)findViewById(R.id.id_bbb22);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(semisterName +" -> " +branchName );
    //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   //     getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                         openPopupMenuForNotes(R.menu.fifth_sem_subject_menu , "Notes" );
                          break;

                    case "Assignments":
                        openPopupMenuForNotes(R.menu.assignments_menu , "Assignments" );
                        break;

                    case "Books":
                        openPopupMenuForNotes(R.menu.fifth_sem_cs_books_menu , "Books");
                        break;

                    case "QuestionPapers":
                        openPopupMenuForNotes(R.menu.fifth_sem_cs_books_menu , "QuestionPapers");
                        break;
                }
            }
        });
    }


    private void openPopupMenuForNotes(int fifth_sem_subject_menu  , final String typeOfData) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), popupDisplayBtn   );

        popupMenu.inflate(fifth_sem_subject_menu );

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.id_atc:
                         sendUserToGeneralNotesActivity(semisterName , branchName ,"Atc" , typeOfData);
                        break;

                    case R.id.id_java:
                        Toast.makeText(CseBranchActivity2.this, "java", Toast.LENGTH_SHORT).show();
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Java" , typeOfData);
                        break;

                    case R.id.id_dbms:
                        Toast.makeText(CseBranchActivity2.this, "dbms", Toast.LENGTH_SHORT).show();
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Dbms" , typeOfData);
                        break;

                    case R.id.id_dot_net:
                        Toast.makeText(CseBranchActivity2.this, "dot net", Toast.LENGTH_SHORT).show();
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Dot new" , typeOfData);
                        break;

                    case R.id.id_mec:
                        Toast.makeText(CseBranchActivity2.this, "mec", Toast.LENGTH_SHORT).show();
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Mec" , typeOfData);
                        break;

                    case R.id.id_cn:
                        message("Networking");
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Networking" , typeOfData);
                        break;

                    case R.id.id_assignment_1:
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Assignment 1" , typeOfData);
                        break;

                    case R.id.id_assignment_2:
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Assignment 2" , typeOfData);
                        break;

                    case R.id.id_assignment_3:
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Assignment 3" , typeOfData);
                        break;

                    case R.id.id_atc_bk:
                          message("Atc");
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Atc" , typeOfData);
                           break;

                    case R.id.id_java_bk:
                        message("Java");
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Java" , typeOfData);
                         break;

                    case R.id.id_dbms_bk:
                        message("Dbms");
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Dbms" , typeOfData);
                        break;

                    case R.id.id_dot_net_bk:
                        message("Dot net");
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Dot net" , typeOfData);
                        break;

                    case R.id.id_mec_bk:
                        message("Mec");
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Mec" , typeOfData);
                        break;

                    case R.id.id_cn_bk:
                        message("Networking");
                        sendUserToGeneralNotesActivity(semisterName , branchName ,"Networking" , typeOfData);
                        break;


                }
                return true;
            }
        });

        popupMenu.show();
    }

    public void message(String str)
    {
        Toast.makeText(getApplicationContext(), "" + str, Toast.LENGTH_SHORT).show();
    }


    private void sendUserToGeneralNotesActivity(String semisterName , String branchName, String subjectName , String type) {
        Intent intent = new Intent(getApplicationContext() , GeneralNotesActivity.class);
        intent.putExtra("subjectName", subjectName);
        intent.putExtra("semisterName" , semisterName);
        intent.putExtra("type" , type);
        intent.putExtra("branchName", branchName);
        startActivity(intent);

    }
}
