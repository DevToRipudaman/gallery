package com.example.openpicture.galleryPicker.view

import android.content.Context
import com.example.openpicture.galleryPicker.model.GalleryAlbums
import com.example.openpicture.galleryPicker.model.GalleryData
import kotlin.collections.ArrayList

interface ImagePickerContract {
    fun initRecyclerViews()
    fun galleryOperation()
    fun toggleDropdown()
    fun getPhoneAlbums(context: Context, listener: OnPhoneImagesObtained)
    fun updateTitle(galleryAlbums: GalleryAlbums = GalleryAlbums())
    fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData> = ArrayList())
}