package com.example.gallery.photo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.R
import com.example.gallery.model.Image

class PhotoAdapter(
    private val context: Context,
    private val imagesGroupBy: Map<String, List<Image>>,
    private val widthHeightDevice: IntArray
) : RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvSizeImage : TextView = itemView.findViewById(R.id.tvSizeImage)
        private val rcvImages : RecyclerView = itemView.findViewById(R.id.rcvImages)

        @SuppressLint("SetTextI18n")
        fun loadData(
            context: Context,
            list: List<Image>?,
            widthHeightDevice: IntArray
        ) {
            if (list != null && list.isNotEmpty()) {
                tvDate.text = list[0].dateAdded
                tvSizeImage.text = "${list.size} photos"
                rcvImages.layoutManager = GridLayoutManager(context,4,
                    GridLayoutManager.VERTICAL,false)
                rcvImages.setHasFixedSize(true)
                rcvImages.adapter = ImageAdapter(context,list,widthHeightDevice)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        return PhotoHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_photo_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return imagesGroupBy.size
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        holder.loadData(context,imagesGroupBy[imagesGroupBy.keys.toList()[position]],widthHeightDevice)
    }
}