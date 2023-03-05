package com.hanna.textrecognition.data.model

import android.net.Uri

data class ImageRequest(
    val imageUri: Uri? = null,
    var imageUrl: String? = null,
    val imageText: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val distance: String? = null,
    val duration: String? = null,
)
