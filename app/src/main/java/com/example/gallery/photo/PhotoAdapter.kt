package com.example.gallery.photo

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gallery.R
import com.example.gallery.model.Image
import com.example.gallery.utils.convertDpToPixel

class PhotoAdapter(
    private val context: Context,
    private val images: List<Image>,
    private val widthHeightDevice: IntArray
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        private val imgPhoto : ImageView = itemView.findViewById(R.id.imgPhoto)

        fun loadData(
            context: Context,
            image: Image,
            widthHeightDevice: IntArray,
            position: Int
        ) {
            val width = (widthHeightDevice[0] - convertDpToPixel(6,context)) / 4

            val layoutParams : ConstraintLayout.LayoutParams = imgPhoto.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = width
            if(position % 4 == 1){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.marginStart = convertDpToPixel(2,context)
                    layoutParams.marginEnd = convertDpToPixel(2,context)
                }
            }else if (position % 4 == 2){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.marginEnd = convertDpToPixel(2,context)
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.marginStart = 0
                    layoutParams.marginEnd = 0
                }
            }
            imgPhoto.layoutParams = layoutParams
            val requestBuilder = RequestOptions().fitCenter().override(width,width)
            Glide.with(context).load(image.uriImage).apply(requestBuilder).into(imgPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_photo_item,parent,false))
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.loadData(context,images[position],widthHeightDevice,position)
    }
}