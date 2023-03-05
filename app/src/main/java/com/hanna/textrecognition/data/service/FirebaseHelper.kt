package com.hanna.textrecognition.data.service

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hanna.textrecognition.data.model.ImageRequest
import java.util.UUID
import javax.inject.Inject

class FirebaseHelper @Inject constructor() {
    fun uploadFile(request: ImageRequest): Boolean {
        request.imageUri ?: return false
        val uri = request.imageUri
        val storage = Firebase.storage
        val reference = storage.reference

        val riversRef = reference.child("images/${uri.lastPathSegment}")
        val uploadTask = riversRef.putFile(uri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            riversRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.path ?: ""
                request.imageUrl = downloadUri
                submitDataToDatabase(request)
            }
            return@addOnCompleteListener
        }
        return true
    }

    private fun submitDataToDatabase(imageRequest: ImageRequest) {
        val database = Firebase.database
        val currentMillis = System.currentTimeMillis()
        val randomUuid = UUID.randomUUID().toString()
        val pathString = "$randomUuid-$currentMillis"
        val myRef = database.getReference("captures")

        val hashMap: HashMap<String, String> = hashMapOf()
        hashMap["imageUrl"] = imageRequest.imageUrl ?: ""
        hashMap["imageText"] = imageRequest.imageText ?: ""
        hashMap["latitude"] = imageRequest.latitude ?: ""
        hashMap["longitude"] = imageRequest.longitude ?: ""
        hashMap["duration"] = imageRequest.duration ?: ""
        hashMap["distance"] = imageRequest.distance ?: ""

        myRef.child(pathString).setValue(hashMap)
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}