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
import com.example.openpicture.galleryPicker.model.GalleryAlbums
import com.example.openpicture.galleryPicker.model.GalleryData
import com.example.openpicture.galleryPicker.presenter.VideosPresenterImpl
import com.example.openpicture.galleryPicker.utils.MLog
import com.example.openpicture.galleryPicker.utils.RunOnUiThread
import com.example.openpicture.galleryPicker.utils.font.FontsConstants
import com.example.openpicture.galleryPicker.utils.font.FontsManager
import com.example.openpicture.galleryPicker.utils.keypad.HideKeypad
import com.example.openpicture.galleryPicker.view.adapters.AlbumAdapter
import com.example.openpicture.galleryPicker.view.adapters.VideoGridAdapter
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
import java.util.*
import kotlinx.android.synthetic.main.activity_picker.toolbar as toolbar1

class VideosFragment : Fragment(), ImagePickerContract {

    var photoList: ArrayList<GalleryData> = ArrayList()
    var albumList: ArrayList<GalleryAlbums> = ArrayList()
    lateinit var glm: GridLayoutManager
    var photoids: ArrayList<Int> = ArrayList()

    val imagePickerPresenter: VideosPresenterImpl = VideosPresenterImpl(this)

    lateinit var listener: OnPhoneImagesObtained

    private val PERMISSIONS_READ_WRITE = 123

    lateinit var ctx: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ctx = inflater.context
        return inflater.inflate(R.layout.fragment_media, container, false)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        /*allowAccessButton.outlineProvider = */ViewOutlineProvider.BACKGROUND

        imageGrid.setPopUpTypeface(FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD))

//        albumselection.text = "All Videos"

//        if (isReadWritePermitted()) initGalleryViews() else allowAccessFrame.visibility = View.VISIBLE


            if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()


//        if (activity != null) HideKeypad().hideKeyboard(activity!!)
//        backFrame.setOnClickListener { activity?.onBackPressed() }

//        galleryIllusTitle.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
//        galleryIllusContent.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
//        allowAccessButton.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
    }

    fun initGalleryViews() {
        allowAccessFrame.visibility = View.GONE
        glm = GridLayoutManager(ctx, 3)
        imageGrid.itemAnimator = null
        val bundle = this.arguments
        if (bundle != null) photoids = if (bundle.containsKey("photoids")) bundle.getIntegerArrayList("photoids")!! else ArrayList()
        galleryOperation()
    }

    override fun galleryOperation() {
        doAsync {
            albumList = ArrayList()
            listener = object : OnPhoneImagesObtained {
                override fun onComplete(albums: ArrayList<GalleryAlbums>) {
                    albums.sortWith(compareBy { it.name })
                    for (album in albums) {
                        albumList.add(album)
                    }
                    albumList.add(0, GalleryAlbums(0, "All Videos", albumPhotos = photoList))
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
                            val newList: ArrayList<GalleryData> = ArrayList()
                            photoList.filterTo(newList) { it.isSelected && it.isEnabled }
                            val i = Intent()
                            i.putParcelableArrayListExtra("MEDIA", newList)
                            (ctx as PickerActivity).setResult((ctx as PickerActivity).REQUEST_RESULT_CODE, i)
                            (ctx as PickerActivity).onBackPressed()
                        }
//                        albumselection.setOnClickListener {
//                            toggleDropdown()
//                        }
//                        dropdownframe.setOnClickListener {
//                            toggleDropdown()
//                        }
                    }
                }

                override fun onError() {
                    MLog.e("CURSOR", "FAILED")
                }
            }

            doAsync {
                getPhoneAlbums(ctx, listener)
            }
        }
    }

    override fun initRecyclerViews() {
        albumsrecyclerview.layoutManager = LinearLayoutManager(ctx)
        albumsrecyclerview.adapter = AlbumAdapter(ArrayList(), this)
        imageGrid.adapter = VideoGridAdapter(imageList = photoList, threshold = (ctx as PickerActivity).VIDEOS_THRESHOLD)
    }

    override fun toggleDropdown() {
       /* dropdown.animate().rotationBy(0f).setDuration(300).setInterpolator(LinearInterpolator()).start()
        if ((albumsrecyclerview.adapter as AlbumAdapter).malbumList.size == 0) {
            albumsrecyclerview.adapter = AlbumAdapter(albumList, this)
            dropdown.setImageResource(R.drawable.ic_dropdown_rotate)
            try {
                done.isEnabled = false
                val animation = AnimationUtils.loadAnimation(ctx, R.anim.scale_down)
                done.startAnimation(animation)
            } catch (e: Exception) {
            }
            done.visibility = View.GONE
        } else {
            albumsrecyclerview.adapter = AlbumAdapter(ArrayList(), this)
            dropdown.setImageResource(R.drawable.ic_dropdown)
            done.isEnabled = true
            done.visibility = View.VISIBLE
        }*/
    }

    override fun getPhoneAlbums(context: Context, listener: OnPhoneImagesObtained) {
        imagePickerPresenter.getPhoneAlbums()
    }

    override fun updateTitle(galleryAlbums: GalleryAlbums) {
//        albumselection.text = galleryAlbums.name
    }

    override fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData>) {
        for (selected in selectedlist) {
            for (photo in photoList) {
                photo.isSelected = selected.id == photo.id
                photo.isEnabled = selected.id == photo.id
            }
        }
    }

    @TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN)
    fun checkReadWritePermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_READ_WRITE)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) initGalleryViews()
            else allowAccessFrame.visibility = View.VISIBLE
        }
    }

    private fun isReadWritePermitted(): Boolean = (context?.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && context?.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
}