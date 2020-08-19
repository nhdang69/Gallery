package com.example.gallery.model

import android.net.Uri


data class Video (
    override var uri: Uri?,
    override var displayName: String = "",
    override var dateAdded: String = "",
    override var dateModified: String = "",
    override var description: String = ""
) : Media(uri,displayName,dateAdded,dateModified,description) {
}