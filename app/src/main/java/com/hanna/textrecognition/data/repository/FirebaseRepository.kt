package com.hanna.textrecognition.data.repository

import com.hanna.textrecognition.data.model.ImageRequest

interface FirebaseRepository {
    suspend fun submitData(request: ImageRequest): Boolean

}