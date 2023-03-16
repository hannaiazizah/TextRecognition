package com.hanna.textrecognition.domain.usecase

import android.net.Uri
import com.google.mlkit.vision.text.Text
import com.hanna.textrecognition.data.model.ImageRequest
import com.hanna.textrecognition.domain.model.UploadDataResultUiModel
import com.hanna.textrecognition.data.repository.FirebaseRepository
import com.hanna.textrecognition.domain.core.Either
import com.hanna.textrecognition.domain.core.Failure
import com.hanna.textrecognition.domain.core.UnknownErrorResult
import com.hanna.textrecognition.domain.core.UseCase
import javax.inject.Inject

class SubmitDataUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): UseCase<UploadDataResultUiModel, SubmitDataUseCase.Params>() {

    data class Params(
        val imageUri: Uri? = null,
        val imageText: Text? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val duration: String? = null,
        val distance: String? = null
    )

    override suspend fun run(params: Params): Either<Failure, UploadDataResultUiModel> {
        params.imageUri?: return Either.fail(Failure.DataFormatFailure)
        val request = ImageRequest(
            imageUri = params.imageUri,
            imageText = params.imageText?.text,
            latitude = params.latitude?.toString(),
            longitude = params.longitude?.toString(),
            distance = params.distance,
            duration = params.duration
        )
        val uploadResult = firebaseRepository.uploadData(request)
        return when {
            uploadResult.isSuccess -> {
                request.imageUrl = uploadResult.getOrNull()
                val dbResult = firebaseRepository.submitData(request)
                if (dbResult.isSuccess) {
                    val uploadDataResponse =
                        UploadDataResultUiModel(
                            referenceId = dbResult.getOrNull() ?: "",
                            imageUrl = uploadResult.getOrNull() ?: ""
                        )
                    Either.success(uploadDataResponse)
                } else {
                    Either.fail(Failure.UnknownFailure)
                }

            }
            uploadResult.isFailure -> {
                Either.fail(
                    Failure.ServerFailure(
                        uploadResult.exceptionOrNull() ?: UnknownErrorResult
                    )
                )
            }
            else -> {
                Either.fail(Failure.UnknownFailure)
            }
        }
    }
}