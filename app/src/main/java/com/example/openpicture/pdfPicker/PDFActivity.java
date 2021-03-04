package com.example.openpicture.pdfPicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import com.example.openpicture.PDFRecyclerAdapter2;
import com.example.openpicture.R;
import com.example.openpicture.galleryPicker.model.PdfData;
import com.example.openpicture.galleryPicker.view.adapters.PdfGridAdapter;

import java.util.ArrayList;

public class PDFActivity extends AppCompatActivity {


    RecyclerView view;
    ArrayList<PdfData> pdfData;
    ArrayList<String>myData;
    TextView selected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f);
        selected = findViewById(R.id.tvSelectedItems);
        view = findViewById(R.id.recyclerView);
        Intent intentParent = getIntent();

        if (getIntent().hasExtra("PdfFiles")){
            pdfData = intentParent.getParcelableArrayListExtra("PdfFiles");
            view.setAdapter(new PDFRecyclerAdapter(pdfData,this));
            selected.setText("Selected " + pdfData.size());
        }else if (getIntent().hasExtra(("MY_PDF_FLES"))){
                myData = intentParent.getStringArrayListExtra("MY_PDF_FLES");
                view.setAdapter(new PDFRecyclerAdapter2(myData,this));
            selected.setText("Selected " + myData.size());
            }
        }
    }

