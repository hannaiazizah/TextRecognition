package com.hanna.textrecognition.domain.model

data class ImageAttributesUiModel(
    val text: String,
    val lat: Double,
    val long: Double,
    val distance: String,
    val time: String,
)
