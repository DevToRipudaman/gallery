package com.example.openpicture.galleryPicker.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.openpicture.MainActivity
import com.example.openpicture.R
import com.example.openpicture.pdfPicker.OpenPdf.Companion.start
import com.example.openpicture.pdfPicker.PDFActivity
import kotlinx.android.synthetic.main.activity_picker.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PickerActivity : AppCompatActivity() {

    private val PERMISSIONS_CAMERA = 124
    var IMAGES_THRESHOLD = 0
    var VIDEOS_THRESHOLD = 0
    var REQUEST_RESULT_CODE = 5
    lateinit var ctx: Context
    var PDF_THRESHOLD = 0



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        val i = intent
        IMAGES_THRESHOLD = i.getIntExtra("IMAGES_LIMIT", 0)
        VIDEOS_THRESHOLD = i.getIntExtra("VIDEOS_LIMIT", 0)
        PDF_THRESHOLD = i.getIntExtra("PDF LIMIT", 0)
        REQUEST_RESULT_CODE = i.getIntExtra("REQUEST_RESULT_CODE", 0)
        if (i.hasExtra("Open")) {
            if (i.getStringExtra("Open")?.equals("Photos")!!) {
                supportFragmentManager.beginTransaction().add(R.id.frame_layout, PhotosFragment()).commit()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    startActivityForResult(getFileChooserIntent(), 478)
                } else {
                    supportFragmentManager.beginTransaction().add(R.id.frame_layout, PdfFragment()).commit()
                }
            }

        }


    }


    private fun getFileChooserIntent(): Intent? {


        val mimeTypes = arrayOf("application/pdf")
        val intent = Intent()
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.size > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

            }
        } else {
            var mimeTypesStr = ""
            for (mimeType in mimeTypes) {
                mimeTypesStr += "$mimeType|"
            }
            intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
        }
        return intent
    }


    private fun isCameraPermitted(): Boolean {
        val permission = android.Manifest.permission.CAMERA
        val cameraPermission = checkCallingOrSelfPermission(permission)
        return (cameraPermission == PackageManager.PERMISSION_GRANTED)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkCameraPermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_CAMERA)
        return true
    }

    val REQUEST_TAKE_PHOTO = 1
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this, applicationContext.packageName, photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    val array = ArrayList<String>()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) galleryAddPic()

        if (requestCode == 478 && resultCode == Activity.RESULT_OK && data != null) {
            if (null != data.clipData) {
                val bundle = Bundle()
                for (i in 0 until data.clipData!!.itemCount) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    array.add(uri.toString())
                    bundle.putStringArrayList("MY_PDF_FLES", array)
                    finish()

                }
                val intent = Intent(this, PDFActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                array.clear()
            } else {
//                val uri = data.data
//                Toast.makeText(this,uri.toString(),Toast.LENGTH_LONG).show()
                data.data?.also { documentUri ->
                    this.contentResolver?.takePersistableUriPermission(
                            documentUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    val file = getFile(this, documentUri)
                    start(this, file.path, "PDF")
                }
                finish()
            }
        } else if (data == null) {
            finish()
        }
    }

    fun getFile(mContext: Context?, documentUri: Uri): File {
        val inputStream = mContext?.contentResolver?.openInputStream(documentUri)
        var file = File("")
        inputStream.use { input ->
            file =
                    File(mContext?.cacheDir, System.currentTimeMillis().toString() + ".pdf")
            FileOutputStream(file).use { output ->
                val buffer =
                        ByteArray(4 * 1024) // or other buffer size
                var read: Int = -1
                while (input?.read(buffer).also {
                            if (it != null) {
                                read = it
                            }
                        } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        return file
    }


    private var mCurrentPhotoPath: String = ""

    private fun galleryAddPic() {
        val f = File(mCurrentPhotoPath)
        val contentUri = Uri.fromFile(f)
        val path = "${Environment.getExternalStorageDirectory()}${File.separator}Zoho Social${File.separator}media${File.separator}Zoho Social Images"
        val folder = File(path)
        if (!folder.exists()) folder.mkdirs()
        val file = File(path, "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_picture.jpg")
        val out = FileOutputStream(file)
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentUri)
        val ei = ExifInterface(mCurrentPhotoPath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        val rotatedBitmap: Bitmap? = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> null
        }
        rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.close()
        ContentUris.parseId(Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, file.absolutePath, file.name, file.name)))
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }


    val tabIconList: ArrayList<Int> = ArrayList()
    private val tabIcons = intArrayOf(R.drawable.ic_picker_photos_unselected,
            R.drawable.ic_video_unselected, R.drawable.ic_baseline_picture_as_pdf_24)

    private val selectedTabIcons = intArrayOf(R.drawable.ic_picker_photos_selected,
            R.drawable.ic_video_selected, R.drawable.ic_baseline_picture_as_pdf_24_selected)


    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(this@PickerActivity.supportFragmentManager)
        val photosFragment = PhotosFragment()
        adapter.addFragment(photosFragment, "PHOTOS")
        val videosFragment = VideosFragment()
        adapter.addFragment(videosFragment, "VIDEOS")
        val pdfFragment = PdfFragment()
        adapter.addFragment(pdfFragment, "PDF")
        viewPager.adapter = adapter
        (viewPager.adapter as ViewPagerAdapter).notifyDataSetChanged()
        viewPager.currentItem = 0
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        val mFragmentList: ArrayList<Fragment> = ArrayList()
        val mFragmentTitleList: ArrayList<String> = ArrayList()

        override fun getItem(position: Int): Fragment = mFragmentList[position]

        override fun getCount(): Int = mFragmentList.size

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getItemPosition(`object`: Any): Int = POSITION_NONE

        override fun getPageTitle(position: Int): CharSequence? = null
    }
}



