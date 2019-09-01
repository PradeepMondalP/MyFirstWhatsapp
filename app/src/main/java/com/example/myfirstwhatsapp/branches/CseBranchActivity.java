package com.example.myfirstwhatsapp.branches;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myfirstwhatsapp.R;
import com.example.myfirstwhatsapp.staticclasses.UploadPDF;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CseBranchActivity extends AppCompatActivity {

    private ImageButton myPDFfile;
    private EditText pdfDescripTion;
    private Button savePdfButtn;
    StorageReference storageRootReference;
    DatabaseReference rootRef , pdfRef;

    private Uri pdfUri;
    private Uri pdfDownloadUrl;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cse_branch);

       initialize();

       savePdfButtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               selectPdfFile();
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

                UploadPDF uploadPDF = new UploadPDF(pdfDescripTion.getText().toString() , pdfDownloadUrl.toString());

                pdfRef.child(pdfRef.push().getKey()).setValue(uploadPDF);

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

    private void initialize() {
        myPDFfile = (ImageButton)findViewById(R.id.id_posting_image);
        pdfDescripTion = (EditText)findViewById(R.id.id_posting_image_descrip);
        savePdfButtn = (Button)findViewById(R.id.id_post_the_image_button);

        storageRootReference = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();
        pdfRef = FirebaseDatabase.getInstance().getReference().child("PDF");
        mDialog = new ProgressDialog(this);
    }
}
