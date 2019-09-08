package com.example.myfirstwhatsapp.branches;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfirstwhatsapp.R;
import com.example.myfirstwhatsapp.pdfviewer.PdfViewerActivity;
import com.example.myfirstwhatsapp.staticclasses.UploadPDF;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CseBranchActivity extends AppCompatActivity {


    private EditText pdfFileName;
    private Button uploadPdfBtn , cancelButton;
    StorageReference storageRootReference;
    DatabaseReference rootRef , pdfRef;

    private Uri pdfUri;
    private Uri pdfDownloadUrl;
    private ProgressDialog mDialog;
    private String unikey;
    private Toolbar mToolbar;
    private ListView mListView;

    private ArrayAdapter<String> mAdapter;
    private ArrayList<UploadPDF> myArrayList = new ArrayList<>();
    private ArrayList myUniqueKeys = new ArrayList();

    private String[]fileName;
    private String  userTypeFileName2;
    private String branchName,semisterName , currentUserId;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cse_branch);

        initialize();

        uploadPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String myFileName = pdfFileName.getText().toString().trim();
               if(TextUtils.isEmpty(myFileName))
               {
                   pdfFileName.setError("must enter  a  name");
                   return;
               }
               else
               {
                   userTypeFileName2 = myFileName;
                   selectPdfFile();
               }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pdfFileName.setVisibility(View.GONE);
                uploadPdfBtn.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
            }
        });

          viewAllFiles();

          mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                  Toast.makeText(CseBranchActivity.this,
                          "you clicked "+ mListView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

                  String randomKey = myUniqueKeys.get(position).toString();
                  System.out.println("your id is  : "+  randomKey);
                  sendUserToPdfActivity(randomKey);
              }
          });



    }

    private void sendUserToPdfActivity(String randomKey) {

        Intent intent = new Intent(getApplicationContext() , PdfViewerActivity.class);
        intent.putExtra("key" , randomKey );
        startActivity(intent);
    }

    private void viewAllFiles() {

        pdfRef.keepSynced(true);

        pdfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    UploadPDF obj = snapshot.getValue(UploadPDF.class);
                    myUniqueKeys.add(snapshot.getKey());
                    myArrayList.add(obj);
                }

                fileName = new String[myArrayList.size()];

                for(int i=0;i<fileName.length;i++){
                    fileName[i] = myArrayList.get(i).getName();
                }


                mAdapter = new ArrayAdapter<String>(getApplicationContext() , android.R.layout.simple_list_item_1,
                     android.R.id.text1   ,fileName);
                mListView.setAdapter(mAdapter);
                // mAdapter.notifyDataSetChanged();

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
        startActivityForResult(Intent.createChooser(intent , "Select Pdf files") , 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data.getData()!=null && data!=null)
        {
            pdfUri = data.getData();
            uploadPdfFile(pdfUri  );
        }

    }

    private void uploadPdfFile(Uri pdfUri) {

        StorageReference reference = storageRootReference.child("PDF").child("Fifth Sem").child("CSE")
                .child(System.currentTimeMillis() +".pdf");

        reference.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete());

                pdfDownloadUrl = uri.getResult();
                unikey = pdfRef.push().getKey();

                UploadPDF uploadPDF = new UploadPDF(userTypeFileName2 , pdfDownloadUrl.toString() , unikey);

                pdfRef.child(unikey).setValue(uploadPDF);

                Toast.makeText(CseBranchActivity.this,
                        "opening..", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                uploadPdfBtn.setVisibility(View.GONE);
                pdfFileName.setVisibility(View.GONE);

                mDialog.setTitle("Loading...");
                mDialog.show();

                double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();

                mDialog.setMessage("uploaded :"+ (int) progress+" %");

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.branch_menu , menu);
          return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.id_branch_menu_upload_file:


                if(currentUserId.equals("EHEMXw7afHWxtlKWUsk6Xfire6g1"))
                {
                    pdfFileName.setVisibility(View.VISIBLE);
                    uploadPdfBtn.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(getApplicationContext(), "you" +
                            "don't have permmission", Toast.LENGTH_SHORT).show();

                break;
        }
        return true;
    }



    private void initialize() {

        branchName = getIntent().getStringExtra("branchName");
        semisterName = getIntent().getStringExtra("semisterName");

        mToolbar = (Toolbar)findViewById(R.id.id_bbb);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(branchName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        storageRootReference = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();
        pdfRef = rootRef.child("PDF").child("Fifth Sem").child("CSE");
        mDialog = new ProgressDialog(this);

        mListView = (ListView)findViewById(R.id.id_selct_subj);

        pdfFileName = (EditText)findViewById(R.id.id_fileName);
        uploadPdfBtn = (Button)findViewById(R.id.id_upload_pdf_file);
        cancelButton = (Button)findViewById(R.id.id_cancel_upload_pdf_file);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        System.out.println("the current user id is :" + currentUserId);
    }
}