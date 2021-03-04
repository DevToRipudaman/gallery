package com.example.openpicture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.openpicture.galleryPicker.model.PdfData
import com.example.openpicture.pdfPicker.OpenPdf.Companion.start
import com.example.openpicture.pdfPicker.PDFActivity
import java.io.File
import java.io.FileOutputStream
import java.util.*

class PDFRecyclerAdapter2(var pdf: ArrayList<String>, var activity: PDFActivity ) : RecyclerView.Adapter<PDFRecyclerAdapter2.ViewHolder>() {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_pdf_view, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = getFile(activity, Uri.parse(pdf[position]))
        holder.imageView.setOnClickListener { start(activity, file.path, "PDF") }

    }

    override fun getItemCount(): Int {
        return pdf.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.findViewById<ImageView>(R.id.imageRC)
    }

    fun getFile(mContext: Context?, documentUri: Uri): File {
        val inputStream = mContext?.contentResolver?.openInputStream(documentUri)
        var file =  File("")
        inputStream.use { input ->
            file =
                    File(mContext?.cacheDir, System.currentTimeMillis().toString() + ".pdf")
            FileOutputStream(file).use { output ->
                val buffer =
                        ByteArray(4 * 1024) // or other buffer size
                var read: Int = -1
                while (input?.read(buffer).also {
                            if (it != null) {
                                read = it
                            }
                        } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        return file
    }

}