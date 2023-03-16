package com.hanna.textrecognition.data.repository

import com.hanna.textrecognition.data.model.ImageRequest
import com.hanna.textrecognition.data.model.UpdateDataRequest
import com.hanna.textrecognition.data.service.FirebaseHelper

class FirebaseRepositoryImpl(
    private val firebaseHelper: FirebaseHelper
) : FirebaseRepository {

    override suspend fun uploadData(request: ImageRequest): Result<String?> {
        return firebaseHelper.uploadFile(request)
    }

    override suspend fun submitData(request: ImageRequest): Result<String?> {
        return firebaseHelper.addNewData(request)
    }

    override suspend fun updateData(request: UpdateDataRequest): Result<Boolean> {
        return firebaseHelper.updateData(request)
    }

}