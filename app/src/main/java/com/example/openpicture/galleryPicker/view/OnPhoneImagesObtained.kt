package com.example.openpicture.galleryPicker.view

import com.example.openpicture.galleryPicker.model.GalleryAlbums
import kotlin.collections.ArrayList

interface OnPhoneImagesObtained {
    fun onComplete(albums: ArrayList<GalleryAlbums>)
    fun onError()
}
