package com.example.gallery.photo

import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gallery.R
import com.example.gallery.model.Image
import com.example.gallery.utils.getWidthHeightDevice
import kotlinx.android.synthetic.main.fragment_photos.*
import java.util.*


class PhotosFragment : Fragment() {

    private val permissionsRequestCode = 101
    private var images: List<Image> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun checkRequestPermission(): Boolean? {
        context?.let {
            return ContextCompat.checkSelfPermission(
                it,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        it,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val isRequest = checkRequestPermission()
            if (isRequest != null) {
                if (isRequest) {
                    getImageFromDevice()
                } else {
                    requestPermission()
                }
            } else {
                loError.visibility = View.VISIBLE
            }
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), permissionsRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionsRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImageFromDevice()
                } else {
                    requestPermission()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getImageFromDevice() {
        if (context!=null){
            val projection = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_ADDED,
                MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.DESCRIPTION
            )

            val sortOrder = "${MediaStore.Images.ImageColumns.DISPLAY_NAME} ASC"

            val query = context!!.contentResolver!!.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            query.use { cursor ->
                // Cache column indices.
                if (cursor != null) {
                    val displayName =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                    val dateAdded =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_ADDED)
                    val dateModified =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED)
                    val description =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DESCRIPTION)
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given video.
                        val displayNameCur = cursor.getString(displayName)
                        val dateAddedCur = cursor.getString(dateAdded)
                        val dateModifiedCur = cursor.getString(dateModified)
                        var descriptionCur = cursor.getString(description)
                        val id = cursor.getLong(fieldIndex)
                        if (descriptionCur == null) {
                            descriptionCur = ""
                        }
                        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        images = images + Image(
                            imageUri,
                            displayNameCur,
                            dateAddedCur,
                            dateModifiedCur,
                            descriptionCur
                        )
                    }
                }
            }

            if (images.isNotEmpty()){
                rcvImages.layoutManager = GridLayoutManager(context,4,GridLayoutManager.VERTICAL,false)
                rcvImages.setHasFixedSize(true)
                rcvImages.adapter = PhotoAdapter(context!!,images,this.getWidthHeightDevice())
            }
        }
    }
}