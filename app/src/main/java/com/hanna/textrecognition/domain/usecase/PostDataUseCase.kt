package com.hanna.textrecognition.domain.usecase

import android.net.Uri
import com.google.mlkit.vision.text.Text
import com.hanna.textrecognition.data.model.ImageRequest
import com.hanna.textrecognition.data.repository.FirebaseRepository
import com.hanna.textrecognition.domain.core.Either
import com.hanna.textrecognition.domain.core.Failure
import com.hanna.textrecognition.domain.core.UseCase
import javax.inject.Inject

class PostDataUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): UseCase<Boolean, PostDataUseCase.Params>() {

    data class Params(
        val imageUri: Uri? = null,
        val imageText: Text? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val duration: String? = null,
        val distance: String? = null
    )

    override suspend fun run(params: Params): Either<Failure, Boolean> {
        params.imageUri?: return Either.fail(Failure.DataFormatError)
        val request = ImageRequest(
            imageUri = params.imageUri,
            imageText = params.imageText?.text,
            latitude = params.latitude?.toString(),
            longitude = params.longitude?.toString(),
            distance = params.distance,
            duration = params.duration
        )
        val result = firebaseRepository.submitData(request)
        return Either.success(result)
    }
}