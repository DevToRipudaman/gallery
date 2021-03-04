package com.example.openpicture.pdfPicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.openpicture.R
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.shockwave.pdfium.PdfDocument
import kotlinx.android.synthetic.main.activity_open_pdf.*
import java.io.File

class OpenPdf :AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
         OnPageErrorListener {
    private val TAG = "OpenPdf"
    var pageNumber = 0
    var pdfFileName: String? = null


    companion object {
        private val EXTRA_PDF_URL = "extra.pdf.url"
        private val EXTRA_PDF_Name = "extra.pdf.pdf"
         fun start(context: Context, url: String, mediaName: String) {
            var bundle = Bundle()
            bundle.putString(EXTRA_PDF_URL, url)
            bundle.putString(EXTRA_PDF_Name, mediaName)
            context.startActivity(Intent(context, OpenPdf::class.java).apply {
                putExtras(bundle)
            })

        }
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_pdf)
        val pdfUrl = intent?.getStringExtra(EXTRA_PDF_URL)
        val pdfName = intent?.getStringExtra(EXTRA_PDF_Name)
        tvPdf.text = pdfName
        ivBack.setOnClickListener {
            onBackPressed()
        }
        try {

            pdfView.fromUri(Uri.fromFile(File(pdfUrl)))
                    .onPageChange(this)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle( DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .onPageError(this)
                    .load()

        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            Log.d(TAG, "onCreate: EXCEPTION: " + e?.toString())
            Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    override fun loadComplete(nbPages: Int) {
        val meta = pdfView.documentMeta
        printBookmarksTree(pdfView.tableOfContents, "-")
    }

    override fun onPageError(page: Int, t: Throwable?) {
        Log.e(TAG, "Cannot load page " + page);
    }
    fun printBookmarksTree(tree: List<PdfDocument.Bookmark>, sep: String) {
        for (b in tree) {
            Log.e(TAG, java.lang.String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()))
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), "$sep-")
            }
        }
    }
}