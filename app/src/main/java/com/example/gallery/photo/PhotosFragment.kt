package com.example.gallery.photo

import android.app.Activity
import android.app.ActivityOptions
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gallery.PhotoDetail
import com.example.gallery.R
import com.example.gallery.model.Image
import com.example.gallery.model.Media
import com.example.gallery.model.Video
import com.example.gallery.utils.getDate
import com.example.gallery.utils.getWidthHeightDevice
import kotlinx.android.synthetic.main.fragment_photos.*
import kotlinx.coroutines.*
import java.util.*


class PhotosFragment : Fragment(), MediaAdapter.MediaListener {

    private val permissionsRequestCode = 101
    private var data: List<Media> = ArrayList()

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
                    getDataFromDevice()
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
                    getDataFromDevice()
                } else {
                    requestPermission()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getDataFromDevice() = runBlocking {

        val deferrals = listOf(async {
            getImageFromDevice()
        }, async {
            getVideoFromDevice()
        })

        deferrals.awaitAll()

        if (data.isNotEmpty()) {
            val dataGroupBy = data.groupBy {
                it.dateAdded
            }
            rcvImages.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rcvImages.setHasFixedSize(true)
            rcvImages.adapter =
                PhotoAdapter(
                    context!!,
                    dataGroupBy,
                    this@PhotosFragment.getWidthHeightDevice(),
                    this@PhotosFragment
                )
        }
    }

    private fun getImageFromDevice() {
        context?.let {
            val projection = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_ADDED,
                MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.DESCRIPTION
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"


            val queryExternal = context!!.contentResolver!!.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            val queryInternal = context!!.contentResolver!!.query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            queryInternal.use { cursor ->
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
                        val dateAddedCur = cursor.getLong(dateAdded)
                        val dateModifiedCur = cursor.getString(dateModified)
                        var descriptionCur = cursor.getString(description)
                        val id = cursor.getLong(fieldIndex)
                        if (descriptionCur == null) {
                            descriptionCur = ""
                        }
                        val imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                            id
                        )
                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        val dateAddedStr = dateAddedCur.getDate()
                        data = data + Image(
                            imageUri,
                            displayNameCur,
                            dateAddedStr,
                            dateModifiedCur,
                            descriptionCur
                        )
                    }
                }
            }

            queryExternal.use { cursorEx ->
                // Cache column indices.
                if (cursorEx != null) {
                    val displayName =
                        cursorEx.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                    val dateAdded =
                        cursorEx.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_ADDED)
                    val dateModified =
                        cursorEx.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED)
                    val description =
                        cursorEx.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DESCRIPTION)
                    val fieldIndex = cursorEx.getColumnIndex(MediaStore.Images.ImageColumns._ID)

                    while (cursorEx.moveToNext()) {
                        // Get values of columns for a given video.
                        val displayNameCur = cursorEx.getString(displayName)
                        val dateAddedCur = cursorEx.getLong(dateAdded)
                        val dateModifiedCur = cursorEx.getString(dateModified)
                        var descriptionCur = cursorEx.getString(description)
                        val id = cursorEx.getLong(fieldIndex)
                        if (descriptionCur == null) {
                            descriptionCur = ""
                        }
                        val imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        val dateAddedStr = dateAddedCur.getDate()
                        data = data + Image(
                            imageUri,
                            displayNameCur,
                            dateAddedStr,
                            dateModifiedCur,
                            descriptionCur
                        )
                    }
                }
            }
        }
    }


    private fun getVideoFromDevice() {
        context?.let {
            val projection = arrayOf(
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATE_ADDED,
                MediaStore.Video.VideoColumns.DATE_MODIFIED,
                MediaStore.Video.VideoColumns.DESCRIPTION
            )

            val sortOrder = "${MediaStore.Video.VideoColumns.DATE_ADDED} DESC"


            val query = context!!.contentResolver!!.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            val queryInternal = context!!.contentResolver!!.query(
                MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            queryInternal.use { cursor ->
                // Cache column indices.
                if (cursor != null) {
                    val displayName =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME)
                    val dateAdded =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED)
                    val dateModified =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_MODIFIED)
                    val description =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DESCRIPTION)
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given video.
                        val displayNameCur = cursor.getString(displayName)
                        val dateAddedCur = cursor.getLong(dateAdded)
                        val dateModifiedCur = cursor.getString(dateModified)
                        var descriptionCur = cursor.getString(description)
                        val id = cursor.getLong(fieldIndex)
                        if (descriptionCur == null) {
                            descriptionCur = ""
                        }
                        val uriVideo = ContentUris.withAppendedId(
                            MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                            id
                        )
                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        val dateAddedStr = dateAddedCur.getDate()
                        data = data + Video(
                            uriVideo,
                            displayNameCur,
                            dateAddedStr,
                            dateModifiedCur,
                            descriptionCur
                        )
                    }
                }
            }

            query.use { cursor ->
                // Cache column indices.
                if (cursor != null) {
                    val displayName =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME)
                    val dateAdded =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED)
                    val dateModified =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_MODIFIED)
                    val description =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DESCRIPTION)
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given video.
                        val displayNameCur = cursor.getString(displayName)
                        val dateAddedCur = cursor.getLong(dateAdded)
                        val dateModifiedCur = cursor.getString(dateModified)
                        var descriptionCur = cursor.getString(description)
                        val id = cursor.getLong(fieldIndex)
                        if (descriptionCur == null) {
                            descriptionCur = ""
                        }
                        val uriVideo = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        val dateAddedStr = dateAddedCur.getDate()
                        data = data + Video(
                            uriVideo,
                            displayNameCur,
                            dateAddedStr,
                            dateModifiedCur,
                            descriptionCur
                        )
                    }
                }
            }
        }
    }

    override fun onClick(itemView: View, media: Media) {
        val intent = Intent(context, PhotoDetail::class.java)
        intent.putExtra("media", media)
        val p1 = Pair.create<View, String>(itemView, "imgPhoto")
        val activityOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions.makeSceneTransitionAnimation(context as Activity, p1)
        } else {
            null
        }
        if (activityOptions != null) {
            startActivity(intent, activityOptions.toBundle())
        } else startActivity(intent)
    }
}