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
import com.example.gallery.model.Media
import com.example.gallery.model.Video
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

class PhotoAdapter(
    private val context: Context,
    private val imagesGroupBy: Map<String, List<Media>>,
    private val widthHeightDevice: IntArray,
    private val mediaListener: MediaAdapter.MediaListener
) : RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvSizeImage: TextView = itemView.findViewById(R.id.tvSizeImage)
        private val rcvImages: RecyclerView = itemView.findViewById(R.id.rcvImages)

        @SuppressLint("SetTextI18n")
        fun loadData(
            context: Context,
            list: List<Media>?,
            widthHeightDevice: IntArray,
            mediaListener: MediaAdapter.MediaListener
        ) {
            if (list != null && list.isNotEmpty()) {
                if (list[0] is Image) {
                    val image = list[0] as Image
                    tvDate.text = image.dateAdded
                } else if (list[0] is Video) {
                    val video = list[0] as Video
                    tvDate.text = video.dateAdded
                }
                runBlocking {
                    var totalVideo = 0
                    var totalImage = 0
                    val deferrals = listOf(async {
                        for (item in list) {
                            if (item is Video)
                                totalVideo++
                        }
                    }, async {
                        for (item in list) {
                            if (item is Image)
                                totalImage++
                        }
                    })
                    deferrals.awaitAll()
                    val total = StringBuilder()
                    if (totalImage > 0)
                        total.append("$totalImage photos")
                    if (totalImage > 0 && totalVideo > 0)
                        total.append(" , ")
                    if (totalVideo > 0)
                        total.append("$totalVideo videos")
                    tvSizeImage.text = total.toString()
                }

                rcvImages.layoutManager = GridLayoutManager(
                    context, 4,
                    GridLayoutManager.VERTICAL, false
                )
                rcvImages.setHasFixedSize(true)
                rcvImages.adapter = MediaAdapter(context, list, widthHeightDevice, mediaListener)
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
        holder.loadData(
            context,
            imagesGroupBy[imagesGroupBy.keys.toList()[position]],
            widthHeightDevice, mediaListener
        )
    }
}