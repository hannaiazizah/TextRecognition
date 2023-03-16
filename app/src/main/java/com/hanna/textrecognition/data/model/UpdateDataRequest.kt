package com.hanna.textrecognition.data.model

data class UpdateDataRequest(
    val imageText: String,
    val latitude: String,
    val longitude: String,
    val distance: String,
    val duration: String,
    val dataPath: String,
    val imagePath: String
)
