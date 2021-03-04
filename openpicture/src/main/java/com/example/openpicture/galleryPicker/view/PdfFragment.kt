package com.example.openpicture.galleryPicker.view

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openpicture.R
import com.example.openpicture.galleryPicker.model.PdfAlbums
import com.example.openpicture.galleryPicker.model.PdfData
import com.example.openpicture.galleryPicker.model.PdfPresenterImpl
import com.example.openpicture.galleryPicker.utils.MLog
import com.example.openpicture.galleryPicker.utils.RunOnUiThread
import com.example.openpicture.galleryPicker.utils.font.FontsConstants
import com.example.openpicture.galleryPicker.utils.font.FontsManager
import com.example.openpicture.galleryPicker.utils.keypad.HideKeypad
import com.example.openpicture.galleryPicker.view.adapters.OnPDFObtained
import com.example.openpicture.galleryPicker.view.adapters.PdfAlbumAdapter
import com.example.openpicture.galleryPicker.view.adapters.PdfGridAdapter
import kotlinx.android.synthetic.main.activity_picker.*
import kotlinx.android.synthetic.main.fragment_media.*
import kotlinx.android.synthetic.main.fragment_media.albumsrecyclerview
import kotlinx.android.synthetic.main.fragment_media.allowAccessFrame
import kotlinx.android.synthetic.main.fragment_media.imageGrid
import kotlinx.android.synthetic.main.fragment_pdf.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.jetbrains.anko.doAsync
import java.io.File
import kotlinx.android.synthetic.main.activity_picker.toolbar as toolbar1

class PdfFragment : Fragment(),PdfPickerContract{

    val pdfPresenterImpl: PdfPresenterImpl = PdfPresenterImpl(this)
    var photoList: ArrayList<PdfData> = ArrayList()
    var albumList: ArrayList<PdfAlbums> = ArrayList()
    lateinit var glm: GridLayoutManager
    var photoids: ArrayList<Int> = ArrayList()
    lateinit var listener: OnPDFObtained
    private val PERMISSIONS_READ_WRITE = 123
    lateinit var ctx: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        ctx = inflater.context
        return inflater.inflate(R.layout.fragment_media, container, false)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        allowAccessButton.outlineProvider = ViewOutlineProvider.BACKGROUND

        initViews()

//        allowAccessButton.setOnClickListener {
            if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
//        }

//        if (activity != null) HideKeypad().hideKeyboard(activity!!)
//        backFrame.setOnClickListener { activity?.onBackPressed() }

        imageGrid.setPopUpTypeface(FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD))
//        galleryIllusTitle.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
//        galleryIllusContent.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
//        allowAccessButton.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
    }
    @TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN)
    fun checkReadWritePermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_READ_WRITE)
        return true
    }
    fun initViews() {
        photoList.clear()
        albumList.clear()
        photoids.clear()

        /*if (isReadWritePermitted())
            initGalleryViews()
        else allowAccessFrame.visibility = View.VISIBLE*/
    }
    fun initGalleryViews() {
        allowAccessFrame.visibility = View.GONE
        glm = GridLayoutManager(ctx, 4)
        imageGrid.itemAnimator = null
        val bundle = this.arguments
        if (bundle != null) photoids = if (bundle.containsKey("photoids")) bundle.getIntegerArrayList("photoids")!! else java.util.ArrayList()
        pdfOperation()
    }
    override fun initRecyclerViews() {
        albumsrecyclerview.layoutManager = LinearLayoutManager(ctx)
        albumsrecyclerview.adapter = PdfAlbumAdapter(java.util.ArrayList(), this)
        imageGrid.adapter = PdfGridAdapter(imageList = photoList, threshold = (ctx as PickerActivity).PDF_THRESHOLD)
    }

    override fun pdfOperation() {
        doAsync {
            albumList = java.util.ArrayList()
            listener = object : OnPDFObtained {
                override fun onComplete(albums: java.util.ArrayList<PdfAlbums>) {
                    albums.sortWith(compareBy { it.name })
                    for (album in albums) {
                        albumList.add(album)
                    }
                    albumList.add(0, PdfAlbums(0, "All PDF", albumPhotos = photoList))
                    photoList.sortWith(compareByDescending { File(it.photoUri).lastModified() })

                    for (id in photoids) {
                        for (image in photoList) {
                            if (id == image.id) image.isSelected = true
                        }
                    }

                    RunOnUiThread(ctx).safely {
                        imageGrid.layoutManager = glm
                        initRecyclerViews()
                        activity?.findViewById<TextView>(R.id.done)?.setOnClickListener {
                            val newList: ArrayList<PdfData> = ArrayList()
                            photoList.filterTo(newList) { it.isSelected && it.isEnabled }
                            val i = Intent()
                            i.putParcelableArrayListExtra("PDF", newList)
                            (ctx as PickerActivity).setResult((ctx as PickerActivity).REQUEST_RESULT_CODE, i)
                            (ctx as PickerActivity).onBackPressed()
                        }

                        /*albumselection.setOnClickListener {
                            toggleDropdown()
                        }
                        dropdownframe.setOnClickListener {
                            toggleDropdown()
                        }*/
                    }
                }

                override fun onError() {
                    MLog.e("CURSOR", "FAILED")
                }
            }

            doAsync {
                getPdfAlbums(ctx, listener)
            }
        }
    }

    override fun toggleDropdown() {
       /* dropdown.animate().rotationBy(0f).setDuration(300).setInterpolator(LinearInterpolator()).start()
        if ((albumsrecyclerview.adapter as PdfAlbumAdapter).malbumList.size == 0) {
            albumsrecyclerview.adapter = PdfAlbumAdapter(albumList, this)
            dropdown.setImageResource(R.drawable.ic_dropdown_rotate)
            try {
                done.isEnabled = false
                val animation = AnimationUtils.loadAnimation(ctx, R.anim.scale_down)
                done.startAnimation(animation)
            } catch (e: Exception) {
            }
            done.visibility = View.GONE
        } else {
            albumsrecyclerview.adapter = PdfAlbumAdapter(java.util.ArrayList(), this)
            dropdown.setImageResource(R.drawable.ic_dropdown)
            done.isEnabled = true
            done.visibility = View.VISIBLE
        }*/
    }

    override fun getPdfAlbums(context: Context, listener: OnPDFObtained) {
        pdfPresenterImpl.getPdfAlbums()
    }

    override fun updatePDFTitle(pdfAlbums: PdfAlbums) {
//        albumselection.text = pdfAlbums.name
    }

    override fun updatePDFSelectedPhotos(selectedlist: ArrayList<PdfData>) {
        for (selected in selectedlist) {
            for (photo in photoList) {
                photo.isSelected = selected.id == photo.id
                photo.isEnabled = selected.id == photo.id
            }
        }
    }
    private fun isReadWritePermitted(): Boolean = (context?.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && context?.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

}