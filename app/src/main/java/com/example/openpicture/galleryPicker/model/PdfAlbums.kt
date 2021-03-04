package com.example.openpicture.galleryPicker.model

    data class PdfAlbums(var id: Int = 0,
                         var name: String = "",
                         var coverUri: String = "",
                         var albumPhotos: ArrayList<PdfData> = ArrayList())
