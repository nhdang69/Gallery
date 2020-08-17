package com.example.gallery.model

import android.net.Uri

data class Image(
    var uriImage: Uri,
    override val displayName: String = "",
    override val dateAdded: String = "",
    override val dateModified: String = "",
    override val description: String = ""
):Media(displayName, dateAdded, dateModified, description)