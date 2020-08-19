package com.example.gallery

import android.os.Build
import android.os.Bundle

import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gallery.model.Media
import kotlinx.android.synthetic.main.activity_photo_detail.*

class PhotoDetail : AppCompatActivity(), View.OnClickListener {

    var media : Media? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }

        setContentView(R.layout.activity_photo_detail)
        getData()
        addEvent()
    }

    private fun getData() {
        media = intent.getParcelableExtra("media")
        loadData()
    }

    private fun loadData() {
        if (media!=null){
            Glide.with(this).load(media!!.uri).into(imgPhoto)
        }
    }

    private fun addEvent() {
        vBack.setOnClickListener(this)
        vMore.setOnClickListener(this)
        vLike.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.vBack -> {
                    onBackPressed()
                }
                R.id.vMore -> {

                }
                R.id.vLike -> {

                }
            }
        }
    }


}
