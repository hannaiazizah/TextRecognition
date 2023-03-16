package com.hanna.textrecognition.domain.usecase

import com.hanna.textrecognition.data.model.UpdateDataRequest
import com.hanna.textrecognition.data.repository.FirebaseRepository
import com.hanna.textrecognition.domain.core.Either
import com.hanna.textrecognition.domain.core.Failure
import com.hanna.textrecognition.domain.core.UnknownErrorResult
import com.hanna.textrecognition.domain.core.UseCase
import javax.inject.Inject

class UpdateDataUseCase @Inject constructor(
    private val repository: FirebaseRepository
): UseCase<Boolean, UpdateDataUseCase.Params>() {

    data class Params(
        val dataPath: String,
        val imageText: String,
        val latitude: Double,
        val longitude: Double,
        val duration: String,
        val distance: String,
    )

    override suspend fun run(params: Params): Either<Failure, Boolean> {
        val updateDataRequest = UpdateDataRequest(
            imageText = params.imageText,
            dataPath = params.dataPath,
            latitude = params.latitude.toString(),
            longitude = params.longitude.toString(),
            duration = params.duration,
            distance = params.distance
        )
        val result = repository.updateData(updateDataRequest)
        return if (result.isSuccess && result.getOrDefault(false)) {
            Either.success(true)
        } else {
            val exception = result.exceptionOrNull() ?: UnknownErrorResult
            Either.fail(Failure.ServerFailure(exception))
        }
    }
}