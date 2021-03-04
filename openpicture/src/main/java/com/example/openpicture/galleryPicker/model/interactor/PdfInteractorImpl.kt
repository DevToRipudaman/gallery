package com.example.openpicture.galleryPicker.model.interactor

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.openpicture.galleryPicker.model.*
import com.example.openpicture.galleryPicker.utils.MLog
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class PdfInteractorImpl(var presenter: PdfPresenterImpl) : PdfInteractor {

    lateinit var ctx: Context

    private fun getThumbnailPath(id: Long): String? {
        var result: String? = null
        val cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(presenter.pdfFragment.ctx.contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA))
            cursor.close()
        }
        return result
    }

   /* override fun getPdfAlbums() {
        val galleryAlbums: ArrayList<PdfAlbums> = ArrayList()
        val albumsNames: ArrayList<String> = ArrayList()
        val cr: ContentResolver = presenter.pdfFragment.ctx.contentResolver

        val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                )

        val mimeType = "application/pdf"

        val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + mimeType + "')"  *//*(MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + R.attr.mimeType + "')"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd%'")*//*
        val orderBy = MediaStore.Files.FileColumns.SIZE + " DESC"
        val cursor: Cursor? = cr.query(MediaStore.Files.getContentUri("external"),
                projection,
                whereClause,
                null,
                orderBy)

        MLog.e("PDFs", cursor?.count.toString())

        try {
            if (cursor != null && cursor.count > 0) {
                if (cursor.moveToFirst()) {

                    do {

                        val idColumn: Int = cursor.getColumnIndex(projection[0])
                        val fileId: Long = cursor.getLong(idColumn)
                        val fileUri: Uri = Uri.parse("$cursor/$fileId")
                        val id = cursor.getString(idColumn)

                        val galleryData = PdfData()
                        galleryData.photoUri = fileUri.toString()
                        galleryData.id = Integer.valueOf(id)
                        galleryData.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT

                        if (albumsNames.contains(galleryData.albumName)) {
                            for (album in galleryAlbums) {
                                if (album.name == galleryData.albumName) {
                                    galleryData.albumId = album.id
                                    album.albumPhotos.add(galleryData)
                                    presenter.pdfFragment.photoList.add(galleryData)
                                    break
                                }
                            }
                        } else {
                            val album = PdfAlbums()
                            album.id = galleryData.id
                            galleryData.albumId = galleryData.id
                            album.name = galleryData.albumName
                            album.coverUri = galleryData.photoUri
                            album.albumPhotos.add(galleryData)
                            presenter.pdfFragment.photoList.add(galleryData)
                            galleryAlbums.add(album)
                            albumsNames.add(galleryData.albumName)
                        }
                    } while (cursor.moveToNext())
                }
                cursor.close()
            } else presenter.pdfFragment.listener.onError()
        } catch (e: Exception) {
            MLog.e("PDF PICKER", e.toString())
        } finally {
            presenter.pdfFragment.listener.onComplete(galleryAlbums)
        }
    }
*/


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun getPdfAlbums() {
        val pdfExt = "_data LIKE '%.pdf'"
        val cr: ContentResolver = presenter.pdfFragment.ctx.contentResolver
        val galleryAlbums: ArrayList<PdfAlbums> = ArrayList()
        val albumsNames: ArrayList<String> = ArrayList()
//MediaStore.Files.FileColumns.DATA,MediaStore.Images.Media.SIZE,MediaStore.Files.FileColumns.MIME_TYPE
        val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.RELATIVE_PATH)
        val uri = MediaStore.Files.getContentUri("external")
//        uri=Uri.parse(copyFileToInternalStorage(uri, "RiDevi", presenter.pdfFragment.ctx))

//        val inputStream = presenter.pdfFragment.ctx.contentResolver.openInputStream(uri)

        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE)
        val selectionArgs: Array<String>? = null
        val selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
        val mimeType: String = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf").toString()
        val selectionArgsPdf = arrayOf(mimeType)

        val cursor: Cursor? = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, null)



//        Handler(Looper.getMainLooper()).postDelayed(Runnable {
//            Toast.makeText(presenter.pdfFragment.ctx, copyFileToInternalStorage(uri, "RiDevi", presenter.pdfFragment.ctx), Toast.LENGTH_LONG).show()
//
//        }, 3000)

        MLog.e("PDF", cursor?.count.toString())

        try {
            if (cursor != null && cursor.count > 0) {
                if (cursor.moveToFirst()) {

//
//                      val dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
//                      val dateAddedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
//                      val titleColumn = cursor.getColumnIndex(MediaStore.Images.Media.TITLE)
                    do {

                        val idColumn: Int = cursor.getColumnIndex(projection[0])
                        Log.e(ContentValues.TAG, "getPdfAlbums: " + idColumn)
                        val fileId: Long = cursor.getLong(idColumn)
                        val fileUri: Uri = Uri.parse("$uri/$fileId")

                        val displayName: String = cursor.getString(cursor.getColumnIndex(projection[1]))
                        Log.e("getPdfAlbums: ", displayName)
                        val id = cursor.getString(idColumn)
//                              val data = cursor.getString(dataColumn)
//                              val dateAdded = cursor.getString(dateAddedColumn)
//                              val title = cursor.getString(titleColumn)
                        val galleryData = PdfData()


//                        galleryData.albumName = File(data).parentFile.name

                        galleryData.photoUri = fileUri.toString()
                        galleryData.id = Integer.valueOf(id)
                        galleryData.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT

//                        galleryData.dateAdded = dateAdded

//                        galleryData.thumbnail = getThumbnailPath(galleryData.id.toLong()) ?: ""
//                        if (galleryData.thumbnail.isNotEmpty()) {
                        if (albumsNames.contains(galleryData.albumName)) {
                            for (album in galleryAlbums) {
                                if (album.name == galleryData.albumName) {
                                    galleryData.albumId = album.id
                                    album.albumPhotos.add(galleryData)
                                    presenter.pdfFragment.photoList.add(galleryData)
                                    break
                                }
                            }
                        } else {
                            val album = PdfAlbums()
                            album.id = galleryData.id
                            galleryData.albumId = galleryData.id
                            album.name = galleryData.albumName
                            album.coverUri = galleryData.photoUri
                            album.albumPhotos.add(galleryData)
                            presenter.pdfFragment.photoList.add(galleryData)
                            galleryAlbums.add(album)
                            albumsNames.add(galleryData.albumName)
                        }
//                        }
                    } while (cursor.moveToNext())
                }
                cursor.close()
            } else presenter.pdfFragment.listener.onError()
        } catch (e: Exception) {
            MLog.e("PDF PICKER", e.toString())
        } finally {
            presenter.pdfFragment.listener.onComplete(galleryAlbums)
        }
    }

    private fun copyFileToInternalStorage(uri: Uri, newDirName: String, ctx: Context): String? {
        val returnCursor: Cursor = ctx.getContentResolver().query(uri, arrayOf<String>(
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        ), null, null, null)!!

        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val output: File
        if (newDirName != "") {
            val dir: File = File(presenter.pdfFragment.ctx.getFilesDir().toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            output = File(presenter.pdfFragment.ctx.getFilesDir().toString() + "/" + newDirName + "/" + name)
        } else {
            output = File(presenter.pdfFragment.ctx.getFilesDir().toString() + "/" + name)
        }
        try {
            val inputStream: InputStream = ctx.getContentResolver().openInputStream(uri)!!
            val outputStream = FileOutputStream(output)
            var read = 0
            val bufferSize = 1024
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return output.path
    }
}