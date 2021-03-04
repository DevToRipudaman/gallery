package com.example.openpicture.galleryPicker.view

import android.content.Context
import com.example.openpicture.galleryPicker.model.PdfAlbums
import com.example.openpicture.galleryPicker.model.PdfData
import com.example.openpicture.galleryPicker.view.adapters.OnPDFObtained

interface PdfPickerContract {

    fun initRecyclerViews()
    fun pdfOperation()
    fun toggleDropdown()
    fun getPdfAlbums(context: Context, listener: OnPDFObtained)
    fun updatePDFTitle(pdfAlbums: PdfAlbums = PdfAlbums())
    fun updatePDFSelectedPhotos(selectedlist: ArrayList<PdfData> = ArrayList())
}