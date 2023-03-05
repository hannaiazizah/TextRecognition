package com.hanna.textrecognition.data.repository

import com.hanna.textrecognition.data.model.DistanceResponse
import com.hanna.textrecognition.data.model.ResultState

interface DistanceRepository {
    suspend fun calculateDistance(lat: Double, long: Double): ResultState<DistanceResponse>
}