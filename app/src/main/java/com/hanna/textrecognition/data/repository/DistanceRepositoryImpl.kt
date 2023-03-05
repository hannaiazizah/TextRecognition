package com.hanna.textrecognition.data.repository

import com.hanna.textrecognition.BuildConfig
import com.hanna.textrecognition.data.model.DistanceResponse
import com.hanna.textrecognition.data.model.ResultState
import com.hanna.textrecognition.data.service.GoogleService

class DistanceRepositoryImpl(
    private val googleService: GoogleService
): DistanceRepository {
    override suspend fun calculateDistance(lat: Double, long: Double): ResultState<DistanceResponse> {
        val apiKey = BuildConfig.API_KEY
        val destination = "-6.1938544,106.8197775" // Plaza Indonesia
        val origin = "$lat,$long"
        val result = googleService.getDistanceMatrix(origin, destination, apiKey)
        return if (result.isSuccessful && result.body() != null) {
            ResultState.Success(result.body()!!)
        } else {
            ResultState.Error(Exception(result.message()))
        }

    }
}