package com.hanna.textrecognition.data.model

import com.google.gson.annotations.SerializedName

data class DistanceResponse(
    @SerializedName("rows") val rows: List<DistanceElements>? = null
)

data class DistanceElements(
    @SerializedName("elements") val elements: List<DistanceMatrix>? = null
)

data class DistanceMatrix(
    @SerializedName("distance") val distance: DistanceItem? = null,
    @SerializedName("duration") val duration: DistanceItem? = null,
    @SerializedName("status") val status: String? = null
)

data class DistanceItem(
    @SerializedName("text") val text: String? = null,
    @SerializedName("value") val value: Long? = null,
)