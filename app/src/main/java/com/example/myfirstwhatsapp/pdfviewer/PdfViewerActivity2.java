package com.example.myfirstwhatsapp.pdfviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfirstwhatsapp.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfViewerActivity2 extends AppCompatActivity {

    private PDFView myPdfView;
    private String myUrl , semisterName , branchName , subjectName , moduleName , type;
    private TextView textView;
    private Toolbar mToolbar;

    // firebase related
    DatabaseReference rootRef , notesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer2);



        initialize();

        showPdfFile();
    }

    private void showPdfFile() {

        notesRef.keepSynced(true);

        notesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("url"))
                {
                    String url = dataSnapshot.child("url").getValue().toString();

                    Toast.makeText(PdfViewerActivity2.this,
                            "its loading please wait for a second...", Toast.LENGTH_LONG).show();
                    new RetreivePDFStream2().execute(url);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialize() {

        myPdfView = (PDFView)findViewById(R.id.id_pdf_view2);
        textView = (TextView)findViewById(R.id.id_text_1);

        semisterName = getIntent().getStringExtra("semisterName");
        branchName = getIntent().getStringExtra("branchName");
        subjectName=getIntent().getStringExtra("subjectName");
        moduleName = getIntent().getStringExtra("moduleName");
        type = getIntent().getStringExtra("type");

        mToolbar = (Toolbar)findViewById(R.id.id_pdf_view_2_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(moduleName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rootRef = FirebaseDatabase.getInstance().getReference();

        notesRef = rootRef.child("All files").child(semisterName).child(branchName).child(type)
                 .child(subjectName).child(moduleName);



    }



    public class RetreivePDFStream2 extends AsyncTask<String , Void , InputStream>
    {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream stream = null;
            try {
                URL url = new URL (strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

                if(urlConnection.getResponseCode()==200)
                {
                    stream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }
            catch (IOException e)
            {
                return null;
            }
            return stream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            myPdfView.fromStream(inputStream).load();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_menu , menu);
        return true ;
    }
}
