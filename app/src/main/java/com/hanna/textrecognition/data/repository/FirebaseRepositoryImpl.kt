package com.hanna.textrecognition.data.repository

import com.hanna.textrecognition.data.model.ImageRequest
import com.hanna.textrecognition.data.service.FirebaseHelper

class FirebaseRepositoryImpl(
    private val firebaseHelper: FirebaseHelper
) : FirebaseRepository {

    override suspend fun submitData(request: ImageRequest): Boolean {
        return firebaseHelper.uploadFile(request)
    }

}