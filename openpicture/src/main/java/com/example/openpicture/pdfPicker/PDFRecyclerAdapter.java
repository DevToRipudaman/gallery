package com.example.openpicture.pdfPicker;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.openpicture.R;
import com.example.openpicture.galleryPicker.model.PdfData;

import java.util.ArrayList;

public class PDFRecyclerAdapter extends RecyclerView.Adapter<PDFRecyclerAdapter.ViewHolder> {


    ArrayList<PdfData> pdf;
    PDFActivity activity;

    public PDFRecyclerAdapter(ArrayList<PdfData> data, PDFActivity pdfActivity) {
        this.pdf = data;
        this.activity = pdfActivity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pdf_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPdf.Companion.start(activity, getPDFPath(Uri.parse(pdf.get(position).getPhotoUri())), "PDF");
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdf.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        ImageView imageView = itemView.findViewById(R.id.imageRC);
    }

    public String getPDFPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
