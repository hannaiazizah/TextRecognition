package com.hanna.textrecognition.data.repository

import com.hanna.textrecognition.data.model.ImageRequest
import com.hanna.textrecognition.data.model.UpdateDataRequest

interface FirebaseRepository {

    suspend fun uploadData(request: ImageRequest): Result<String?>

    suspend fun submitData(request: ImageRequest): Result<String?>

    suspend fun updateData(request: UpdateDataRequest): Result<Boolean>

}