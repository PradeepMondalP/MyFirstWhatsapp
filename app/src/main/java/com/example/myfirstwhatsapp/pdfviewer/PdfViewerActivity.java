package com.example.myfirstwhatsapp.pdfviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfirstwhatsapp.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class PdfViewerActivity extends AppCompatActivity {

    private String uniKey;
    private TextView textView;
    private PDFView pdfView;
    private DatabaseReference pdfRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        Toast.makeText(this,
                "inside viewer activity", Toast.LENGTH_SHORT).show();
        initialize();

        showPdfFile();
    }

    private void showPdfFile() {

        pdfRef.keepSynced(true);
        pdfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String value = dataSnapshot.getValue(String.class);
                    textView.setText(value);
                    Toast.makeText(PdfViewerActivity.this,
                            "updated", Toast.LENGTH_SHORT).show();
                    String url = textView.getText().toString();

                    new RetreivePDFStream().execute(url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(PdfViewerActivity.this,
                        "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialize() {

        uniKey = getIntent().getStringExtra("key");
        System.out.println("received key is ="+ uniKey);


        textView = (TextView)findViewById(R.id.id_text1);
        pdfView = (PDFView)findViewById(R.id.id_pdf_view);
        pdfRef = FirebaseDatabase.getInstance().getReference().child("PDF").child("Fifth Sem").child("CSE")
                                 .child(uniKey).child("url");
    }

    public class RetreivePDFStream extends AsyncTask<String , Void , InputStream>
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
            pdfView.fromStream(inputStream).load();
        }
    }
}

