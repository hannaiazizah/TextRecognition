package com.hanna.textrecognition.data.service

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hanna.textrecognition.data.model.ImageRequest
import com.hanna.textrecognition.data.model.UpdateDataRequest
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseHelper @Inject constructor() {
    suspend fun uploadFile(request: ImageRequest): Result<String?> {
        request.imageUri ?: return Result.success(null)
        val uri = request.imageUri
        val storage = Firebase.storage
        val reference = storage.reference

        val riversRef = reference.child("images/${uri.lastPathSegment}")

        return try {
            riversRef.putFile(uri).await()
            Result.success(riversRef.path)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addNewData(imageRequest: ImageRequest): Result<String?> {
        val database = Firebase.database
        val currentMillis = System.currentTimeMillis()
        val randomUuid = UUID.randomUUID().toString()
        val pathString = "$randomUuid-$currentMillis"
        val myRef = database.getReference("captures")

        val hashMap: HashMap<String, String> = hashMapOf()
        hashMap["imagePath"] = imageRequest.imageUrl ?: ""
        hashMap["imageText"] = imageRequest.imageText ?: ""
        hashMap["latitude"] = imageRequest.latitude ?: ""
        hashMap["longitude"] = imageRequest.longitude ?: ""
        hashMap["duration"] = imageRequest.duration ?: ""
        hashMap["distance"] = imageRequest.distance ?: ""

        return try {
            myRef.child(pathString).setValue(hashMap).await()
            val data = myRef.child(pathString).get().await()
            if (data.exists()) {
                Result.success(pathString)
            } else {
                Result.failure(Throwable("empty failure"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateData(request: UpdateDataRequest): Result<Boolean> {
        if (request.dataPath.isEmpty()) return Result.failure(Throwable("data path not found"))
        val database = Firebase.database
        val myRef = database.getReference("captures")
        val hashMap: HashMap<String, String> = hashMapOf()
        hashMap["imageText"] = request.imageText
        hashMap["latitude"] = request.latitude
        hashMap["longitude"] = request.longitude
        hashMap["duration"] = request.duration
        hashMap["distance"] = request.distance
        hashMap["imagePath"] = request.imagePath

        return try {
            myRef.child(request.dataPath).setValue(hashMap).await()
            val data = myRef.child(request.dataPath).get().await()
            if (data.exists()) {
                Result.success(true)
            } else {
                Result.failure(Throwable("empty failure"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}