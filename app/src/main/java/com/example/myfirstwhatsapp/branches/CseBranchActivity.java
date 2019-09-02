package com.example.myfirstwhatsapp.branches;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    private ImageButton myPDFfile;
    private EditText pdfDescripTion;
    private Button savePdfButtn;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cse_branch);

        initialize();

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
        intent.putExtra("key" , randomKey);
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
                for(int i=0;i<fileName.length;i++)
                    fileName[i] = myArrayList.get(i).getName();

                mAdapter = new ArrayAdapter<String>(getApplicationContext() , android.R.layout.activity_list_item,
                     android.R.id.text1   ,fileName);
                mListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

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
            uploadPdfFile(pdfUri);
        }

    }

    private void uploadPdfFile(Uri pdfUri) {

        StorageReference reference = storageRootReference.child("Files").child("pdf")
                .child(System.currentTimeMillis() +".pdf");

        reference.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete());

                pdfDownloadUrl = uri.getResult();
                unikey = pdfRef.push().getKey();

                UploadPDF uploadPDF = new UploadPDF("Bye" , pdfDownloadUrl.toString() , unikey);

                pdfRef.child(unikey).setValue(uploadPDF);

                Toast.makeText(CseBranchActivity.this,
                        "file uploaded ", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

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
                selectPdfFile();
                break;


        }
        return true;
    }

    private void initialize() {


        storageRootReference = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();
        pdfRef = FirebaseDatabase.getInstance().getReference().child("PDF");
        mDialog = new ProgressDialog(this);

        mToolbar = (Toolbar)findViewById(R.id.id_branch_toolbar);
        setSupportActionBar(mToolbar);

        mListView = (ListView)findViewById(R.id.id_selct_subj);
    }
}