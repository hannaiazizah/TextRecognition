package com.hanna.textrecognition.presentation.camera

import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.hanna.textrecognition.domain.core.Either
import com.hanna.textrecognition.domain.core.Failure
import com.hanna.textrecognition.domain.core.FlowUseCase
import com.hanna.textrecognition.domain.core.onSuccess
import com.hanna.textrecognition.domain.model.DistanceUiModel
import com.hanna.textrecognition.domain.model.ImageAttributesUiModel
import com.hanna.textrecognition.domain.model.UploadDataResultUiModel
import com.hanna.textrecognition.domain.usecase.CalculateDistanceUseCase
import com.hanna.textrecognition.domain.usecase.GetLastLocationUseCase
import com.hanna.textrecognition.domain.usecase.SubmitDataUseCase
import com.hanna.textrecognition.domain.usecase.TextRecognitionUseCase
import com.hanna.textrecognition.domain.usecase.UpdateDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val textRecognitionUseCase: TextRecognitionUseCase,
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val calculateDistanceUseCase: CalculateDistanceUseCase,
    private val submitDataUseCase: SubmitDataUseCase,
    private val updateDataUseCase: UpdateDataUseCase
) : ViewModel() {

    private val _imageUri by lazy { MutableStateFlow<Uri?>(null) }
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _visionResult by lazy { MutableStateFlow<Text?>(null) }
    val visionResult: StateFlow<Text?> = _visionResult

    private val _locationResult by lazy { MutableStateFlow<Location?>(null) }
    val locationResult: StateFlow<Location?> = _locationResult

    private val _distanceResult by lazy {
        MutableStateFlow<Either<Failure, DistanceUiModel>>(
            Either.fail(Failure.Empty)
        )
    }
    val distanceResult: StateFlow<Either<Failure, DistanceUiModel>> = _distanceResult

    private val _isLoading by lazy { MutableSharedFlow<Boolean>() }
    val isLoading: SharedFlow<Boolean> = _isLoading

    private val _uploadResult by lazy {
        MutableStateFlow<Either<Failure, UploadDataResultUiModel>>(
            Either.fail(Failure.Empty)
        )
    }
    val uploadResult: StateFlow<Either<Failure, UploadDataResultUiModel>> = _uploadResult

    private val _updateResult by lazy { MutableSharedFlow<Either<Failure, Boolean>>() }
    val updateResult: SharedFlow<Either<Failure, Boolean>> = _updateResult

    private var dataDistance: DistanceUiModel? = null
    private var imageAttributesUiModel: ImageAttributesUiModel? = null

    fun setImageUri(uri: Uri?) {
        viewModelScope.launch {
            _isLoading.emit(true)
            _imageUri.emit(uri)
            _isLoading.emit(false)
        }
    }

    // run text recognition
    // request current location
    // calculate distance and duration
    // submit data
    fun fetchImageAttributes(image: InputImage) {
        viewModelScope.launch {
            _isLoading.emit(true)
            val recognitionParams = TextRecognitionUseCase.Params(image)

            val textVisionFlow = textRecognitionUseCase.run(recognitionParams)
            val lastLocationFlow = getLastLocationUseCase.run(FlowUseCase.None())

            combine(textVisionFlow, lastLocationFlow) { text, location ->
                Pair(text, location)
            }.collect {
                _visionResult.emit(it.first)
                _locationResult.emit(it.second)
                _isLoading.emit(false)

                it.second?.let { location ->
                    calculateDistanceMatrix(location)
                }
            }
        }
    }

    private fun calculateDistanceMatrix(location: Location) {
        viewModelScope.launch {
            _isLoading.emit(true)
            val params = CalculateDistanceUseCase.Params(
                location.latitude,
                location.longitude
            )
            val result = calculateDistanceUseCase.run(params)
            result.onSuccess {
                dataDistance = it
            }
            _distanceResult.emit(result)
            _isLoading.emit(false)
            submitData()
        }
    }

    private fun submitData() {
        viewModelScope.launch {
            _isLoading.emit(true)
            val params = SubmitDataUseCase.Params(
                imageUri = imageUri.value,
                imageText = visionResult.value,
                latitude = locationResult.value?.latitude,
                longitude = locationResult.value?.longitude,
                distance = dataDistance?.distance,
                duration = dataDistance?.estimatedTime
            )

            val result = submitDataUseCase.run(params)
            _uploadResult.emit(result)
            _isLoading.emit(false)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            dataDistance = null
            _visionResult.emit(null)
            _locationResult.emit(null)
            _uploadResult.emit(Either.fail(Failure.Empty))
            imageAttributesUiModel = null
        }
    }

    fun updateData(attributesModel: ImageAttributesUiModel) {
        imageAttributesUiModel = attributesModel
    }

    fun postUpdateData() {
        imageAttributesUiModel ?: return
        viewModelScope.launch {
            val dataPath =
                (uploadResult.value as? Either.Success)?.value?.referenceId ?: return@launch
            val params = UpdateDataUseCase.Params(
                dataPath = dataPath,
                imageText = imageAttributesUiModel!!.text,
                latitude = imageAttributesUiModel!!.lat,
                longitude = imageAttributesUiModel!!.long,
                distance = imageAttributesUiModel!!.distance,
                duration = imageAttributesUiModel!!.time
            )
            val result = updateDataUseCase.run(params)
            _updateResult.emit(result)
        }
    }
}