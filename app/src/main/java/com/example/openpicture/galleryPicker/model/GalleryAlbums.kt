package com.example.openpicture.galleryPicker.model

import kotlin.collections.ArrayList

data class GalleryAlbums(var id: Int = 0, var name: String = "", var coverUri: String = "", var albumPhotos: ArrayList<GalleryData> = ArrayList())