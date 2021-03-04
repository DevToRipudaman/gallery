package com.example.openpicture.galleryPicker.model

import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.android.parcel.Parcelize

    @Parcelize
    data class PdfData(var id: Int = 0,
                       var albumName: String = "",
                       var photoUri: String = "",
                       var albumId: Int = 0,
                       var isSelected: Boolean = false,
                       var isEnabled: Boolean = true,
                       var mediaType: Int = MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT,
                       var duration: Int = 0, var dateAdded: String = "",
                       var thumbnail: String = "") : Parcelable
