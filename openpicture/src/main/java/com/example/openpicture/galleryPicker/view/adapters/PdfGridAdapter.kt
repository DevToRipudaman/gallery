package com.example.openpicture.galleryPicker.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openpicture.R
import com.example.openpicture.galleryPicker.model.GalleryData
import com.example.openpicture.galleryPicker.model.PdfData
import com.example.openpicture.galleryPicker.utils.DateUtil
import com.example.openpicture.galleryPicker.utils.scroll.FastScrollRecyclerView
import com.example.openpicture.galleryPicker.view.PickerActivity
import kotlinx.android.synthetic.main.grid_item.view.checkbox
import kotlinx.android.synthetic.main.grid_item.view.frame
import kotlinx.android.synthetic.main.grid_item.view.image
import kotlinx.android.synthetic.main.grid_pdf.view.*

class PdfGridAdapter() : RecyclerView.Adapter<PdfGridAdapter.MyViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    lateinit var ctx: Context
    private var mImageList: ArrayList<PdfData> = ArrayList()
    private var fullimagelist: ArrayList<PdfData> = ArrayList()
    var THRESHOLD = 4

    constructor(imageList: ArrayList<PdfData> = ArrayList(), filter: Int = 0, threshold: Int = 4) : this() {
        fullimagelist = imageList
        THRESHOLD = threshold
        if (filter == 0) mImageList = imageList
        else imageList.filter { it.albumId == filter }.forEach { mImageList.add(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        ctx = parent.context
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grid_pdf, parent, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (THRESHOLD != 0) {
            if (getSelectedCount() >= THRESHOLD) mImageList.filterNot { it.isSelected }.forEach { it.isEnabled = false }
            else mImageList.forEach { it.isEnabled = true }
        }


        if (mImageList[holder.adapterPosition].isEnabled) {
            holder.frame.alpha = 1.0f
            holder.image.isEnabled = true
            holder.checkbox.visibility = View.VISIBLE
        } else {
            holder.frame.alpha = 0.3f
            holder.image.isEnabled = false
            holder.checkbox.visibility = View.INVISIBLE
        }


        if (mImageList[holder.adapterPosition].isSelected) holder.checkbox.setImageResource(R.drawable.tick)
        else holder.checkbox.setImageResource(R.drawable.round)

        holder.image.setOnClickListener {
            if (THRESHOLD != 0) {
                when {
                    getSelectedCount() <= THRESHOLD -> {
                        if (mImageList[holder.adapterPosition].isSelected) {
                            mImageList[holder.adapterPosition].isSelected = false
                            holder.checkbox.setImageResource(R.drawable.round)
                            if (getSelectedCount() == (THRESHOLD - 1) && !mImageList[holder.adapterPosition].isSelected) {
                                mImageList.forEach { it.isEnabled = true }
                                for ((index, item) in mImageList.withIndex()) {
                                    if (item.isEnabled && !item.isSelected) notifyItemChanged(index)
                                }
                            }
                        } else {
                            mImageList[holder.adapterPosition].isSelected = true
                            holder.checkbox.setImageResource(R.drawable.tick)
                            if (getSelectedCount() == THRESHOLD && mImageList[holder.adapterPosition].isSelected) {
                                mImageList.filterNot { it.isSelected }.forEach { it.isEnabled = false }
                                for ((index, item) in mImageList.withIndex()) {
                                    if (!item.isEnabled) notifyItemChanged(index)
                                }
                            }
                        }
                    }
                    getSelectedCount() > THRESHOLD -> {
                        for (image in mImageList) {
                            mImageList.filter { it.isSelected && !it.isEnabled }.forEach { it.isSelected = false }
                        }
                    }
                    else -> {
                    }
                }
            } else {
                if (mImageList[holder.adapterPosition].isSelected) {
                    mImageList[holder.adapterPosition].isSelected = false
                    holder.checkbox.setImageResource(R.drawable.round)
                    notifyItemChanged(holder.adapterPosition)
                } else {
                    mImageList[holder.adapterPosition].isSelected = true
                    holder.checkbox.setImageResource(R.drawable.tick)
                    notifyItemChanged(holder.adapterPosition)
                }
        }
        }

        if (THRESHOLD==1) {
            val newList: ArrayList<PdfData> = ArrayList()
            mImageList.filterTo(newList) { it.isSelected }
            if (newList.size == 1){
                val i = Intent()
                i.putParcelableArrayListExtra("PDF", newList)
                (ctx as PickerActivity).setResult((ctx as PickerActivity).REQUEST_RESULT_CODE, i)
                (ctx as PickerActivity).onBackPressed()
            }
        }

        //   holder.fileName.text=mimageList[position].albumName
    }

    private fun getSelectedCount(): Int = fullimagelist.count { it.isSelected }

    override fun getItemCount(): Int = mImageList.size

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image = view.image
        var checkbox = view.checkbox
        var frame = view.frame
        var fileName=view.tvPdfFile

    }

    override fun getSectionName(position: Int): String = DateUtil().getMonthAndYearString(mImageList[position].dateAdded.toLong() * 1000)
}