package com.hanna.textrecognition.domain.model

import com.hanna.textrecognition.data.model.DistanceResponse

data class DistanceUiModel(
    val distance: String,
    val estimatedTime: String
) {
    companion object {
        fun toDistanceUiModel(response: DistanceResponse): DistanceUiModel? {
            return response.rows?.first()?.elements?.first()?.let {
                DistanceUiModel(
                    distance = it.distance?.value?.toString() ?: "0",
                    estimatedTime = it.duration?.value?.toString() ?: "0"
                )
            }
        }
    }
}
