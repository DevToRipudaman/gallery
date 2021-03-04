package com.example.openpicture.galleryPicker.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.openpicture.R
import com.example.openpicture.galleryPicker.model.PdfAlbums
import com.example.openpicture.galleryPicker.utils.font.FontsConstants
import com.example.openpicture.galleryPicker.utils.font.FontsManager
import com.example.openpicture.galleryPicker.view.PdfFragment
import kotlinx.android.synthetic.main.album_item.view.*
import kotlinx.android.synthetic.main.fragment_media.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.ArrayList

class PdfAlbumAdapter() : RecyclerView.Adapter<PdfAlbumAdapter.MyViewHolder>() {
    var malbumList: ArrayList<PdfAlbums> = ArrayList()
    lateinit var currentFragment: Fragment

    constructor(albumList: ArrayList<PdfAlbums> = ArrayList(), currentFragment: Fragment) : this() {
        malbumList = albumList
        this.currentFragment = currentFragment
    }

    lateinit var ctx: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        ctx = parent.context
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val count = malbumList[holder.adapterPosition].albumPhotos.count { it.isSelected }
        if (count > 0 && malbumList[holder.adapterPosition].id != 0) {
            holder.selectedcount.visibility = View.VISIBLE
            holder.selectedcount.text = count.toString()
        } else holder.selectedcount.visibility = View.GONE

        holder.albumtitle.text = malbumList[holder.adapterPosition].name
        holder.photoscount.text = malbumList[holder.adapterPosition].albumPhotos.size.toString()

        doAsync {
            uiThread {
                Glide.with(currentFragment).load(malbumList[holder.adapterPosition].coverUri).apply(RequestOptions().centerCrop().placeholder(R.drawable.ic_link_cont_default_img_1_5x)).into(holder.albumthumbnail)
            }
        }

        holder.albumFrame.setOnClickListener {
            when (currentFragment) {

                is PdfFragment->{
                    (currentFragment as PdfFragment).updatePDFTitle(malbumList[holder.adapterPosition])
                    (currentFragment as PdfFragment).imageGrid.adapter = PdfGridAdapter((currentFragment as PdfFragment).photoList, malbumList[holder.adapterPosition].id)
                    (currentFragment as PdfFragment).toggleDropdown()
                }
            }
        }

        holder.albumtitle.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
        holder.photoscount.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
    }

    override fun getItemCount(): Int = malbumList.size

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var albumthumbnail: ImageView = view.albumthumbnail
        var albumtitle: TextView = view.albumtitle
        var photoscount: TextView = view.photoscount
        var selectedcount: TextView = view.selectedcount
        var albumFrame: FrameLayout = view.albumFrame
    }
}