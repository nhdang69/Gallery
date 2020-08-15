package com.example.gallery.model

import android.net.Uri

data class Image(
    var uriImage: Uri,
    var displayName: String = "",
    var dateAdded: String = "",
    var dateModified: String = "",
    var description: String = ""
) {

}