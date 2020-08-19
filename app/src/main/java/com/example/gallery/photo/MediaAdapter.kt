package com.example.gallery.photo

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gallery.R
import com.example.gallery.model.Media
import com.example.gallery.model.Video
import com.example.gallery.utils.convertDpToPixel

class MediaAdapter(
    private val context: Context,
    private val images: List<Media>,
    private val widthHeightDevice: IntArray,
    private val mediaListener: MediaListener
) : RecyclerView.Adapter<MediaAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgPhoto: ImageView = itemView.findViewById(R.id.imgPhoto)
        private val tvVideo: TextView = itemView.findViewById(R.id.tvVideo)

        fun loadData(
            context: Context,
            media: Media,
            widthHeightDevice: IntArray,
            position: Int,
            mediaListener: MediaListener
        ) {
            val width = (widthHeightDevice[0] - convertDpToPixel(12, context)) / 4

            val layoutParams: ConstraintLayout.LayoutParams =
                imgPhoto.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = width
            layoutParams.topMargin = convertDpToPixel(4, context)
            if (position % 4 == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.marginStart = convertDpToPixel(4, context)
                    layoutParams.marginEnd = convertDpToPixel(4, context)
                }
            } else if (position % 4 == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.marginEnd = convertDpToPixel(4, context)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.marginStart = 0
                    layoutParams.marginEnd = 0
                }
            }
            imgPhoto.layoutParams = layoutParams
            val requestBuilder = RequestOptions().centerCrop().override(width, width)
            tvVideo.visibility = View.GONE
            Glide.with(context).load(media.uri).apply(requestBuilder).into(imgPhoto)
            if (media is Video) {
                tvVideo.visibility = View.VISIBLE
            }

            itemView.setOnClickListener{
                mediaListener.onClick(it,media)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_image_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.loadData(context, images[position], widthHeightDevice, position,mediaListener)
    }

    interface MediaListener {
        fun onClick(itemView: View, media: Media)
    }
}