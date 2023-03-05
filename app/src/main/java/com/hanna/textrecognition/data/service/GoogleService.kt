package com.hanna.textrecognition.data.service

import com.hanna.textrecognition.data.model.DistanceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleService {
    @GET("distancematrix/json")
    suspend fun getDistanceMatrix(
        @Query("origins") origin: String,
        @Query("destinations") destination: String,
        @Query("key") apiKey: String
    ): Response<DistanceResponse>
}