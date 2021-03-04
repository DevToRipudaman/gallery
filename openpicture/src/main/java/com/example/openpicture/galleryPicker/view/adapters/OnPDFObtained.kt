package com.example.openpicture.galleryPicker.view.adapters

import com.example.openpicture.galleryPicker.model.PdfAlbums

interface OnPDFObtained {
    fun onComplete(albums: ArrayList<PdfAlbums>)
    fun onError()
}