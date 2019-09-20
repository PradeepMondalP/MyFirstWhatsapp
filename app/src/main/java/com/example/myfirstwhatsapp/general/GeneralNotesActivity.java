package com.example.myfirstwhatsapp.general;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfirstwhatsapp.MainActivity;
import com.example.myfirstwhatsapp.R;
import com.example.myfirstwhatsapp.branches.CseBranchActivity2;
import com.example.myfirstwhatsapp.pdfviewer.PdfViewerActivity2;
import com.example.myfirstwhatsapp.staticclasses.UploadPdf2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class GeneralNotesActivity extends AppCompatActivity {

    private ListView listView;
    private Toolbar mToolbar;
    String subjectName , semisterName , branchName , type;
    private EditText fileName;
    private Button saveBtn , cancelBtn;

    // firebase related
    private DatabaseReference rootRef , myPdfFileRef;
    private StorageReference rootRefStorage , filePath;
    private FirebaseAuth mAuth;
    private String currentUserId;


    // general
    private String userTypedFileName , pdfDownloadUrl;
    private Uri pdfUri ;
    private ProgressDialog mDialog;

    // for retreiving the files
    ArrayList<UploadPdf2> myArrayList = new ArrayList<>();
    ArrayList<String> myKeys = new ArrayList<>();
    String [] moduleNames;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_notes);

        initialize();

        viewAllTheData();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            final   String name = fileName.getText().toString().trim();
                if(TextUtils.isEmpty(name))
                {
                    fileName.setError("must enter  a  name");
                    return;
                }
                else
                {
                    userTypedFileName = name;
                    System.out.println("selected name is "+ userTypedFileName);
                    selectPdfFile();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName.setVisibility(View.GONE);
                saveBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = moduleNames[position];
                System.out.println("you clicked on :"+ item );

                sendUserToPdfViewActivity2(item , type);
            }
        });

    }

    private void sendUserToPdfViewActivity2(String item , String type) {
        Intent intent = new Intent(getApplicationContext() , PdfViewerActivity2.class);
        intent.putExtra("semisterName" , semisterName);
        intent.putExtra("branchName", branchName);
        intent.putExtra("type", type);
        intent.putExtra("subjectName" , subjectName);
        intent.putExtra("moduleName", item);
        startActivity(intent);


    }

    private void viewAllTheData() {

        myPdfFileRef.keepSynced(true);
        myPdfFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                     myKeys.add(snapshot.getKey());
                     UploadPdf2 obj = snapshot.getValue(UploadPdf2.class);
                     myArrayList.add(obj);
                }

                moduleNames = new String[myKeys.size()];

                for(int i=0;i<moduleNames.length ; i++)
                      moduleNames[i] = myKeys.get(i);

                adapter = new ArrayAdapter<String>(getApplicationContext() , android.R.layout.simple_list_item_1
                ,android.R.id.text1 ,moduleNames);

                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void selectPdfFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent , "Select Pdf files") , 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2 && resultCode==RESULT_OK && data.getData()!=null && data!=null)
        {
            pdfUri = data.getData();
            System.out.println("inside onActivityResult  109");
            uploadPdfFile(pdfUri  );
        }

    }

    private void initialize() {

        subjectName = getIntent().getStringExtra("subjectName");
        semisterName = getIntent().getStringExtra("semisterName");
        branchName = getIntent().getStringExtra("branchName");
        type = getIntent().getStringExtra("type");

        mToolbar = (Toolbar)findViewById(R.id.id_general_notes_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(semisterName+" -> "+branchName+" -> "+subjectName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView)findViewById(R.id.id_genral_notes_list_view);
        saveBtn = (Button)findViewById(R.id.id_upload_pdf_file_x);
        cancelBtn = (Button)findViewById(R.id.id_cancel_upload_pdf_file_x);
        fileName = (EditText)findViewById(R.id.id_fileName_2);

        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRefStorage = FirebaseStorage.getInstance().getReference();
        mAuth =FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mDialog = new ProgressDialog(this);

          filePath = rootRefStorage.child("All files").
                child(semisterName).child(branchName).child(subjectName).child(type)
                .child(System.currentTimeMillis() +".pdf");

          myPdfFileRef = rootRef.child("All files").child(semisterName)
                .child(branchName).child(type).child(subjectName);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.branch_menu , menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.id_branch_menu_upload_file:
                if(currentUserId.equals("EHEMXw7afHWxtlKWUsk6Xfire6g1"))
                {
                    fileName.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(getApplicationContext(), "you" +
                            "don't have permmission", Toast.LENGTH_SHORT).show();

                break;
        }
        return true ;
    }

    private void uploadPdfFile(final Uri pdfUri) {

      mDialog.setMessage("please wait");
      mDialog.setCanceledOnTouchOutside(false);

        filePath.putFile(pdfUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(GeneralNotesActivity.this,
                            "saved to storage..", Toast.LENGTH_SHORT).show();

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            pdfDownloadUrl = uri.toString();
                            System.out.println("download uri is "+ pdfDownloadUrl);

                             UploadPdf2 obj = new UploadPdf2(pdfDownloadUrl);

                            DatabaseReference temp = myPdfFileRef.child(userTypedFileName);

                            temp.setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(GeneralNotesActivity.this,
                                                "uploaded", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                        sendUserToMainActivity();
                                    }
                                   else
                                    {
                                        Toast.makeText(GeneralNotesActivity.this,
                                                "error...", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                saveBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                fileName.setVisibility(View.GONE);

                mDialog.setTitle("Loading...");
                mDialog.show();

                double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();

                mDialog.setMessage("uploaded :"+ (int) progress+" %");

            }
        });

        }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
        startActivity(intent);
        finish();
    }
}
