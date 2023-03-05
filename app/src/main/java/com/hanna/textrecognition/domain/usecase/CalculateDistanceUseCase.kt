package com.hanna.textrecognition.domain.usecase

import com.hanna.textrecognition.data.model.ResultState
import com.hanna.textrecognition.data.model.isSuccess
import com.hanna.textrecognition.data.repository.DistanceRepository
import com.hanna.textrecognition.domain.core.Either
import com.hanna.textrecognition.domain.core.Failure
import com.hanna.textrecognition.domain.core.UseCase
import com.hanna.textrecognition.domain.model.DistanceUiModel
import javax.inject.Inject

class CalculateDistanceUseCase @Inject constructor(
    private val distanceRepository: DistanceRepository
) : UseCase<DistanceUiModel, CalculateDistanceUseCase.Params>() {
    data class Params(
        val latitude: Double,
        val longitude: Double
    )

    override suspend fun run(params: Params): Either<Failure, DistanceUiModel> {
        val result = distanceRepository.calculateDistance(params.latitude, params.longitude)

        if (result.isSuccess()) {
            val uiModel = DistanceUiModel.toDistanceUiModel((result as ResultState.Success).data)
            if (uiModel != null) return Either.success(uiModel)
        }

        return Either.fail(Failure.UnknownError)
    }
}